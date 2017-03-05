(ns mishmash.event-logger
  (:refer-clojure :exclude [ensure])
  (:require [clojure.pprint :as pp]
            [clojure.core.async :as a]
            [clojure.tools.logging :as logging]
            [mishmash.core :as mm]
            [riemann.client :as r]
            [mishmash.circuit-breaker :as cb]
            [mishmash.conv :as conv]
            ))

(def ^:dynamic *event* {})
(defonce riemann-host (atom (mm/getenv "RIEMANN_HOST")))
(defonce riemann-port
  (atom (conv/->int (mm/getenv "RIEMANN_PORT" 5555))))
(defonce logging-chan (atom nil))
(defonce riemann-repo (atom nil))
(defonce riemann-alternative (atom println))
(cb/define :riemann {:timeout 300 :threshold 2})

(defn use-riemann?
  "returns true if a riemann host has been declared"
  []
  (not (nil? @riemann-host)))

(defmacro with-merged-event
  [options & body]
  `(binding [~'mishmash.event-logger/*event* (merge ~'mishmash.event-logger/*event* ~options)]
     ~@body))
;; (with-merged-event {:a 4} *event*)
;; (with-merged-event {:a 4} (with-event {:b 7} *event*))
;; (with-merged-event {:a 4} (with-event {:b 7 :a 2} *event*))

(defn start-logging
  []
  (reset! logging-chan (a/chan 1024))
  (a/go
    (loop []
      (when-some [[f args] (a/<! @logging-chan)]
        (try
          (f args)
          (catch Throwable e
            (logging/error e "Error in logging go loop")))
        (recur)))
    (println "after logging loop")
    ))

(defn stop-logging
  []
  (when @logging-chan
    (a/close! @logging-chan)
    (reset! logging-chan nil)))

(defn start-riemann
  ([] (start-riemann @riemann-host))
  ([host] (start-riemann host @riemann-port))
  ([host port] (when host
            (reset! riemann-repo (r/tcp-client {:host host :port port})))))
(defn stop-riemann
  []
  (when @riemann-repo
    (r/close! @riemann-repo)
    (reset! riemann-repo nil)))

(defn start
  []
  (start-logging)
  (start-riemann))
(defn stop
  []
  (stop-logging)
  (stop-riemann))
(defn restart
  []
  (stop)
  (start))
(defn ensure
  []
  (when-not @riemann-repo (start)))
(defn print-riemann-error [evt e]
  (println "**************          error communicating with riemann")
  (println "                        printing event here instead")
  (println "                        should probably save to a file though ...")
  (println "    "  evt)
  (throw e)
  )

(defn send-to-riemann
  ([evt] (send-to-riemann @riemann-repo evt))
  ([repo evt]
   (if (use-riemann?)
     (cb/call
      :riemann
      (fn []
        (ensure)
        @(r/send-event repo (update evt :state #(conv/->str %)))))
     (@riemann-alternative (str "event: " (pr-str evt))))))

(defn query-riemann
  ([query] (query-riemann @riemann-repo query))
  ([repo query]
   (cb/call
    :riemann
    (fn [] @(r/query repo query)))))

(defn log
  "log event to riemann"
  [& events]
  (let [evts (doseq [e events] (merge *event* e))]
    (a/>!! @logging-chan [#(doseq [e %] (send-to-riemann e)) evts])
    (last evts)))

(defn log-duration
  ([event f] (log-duration event f (fn [evt result] evt)))
  ([event f log-result-fn]
   (log-duration event f log-result-fn (fn [evt error] evt)))
  ([event f log-result-fn log-error-fn]
   (let [result (atom nil)
         error (atom nil)
         start-time (System/nanoTime)
         ]
     (try
       (reset! result (f))
       (catch Throwable e
         (reset! error e)))
     (let [duration (/ (- (System/nanoTime) start-time) 1e6)]
       (log event
            (if @error
              (log (log-error-fn
                    (merge {:state "critical" :metric duration :error (str @error)} event)
                    @error))
              (log (log-result-fn
                    (merge {:state "ok" :metric duration} event)
                    @result))
              ))
       (if @error
         (throw @error)
         @result)))))

(restart)

;; (send-to-riemann {:service "mishmash" :state "ok" :description "testing" :tags ["speed" "dev"] :metric 41})
;; (send-to-riemann @riemann-repo {:service "mishmash" :state "ok" :description "testing" :tags ["speed" "dev"] :metric 41})
;; (send-to-riemann @riemann-repo {:service "mishmash" :state "critical" :tags ["slow"] :metric 32})
;; (send-to-riemann @riemann-repo {:service "query" :state "critical" :tags ["slow"] :metric 32 })
;; (send-to-riemann @riemann-repo {:service "query" :state "warning" :tags ["slow"] :metric 99})
;; ;; (send-to-riemann @riemann-repo {:service "query" :state "test2" :tags ["slow"] :metric 1099 :critical true})
;; (query-riemann @riemann-repo "host = \"finch\"")
;; (query-riemann @riemann-repo "service = \"mishmash\"")
;; (query-riemann @riemann-repo "service = \"mishmash\" and tagged \"slow\"")
;; ;; (query-riemann @riemann-repo "service = \"mishmash\" and state = \"test\"")

;; (log {:service "mishmash" :state "ok" :description "testing" :tags ["speed" "dev"] :metric 41})
;; (log {:service "query" :state "warning" :tags ["slow"] :metric 99})
;; (query-riemann @riemann-repo "host = \"finch\"")
