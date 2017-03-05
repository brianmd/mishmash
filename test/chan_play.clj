(ns chan-play
  (:refer-clojure :exclude [ensure])
  (:require [clojure.pprint :as pp]
            [clojure.core.async :as a]
            [clojure.tools.logging :as logging]
            [riemann.client :as r]
            [mishmash.circuit-breaker :as cb]
            [clojure.test :refer :all]
            ))

(defn xform [x]
  ;; (odd? x)
  (/ 100 x)
  )
(let [c (a/chan 1 (map xform) (fn [e] (println (type e)) (println e) "got error :("))]
  (future
    (a/>!! c 23)
    (a/>!! c 0)
    (a/>!! c 25)
    )
  (future
    (println "\ngot: " (a/<!! c))
    (println "got: " (a/<!! c))
    (println "got: " (a/<!! c))
    )
  (future
    (Thread/sleep 500)
    (a/close! c)))


