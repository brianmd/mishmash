(ns mishmash.meta
  )

;; Note:
;;  - getDeclared... gets all public and private ...
;;  - get... gets only public ... (and protected if the calling class has access)

;; mishmash
;; mishmash.meta/find-class
;; mishmash.meta/find-class


(defn getNames [coll]
  (map #(.getName %) coll))



(defn find-class [klass-str]
  (Class/forName klass-str))

;; (.getMethod Integer "+")

(defn superclass [klass]
  (.getSuperclass klass))

(defn superclasses
  ([klass] (superclasses klass []))
  ([klass v]
   (loop [klass klass v v]
     (let [superklass (superclass klass)]
       (if (nil? superklass)
         v
         (recur superklass (conj v superklass)))))))

(defn subclasses
  [klass]
  )

;; see http://www.javaworld.com/article/2077015/java-se/take-an-in-depth-look-at-the-java-reflection-api.html

(defn classMethods [klass]
  (.getDeclaredMethods klass))

(defn allPublicMethods [klass]
  (.getMethods klass))

;; (class 3)
;; (.getInterfaces Integer)
;; (-> (.getInterfaces Integer) getNames)
;; (-> (.getInterfaces Number) getNames)
;; (-> (.getMethods Comparable) getNames)

(defn functionsInPackage [pkg]
  )

;; (methods Integer)
;; (methods 3)
;; (.getFields Integer)
;; (-> (.getFields Integer) getNames)
;; (->> (.getFields Integer) (map #(.getType %)))
;; (classMethods Object)
;; (classMethods Integer)
;; (-> (allPublicMethods Integer) getNames sort)
;; (classMethodNames Integer)
;; (classMethodNames Object)
;; (-> (classMethods Object) type)

(defn classMethodNames [klass]
  (sort
   (map #(.getName %) (classMethods klass))))

(defn methodNames [obj]
  (sort
   (map #(.getName %)
        (.getDeclaredMethods
         (type obj)))))

(defn allMethodNames [obj]
  (sort
   (reduce (fn [accum o] (concat accum (map #(.getName %) (.getDeclaredMethods o)))) [] (conj (disj (supers (type obj)) java.lang.Object) (type obj)))))



