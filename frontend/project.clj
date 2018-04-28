(defproject microblog.frontend "0.1.0"
  :description "frontend for microblogging web app"
  :url ""
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-devel "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-anti-forgery "1.2.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [environ "1.1.0"]
                 [http-kit "2.3.0"]
                 [compojure "1.6.1"]
                 [hiccup "2.0.0-alpha1"]
                 [clj-http "3.8.0"]
                 ]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler microblog.frontend.web/app}
  :main ^:skip-aot microblog.frontend.web
  :jvm-opts ["-Xmx1g"]
  :uberjar-name "microblog-frontend-standalone.jar"
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
