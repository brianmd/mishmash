(ns mishmash.core
  (:require [potemkin.namespaces :as pot]
            [mishmash.conv :as conv]
            ))

(defn import-ns-vars
  [ns-symbol]
  (pot/import-vars [])
  )
;; (pot/import-vars [conv ->str])
;; (->str 3)
;; for debugging examples
;; (defmacro examples [& forms]
;;   `(do ~@forms))
(defmacro examples [& forms]
  )

(defmacro assert= [& args]
  `(assert (= ~@args)))
(defmacro assert-= [& args]
  `(assert (= ~@args)))
(defmacro assert-false
  ([x] `(assert (clojure.core/not ~x)))
  ([x message] `(assert (clojure.core/not ~x) message)))

(examples
 (assert= nil (->int nil) (->int "    "))
 (assert= 34 (->int "   34") (->int "   000000034")))

(defmacro defn-memo [name & body]
  `(def ~name (memoize (fn ~body))))

(def map! (comp doall map))
(def maprun (comp dorun map))

(defn third [s]
  (first (next (next s))))

(defn any [s]
  (nth s (rand-int (count s))))

(defn detect
  ([predicate] (partial detect predicate))
  ([predicate coll]
   (some #(if (predicate %) %) coll)))
(examples
 (assert= 9 (detect #(> % 5) [2 9 4 7]) ((detect #(> % 5)) [2 9 4 7]))
 (assert= 6 (detect #(> % 5) (range)) ((detect #(> % 5)) (range)))
 )

(defn dissoc-key
  "inteded to allow point-free calls"
  ([key] (fn [m] (dissoc m key)))
  ([key m] (dissoc m key)))

;; (defmacro defn-memo [name & body]
;;   `(def ~name (memoize (fn ~body))))
