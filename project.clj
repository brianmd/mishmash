(defproject com.murphydye/mishmash "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/core.async "0.2.385"]

                 [potemkin "0.4.3"]

                 ;; [org.clojure/data.codec "0.1.0"]
                 [cheshire "5.6.3"]   ;; json

                 [com.taoensso/timbre "4.8.0"] ;; logging for clojure/script

                 [clj-http "2.3.0"]
                 [clj-time "0.13.0"]
                 [com.rpl/specter "0.13.2"]

                 [org.clojure/core.memoize "0.5.8"]

                 [incanter "1.5.7"]
                 [manifold "0.1.5"]
                 [funcool/cats "2.0.0"]

                 [riemann-clojure-client "0.4.4"]
                 [circuit-breaker "0.1.8"]
                 ])
