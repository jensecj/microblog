(ns backend.dbmigrations
  (:require [migratus.core :as migratus]
            [taoensso.timbre :as log]))

(defn migrate [connection]
  "Migrate the database using migratus. migratus will automatically apply any
  migrations that have not yet been applied, it skips all others."

  ;; construct the config format migratus expects
  (def config
    {:store         :database
     :migration-dir "migrations/"
     :db            connection})

  (log/info "handling database migrations")

  (migratus/migrate config))
