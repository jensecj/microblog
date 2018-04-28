(defproject microblog.backend "0.1.0"
  :description "backend for microblogging web app"
  :url ""
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [environ "1.1.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [mount "0.1.12"]
                 [http-kit "2.3.0"]
                 [migratus "1.0.6"] [com.fzakaria/slf4j-timbre "0.3.2"]
                 [com.layerware/hugsql "0.4.8"]
                 [org.postgresql/postgresql "42.2.2"]
                 [metosin/compojure-api "2.0.0-alpha19"]
                 [prismatic/schema "1.1.9"]
                 ]
  :plugins [[lein-ring "0.12.4"]
            [lein-environ "1.1.0"]]
  :ring {:handler microblog.backend.server/app
         :auto-reload? true
         :auto-refresh? true
         }
  :main ^:skip-aot microblog.backend.server
  :jvm-opts ["-Xmx1g"]
  :target-path "target/%s"
  :uberjar-name "microblog-backend-standalone.jar"
  :profiles {:uberjar {:aot :all}}
  )
