(defproject microblog "0.1.0-SNAPSHOT"
  :description "simple, low fidelity microblogging platform"
  :url ""
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot microblog.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
