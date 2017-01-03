(ns mishmash.core-test
  (:require [clojure.test :refer :all]
            [mishmash.core :refer :all]))

(deftest map!-test
  (let [plain (map (partial + 1) [1 2 3])
        ran   (map! (partial + 1) [1 2 3])]
    (is (not (realized? plain)))
    (is      (realized? ran))
    ))

(deftest detect-test
  (are [x y] =
    9   (detect #(> % 5) [2 9 4 7])
    6   (detect #(> % 5) (range)) ;; works lazily
    nil (detect #(> % 5) [2 9 4 7])
    ))

(deftest dissoc-key-test
  (is (= [{} {:b 4} {:b 3}] (map (dissoc-key :a) [{:a 7} {:a 9 :b 4} {:b 3}]))))

;; (defn-memo adder [& args]
;;   (apply + args))
