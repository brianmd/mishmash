(ns mishmash.meta-test
  (:require [mishmash.meta :as sut]
            [clojure.test :refer :all]
            ))

(deftest conversion-test
  (testing "find-class"
    (is (= java.lang.String (sut/find-class "java.lang.String")))))

(deftest hierarchy-test
  (testing "superclasses"
    (is (= java.lang.Number (sut/superclass Integer)))
    (is (= [Number Object] (sut/superclasses Integer)))
    ))

;; (deftest method-test
;;   (testing "has correct class method names"
;;     (is (= [] (sut/classMethodNames Integer)))))

