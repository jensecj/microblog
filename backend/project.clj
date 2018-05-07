(defproject backend "0.1.0"
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
                 [hikari-cp "2.4.0"]
                 [ring/ring-mock "0.3.2"]
                 [ring-cors "0.1.12"]
                 [cheshire "5.8.0"]
                 [buddy "2.0.0"]]

  :plugins [[lein-environ "1.1.0"]
            [migratus-lein "0.5.7"]]

  :migratus {:store :database
             :migration-dir "migrations/"
             :db ~(format "postgres://%s:%s/%s?user=%s&password=%s"
                          (System/getenv "MICROBLOG_DATABASE_URL")
                          (System/getenv "MICROBLOG_DATABASE_PORT")
                          (System/getenv "MICROBLOG_DATABASE_NAME")
                          (System/getenv "MICROBLOG_DATABASE_USER")
                          (System/getenv "MICROBLOG_DATABASE_PASSWORD"))}

  :main ^:skip-aot backend.server
  :jvm-opts ["-Xmx1g"]
  :target-path "target/%s"
  :uberjar-name "microblog-backend-standalone.jar"
  :profiles {:uberjar {:aot :all}})
