(ns mishmash.circuit-breaker
  (:require [circuit-breaker.core :as cb]))

(defn tripped?
  "returns true if breaker is still closed"
  [breaker-name]
  (cb/tripped? breaker-name))

(defn call
  ([breaker-name f] (call breaker-name f (fn [] (throw (Exception. "::open")))))
  ([breaker-name f err-fn]
   (cb/wrap-with-circuit-breaker breaker-name f err-fn)))

(defn define
  ([breaker-name] (define breaker-name {:timeout 1 :threshold 2}))
  ([breaker-name options]
   (cb/defncircuitbreaker breaker-name options)))

(defn options-for
  "return current options for a breaker"
  [breaker-name]
  (get cb/circuit-breakers-config breaker-name))

(defn failures-for
  "return current number of consecutive failures for a breaker"
  [breaker-name]
  (get cb/circuit-breakers-counters breaker-name))

