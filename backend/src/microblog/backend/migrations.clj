(ns microblog.backend.migrations
  (:require
   [clojure.java.jdbc :as db]
   [environ.core :refer [env]]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as log]
   [migratus.core :as migratus]
   [microblog.backend.connectionpool :refer [ConnectionPool]]
   ))

(defn- migrated? []
  false)

(defn migrate []
  (when (not (migrated?))
    (def config
      {:store         :database
       :migration-dir "migrations/"
       :db            ConnectionPool})

    (log/info "migrating database")
    (migratus/migrate config)))

(defstate Migrate
  :start (do
           (log/info "starting database migration component")
           (migrate))
  :stop (log/info "stopping database migration component"))
