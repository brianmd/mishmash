(ns mishmash.circuit-breaker-test
  (:require [mishmash.circuit-breaker :as sut]
            [clojure.test :refer :all]))

;; note: circuit-breaker timeout resolution is in seconds
;; wouldn't make sense to reset a breaker in less than a second,
;; but takes longer for test.
(sut/define :test-breaker {:timeout 1 :threshold 2}) ;; resets in 1 second

(deftest circuit-breaker
  (is (= 0 (sut/call :test-breaker (fn [] 0))))

  (is (thrown? Exception (sut/call :test-breaker #(throw (Exception. "nope")))))
  (is (thrown? Exception (sut/call :test-breaker #(throw (Exception. "nope")))))
  (is (= 0 (sut/call :test-breaker (fn [] 0))))
  (is (= false (sut/tripped? :test-breaker)))

  (is (thrown? Exception (sut/call :test-breaker #(throw (Exception. "nope")))))
  (is (thrown? Exception (sut/call :test-breaker #(throw (Exception. "nope")))))
  (is (thrown? Exception (sut/call :test-breaker #(throw (Exception. "nope")))))
  (is (thrown? Exception (sut/call :test-breaker (fn [] 0))))
  (is (= true (sut/tripped? :test-breaker)))
  ;; (Thread/sleep 1000)   ;; this takes too long to run frequently!!
  ;; (is (= 0 (sut/call :test-breaker (fn [] 0))))
  )


