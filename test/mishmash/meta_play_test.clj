(ns mishmash.meta-play-test
  (:require [mishmash.meta :as sut]
            [clojure.test :refer :all]
            ))

;; see http://www.javaworld.com/article/2077015/java-se/take-an-in-depth-look-at-the-java-reflection-api.html?page=2


;; TODO: for finding subclasses: https://github.com/lukehutch/fast-classpath-scanner



(defn getNames [coll]
  (map #(.getName %) coll))



(deftest interface-test
  (are [x y] (= x y)
    (-> (.getInterfaces Integer) getNames) ["java.lang.Comparable"]
    (-> (.getInterfaces Number) getNames) ["java.io.Serializable"]
    (-> (.getMethods Comparable) getNames) ["compareTo"]
    ))


;;   meta
;;   https://en.wikibooks.org/wiki/Learning_Clojure/Meta_Data

(def x ^{:doc :meta-test} {:a 7})
(def y x)
(def z ^{:type :xyz} ^{:x 4} {:q 9})
(def y1 ^:test-flag ^{:x 4} {:r 4})
(def z2 ^{:type :xyz} ^{:x 4} y)
(def zz (with-meta y (merge (meta y) {:zz :yep})))

(def ^{:version 1} doc1 "This is text")
(def ^{:version 1} doc2 "This is text")
(alter-meta! #'doc2 #(update-in % [:version] inc))

;; (defn xy [])
;; (clojure.repl/source xy)
;; (clojure.repl/source meta)


(deftest meta-test
  (are [x y] (= x y)
    {:a 7} x
    {:doc :meta-test} (meta x)
    ;; {:doc :meta-test} (meta #'x)
    {:doc :meta-test} (meta y)
    {:type :xyz :x 4} (meta z)
    {:test-flag true :x 4} (meta y1)
    {:doc :meta-test :zz :yep} (meta zz)

    ;;   surpising !!!
    {:doc :meta-test} (meta z2)

    1 (:version (meta #'doc1))  ;; includes line-num, file, etc.
    nil (meta doc1) ;; meta was set on variable, not object
    2 (:version (meta #'doc2))
    ))

;; (meta x)
;; (meta y)
;; (meta z)
;; (meta zz)
;; (meta (with-meta y (merge (meta y) {:zz :yep})))
;; (meta ^{:zz :yep} y)
;; (meta (meta y) ^{:zz :yep} y)


