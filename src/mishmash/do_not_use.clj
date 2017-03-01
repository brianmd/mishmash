(ns mishmash.do-not-use)

;; use only in extraordinary circumstances ....

;; helper functions to enable #'apply work for logging macros
;; see http://stackoverflow.com/questions/9273333/in-clojure-how-to-apply-a-macro-to-a-list
(defmacro functionize [macro]
  `(fn [& args#] (eval (cons '~macro args#))))
(defmacro apply-macro [macro args]
  `(apply (functionize ~macro) ~args))
