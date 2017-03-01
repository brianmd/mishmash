(println "loading mishmash.log")

(ns mishmash.log
  (:require [clojure.pprint :as pp]
            [clojure.core.async :as a]
            [clojure.tools.logging :as logging]

            ;; [cats.core :as m]
            ;; [cats.labs.manifold :as mf]
            ;; [cats.builtin]
            ;; [cats.monad.maybe :as maybe]

            ;; [manifold.deferred :as d]
            ;; [manifold.stream :as s]
            ;; [manifold.executor :as e]
            ))

(defn start
  []
  (def logging-chan (a/chan 1024))
  (a/go
    (do
      (loop []
        (when-some [[f args] (a/<! logging-chan)]
          (try
            (f args)
            (catch Throwable e
              (logging/error e "Error in logging go loop")))
          (recur)))
      (println "exiting logging loop"))))

(defn stop
  []
  (a/close! logging-chan))

(defn pprint-fn [& args]
  (doseq [a args] (pp/pprint a))
  (last args))

(defn log
  [& args]
  (a/>!! logging-chan [#(apply println %) args])
  (last args))
(defn logp
  [& args]
  (println "---fd mefjj")
  (a/>!! logging-chan [#(apply pprint-fn %) args])
  (last args))

(defn info
  [& args]
  (a/>!! logging-chan [#(logging/info %) args])
  ;; (a/>!! logging-chan [(fn [v] (apply-macro logging/info v)) args])
  (last args))
(defn debug
  [& args]
  (a/>!! logging-chan [#(logging/debug %) args])
  (last args))
(defn error
  [& args]
  (a/>!! logging-chan [#(logging/error %) args])
  (last args))

;; (log 3 ["hey" (range 50)] "done")
;; (plog 3 ["hey" (range 50)] "done")
;; (info 3 ["hey" (range 50)] "donee")
;; (dotimes [n 1000]
;;   (error n))
;; (a/close! logging-chan)




;; (defn async-call
;;     "A function that emulates some asynchronous call."
;;   [n]
;;   (d/future
;;     (println "---> sending request" n)
;;     (Thread/sleep n)
;;     (println "<--- receiving request" n)
;;     n))

;; (time
;;  @(m/mlet [x (async-call 2002)
;;            y (async-call 1001)]
;;           (m/return (+ x y))))
;; (def zz
;;   (time
;;    (m/mlet [x (async-call 2002)
;;             y (async-call 1001)]
;;            (m/return (+ x y)))))

;; (m/mappend [1 2 3] [4 5 6])
;; (type (m/mappend [1 2 3] [4 5 6]))

;; (m/mappend (maybe/just [1 2 3])
;;            (maybe/just [4 5 6]))
;; (maybe/just 2)
;; (m/fmap inc (maybe/just 1))
;; (m/fmap inc (maybe/nothing))
;; (type (map inc [1 2 3]))
;; (type (m/fmap inc [1 2 3]))


;; (def ss (s/stream 5))
;; (doseq [x (range 6)]
;;   (s/try-put! ss x 100))
;; (s/try-take! ss ::drained 100 ::timedout)


;; (def d (d/deferred))
;; (d/success! d :foo)
;; @d

;; (def executor (e/fixed-thread-executor 42))

;; (-> (d/future 1)
;;     (d/onto executor)
;;     (d/chain inc inc inc))

;; (->> (s/->source (range 1e3))
;;      (s/onto executor)
;;      (s/map inc))



;; (defn slow-prn
;;   [x]
;;   (prn x)
;;   (Thread/sleep 100))
;; (defn deferred-slow-prn
;;   [x]
;;   (let [d (d/deferred)]
;;     (d/chain d #(d/future (slow-prn %)))
;;     (d/success! d x)))
;; ;; (slow-prn :hey)

;; (def print-stream (s/buffered-stream 100))
;; (def async-print-stream (s/onto (e/executor) print-stream))
;; (s/consume #(slow-prn %) async-print-stream)
;; (s/consume #(deferred-slow-prn %) async-print-stream)
;; (def zz (doall (map #(s/put! print-stream %) (range 10))))

;; (def zz (map #(s/put! print-stream %) (range 10)))

;; @(d/timeout!
;;  (d/future (Thread/sleep 1000) :yep)
;;  100
;;  :nope)
;; (d/timeout!
;;  (d/deferred)
;;  1000
;;  (do (Thread/sleep 2000) (prn 7)))


;; (def d (d/deferred))
;; (d/chain d inc inc inc #(println "x + 3 =" %))
;; (d/success! d 7)
;; @d



;; (s/consume #(slow-prn %) print-stream)
;; (s/consume-async #(slow-prn %) print-stream)
;; (s/take! print-stream)
;; (s/map slow-prn print-stream)
;; ;; (s/downstream print-stream)
;; @(s/put! print-stream :boo)
;; (doall (map #(s/put! print-stream %) (range 10)))
;; (def zz
;;   (doall
;;    (map #(s/put! print-stream %) (range 10))))
;; zz


;; (do
;;   (->> (s/->source (range 1e2))
;;        (s/onto executor)
;;        (s/map inc)
;;        ;; (s/take! )
;;        (s/consume slow-prn)
;;        )
;;   7)



;; (def ss (s/stream))
;; (def a (s/map inc ss))
;; (def b (s/map dec ss))
;; @(s/put! ss 0)
;; @(s/put! ss 5)
;; @(s/take! a)
;; @(s/take! b)


;; (defn- create-soon-chan []
;;   (chan 30))
;; (defn- create-slow-chan []
;;   (chan (sliding-buffer 3)))

;; (def ^:private log-chan (atom (create-soon-chan)))
;; (def ^:private log-slowly-chan (atom (create-slow-chan)))
;; (def log-delay 500)

;; (defn- log
;;   [args]
;;   (binding [clojure.pprint/*print-miser-width* 120
;;             clojure.pprint/*print-right-margin* 120]
;;     (doseq [arg args] (pprint arg))))

;; ;; (defn- log [args]
;; ;;   (try
;; ;;     (apply println args)
;; ;;     (catch Exception e)
;; ;;     ))

;; (defn- log-loop [chan-atom post-log-fn]
;;   (go-loop []
;;     (let [args (<! @chan-atom)]
;;       (when args
;;         (log args)
;;         (post-log-fn)
;;         (recur))
;;       )))

;; (defn- chan-open? [c]
;;   ;; Without keeping state, seems to be no other way to determine if channel is
;;   ;; open other than writing to it.
;;   (>!! c "."))

;; (defn- start-loop [chan-atom create-chan-fn post-log-fn]
;;   (when-not (chan-open? @chan-atom)
;;     (reset! chan-atom (create-chan-fn))
;;     (log-loop chan-atom post-log-fn)
;;     ))

;; (defn start []
;;   (println "starting log-loops")
;;   (start-loop log-chan create-soon-chan #())
;;   (start-loop log-slowly-chan create-slow-chan #(Thread/sleep log-delay)))

;; (defn stop []
;;   (println "stopping logging")
;;   (close! @log-chan)
;;   (close! @log-slowly-chan)
;;   )

;; (stop)
;; (start)


;; ;; These are the primary interface functions:

;; (defn log-soon [& args]
;;   (when-not (>!! @log-chan args)
;;     (log args))
;;   (last args))
;; ;; (>!! @log-chan [3 4 5])
;; ;; (<!! @log-chan)

;; (defn log-slowly [& args]
;;   (when-not (>!! @log-slowly-chan args)
;;     (log args))
;;   (last args))



;; ;; (log-soon 3 4)
;; ;; (log-slowly 5 6)

;; ;; (dotimes [n 50]
;; ;;   (log-soon n 4 (rand)))
;; ;; (dotimes [n 50]
;; ;;   (log-slowly n 4 (rand)))

(println "starting log loop")
;; (stop)
(start)

(println "done loading mishmash.log")

