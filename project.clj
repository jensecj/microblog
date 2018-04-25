(defproject microblog "0.1.0"
  :description "simple, low fidelity microblogging web app"
  :url ""
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"],
                 [org.clojure/java.jdbc "0.7.5"], ;; database connection
                 [org.postgresql/postgresql "42.2.2"] ;; database
                 [ring/ring-core "1.6.3"] ;; ring framework
                 [ring/ring-devel "1.6.3"] ;; more ring
                 [ring/ring-defaults "0.3.1"] ;; good defaults for ring
                 [ring/ring-anti-forgery "1.2.0"] ;; middleware for CSRF prevention
                 [http-kit "2.3.0"] ;; server
                 [compojure "1.6.1"] ;; routing
                 [hiccup "2.0.0-alpha1"]] ;; html rendering
  :main ^:skip-aot microblog.web
  :uberjar-name "microblog-standalone.jar"
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
