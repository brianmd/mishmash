(ns mishmash.circuit-breaker
  (:require [clojure.pprint :as pp]
            [clojure.core.async :as a]
            [clojure.tools.logging :as logging]
            [riemann.client :as r]
            [circuit-breaker.core :as cb]
            ;; [circuity.core :as cb]
            ;; [circuit-breaker.concurrent-map :as cbcm]

            ;; [cats.core :as m]
            ;; [cats.labs.manifold :as mf]
            ;; [cats.builtin]
            ;; [cats.monad.maybe :as maybe]

            ;; [manifold.deferred :as d]
            ;; [manifold.stream :as s]
            ;; [manifold.executor :as e]
            ))

(defn tripped?
  "returns true if breaker is still closed"
  [breaker-name]
  (cb/tripped? breaker-name))

(defn call
  [breaker-name f]
  (cb/wrap-with-circuit-breaker breaker-name f (fn [] (throw (Exception. "::open")))))

;; (defn run
;;   [breaker & rest]
;;   (let [result (apply cb/wrap-with-circuit-breaker breaker rest)]
;;     (if (and (nil? result) (not (tripped? breaker)))
;;       (throw (Exception. "::circuit-breaker is open"))
;;       result)))

(defn define
  ([breaker-name] (define breaker-name {:timeout 1 :threshold 2}))
  ([breaker-name options]
   (cb/defncircuitbreaker breaker-name options)))

