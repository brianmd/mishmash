(ns mishmash.circuit-breaker
  (:require [circuit-breaker.core :as cb]))

(defn tripped?
  "returns true if breaker is still closed"
  [breaker-name]
  (cb/tripped? breaker-name))

(defn call
  [breaker-name f]
  (cb/wrap-with-circuit-breaker breaker-name f (fn [] (throw (Exception. "::open")))))

(defn define
  ([breaker-name] (define breaker-name {:timeout 1 :threshold 2}))
  ([breaker-name options]
   (cb/defncircuitbreaker breaker-name options)))

