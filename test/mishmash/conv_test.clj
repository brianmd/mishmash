(ns mishmash.conv-test
  (:require [mishmash.conv :as sut]
            [clojure.test :refer :all]))


(deftest str-conversion-test
  (are [x y] (= x (sut/->str y))
    ""        nil
    "abc"     "abc"
    "abc"     :abc
    "abc"     'abc
    "abc/def" :abc/def
    "3"       3
    "abc-def" :abc_def
    ))

(deftest keyword-conversion-test
  (are [x y] (= x (sut/->keyword y))
    :abc :abc
    :abc "abc"
    :abc 'abc
    ))

(deftest int-conversion-test
  (are [x y] (= x (sut/->int y))
    0 nil
    3 3
    3 3.2
    4 3.6
    3 "3"
    3 "3.2"
    4 "3.6"
    ;; 3 :3.2
    ;; 4 :3.6
    ))
