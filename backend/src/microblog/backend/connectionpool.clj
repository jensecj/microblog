(ns microblog.backend.connectionpool
  (:require [environ.core :refer [env]]
            [mount.core :refer [defstate]]
            [hikari-cp.core :refer :all]
            [taoensso.timbre :as log]))

(def spec
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :dbtype "postgresql"
   :dbname (env :microblog-database-name)
   :host (env :microblog-database-url)
   :port (read-string (env :microblog-database-port))
   :user (env :microblog-database-user)
   :password (env :microblog-database-password)})

(def docker-spec
  {:dbtype "postgresql"
   :dbname (env :microblog-db-env-postgres-db)
   :host (env :microblog-db-port-5432-tcp-addr)
   :port (env :microblog-db-port-5432-tcp-port)
   :user (env :microblog-db-env-postgres-user)
   :password (env :microblog-db-env-postgres-password)})

(def datasource-options
  {:auto-commit        true
   :read-only          false
   :connection-timeout 30000
   :validation-timeout 5000
   :idle-timeout       600000
   :max-lifetime       1800000
   :minimum-idle       10
   :maximum-pool-size  10
   :pool-name          "db-pool"
   :adapter            "postgresql"
   :register-mbeans    false
   ;; database settings
   :server-name(env :microblog-database-url)
   :database-name (env :microblog-database-name)
   :port-number (read-string (env :microblog-database-port))
   :username (env :microblog-database-user)
   :password (env :microblog-database-password)})

(defstate ConnectionPool
  :start (do
           (log/info "starting database connection pool component")
           {:datasource (make-datasource datasource-options)})
  :stop (do
          (log/info "stopping database connection pool component")
          (close-datasource (:datasource ConnectionPool))))
