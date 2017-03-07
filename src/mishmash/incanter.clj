(ns mishmash.incanter
  (:require [incanter.core :as i]
            ))

(defmulti to-maps
  "Convert to sequence of maps"
  type)

(defmethod to-maps :incanter.core/dataset
  ([dataset]
   (let [colnames (i/col-names dataset)
         data (i/to-vect dataset)]
     (map #(zipmap colnames %) data))))

