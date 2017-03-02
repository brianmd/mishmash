"Intention is to push logging off onto another thread soas
to not hold up calling processes from doing real work"

(println "loading mishmash.log")

(ns mishmash.log
  (:require [clojure.core.async :as a]
            [clojure.pprint :as pp]
            [clojure.tools.logging :as logging]
            [mishmash.event-logger :as event-logger]))

(defonce logging-chan (atom nil))

(defn start
  []
  (reset! logging-chan (a/chan 1024))
  (a/go
    (do
      (loop []
        (when-some [[f args] (a/<! @logging-chan)]
          (try
            (f args)
            (catch Throwable e
              (logging/error e "Error in logging go loop")))
          (recur)))
      (println "exiting logging loop"))))

(defn stop
  []
  (when @logging-chan
    (a/close! @logging-chan)
    (reset! logging-chan nil)))

(defn restart
  []
  (stop)
  (start))

(defn pprint-fn [& args]
  (doseq [a args] (pp/pprint a))
  (last args))

(defn log
  [& args]
  (a/>!! @logging-chan [#(apply println %) args])
  (last args))
(defn logp
  [& args]
  (a/>!! @logging-chan [#(apply pprint-fn %) args])
  (last args))

(defn info
  [& args]
  (a/>!! @logging-chan [#(logging/info %) args])
  (last args))
(defn debug
  [& args]
  (a/>!! @logging-chan [#(logging/debug %) args])
  (last args))
(defn error
  [& args]
  (a/>!! @logging-chan [#(logging/error %) args])
  (last args))

(def log-event event-logger/log)

; (stop)
(restart)

(println "done loading mishmash.log")

