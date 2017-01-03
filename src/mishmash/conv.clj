(ns mishmash.conv
  (:require [clojure.string :as str]
            ))


(defn ->str
  "returns string type sans : (keyword) ' (symbol. If keyword, replaces _ with -)"
  [o]
  (condp = true
    (nil? o) ""
    (number? o) (str o)
    (keyword? o) (-> o str (.substring 1) (str/replace "_" "-"))
    true (name o)
    ))

(defn ->keyword [a-string]
  (if (keyword? a-string)
    a-string
    (-> a-string str (str/replace "_" "-") str/lower-case keyword)))

(defn ->int-orig [v]
  (if (nil? v)
    nil
    (if (string? v)
      (let [v (str/trim v)]
        (if (empty? v)
          nil
          (-> v Double/parseDouble int)))
      (int v))))

(defn ->int [o]
  (let [t (type o)]
    (condp = true
      (nil? o) 0
      (= t Integer) o
      (= t Long) o
      (number? o) (-> o Math/round int)
      (string? o)
      (let [v (str/trim o)]
        (if (empty? v)
          0
          (-> v Double/parseDouble Math/round int)))
      true (type o)
      )))

(defn ->long [v]
  (if (nil? v)
    nil
    (if (string? v)
      (let [v (str/trim v)]
        (if (empty? v)
          nil
          (-> v Double/parseDouble long)))
      (int v))))

(defn ->float [v]
  (if (nil? v)
    nil
    (if (string? v)
      (let [v (str/trim v)]
        (if (empty? v)
          nil
          (Double/parseDouble v)))
      (double v))))

(defn as-integer [string]
  (->int string))

