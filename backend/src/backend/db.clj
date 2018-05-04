(ns backend.db
  (:require [backend.connectionpool :refer [ConnectionPool]]
            [mount.core :refer [defstate]]
            [migratus.core :as migratus]
            [hugsql.core :as hugsql]
            [taoensso.timbre :as log]
            [backend.postgresdb :as postgres]
            [backend.dbmigrations :as dbm]))

(defstate Database
  :start (do
           (log/info "starting database component")

           ;; import queries as clojure code into this namespace, based on
           ;; the SQL from queries.sql
           (hugsql/def-db-fns "queries.sql")

           ;; handle database migrations, automatically applies new migrations.
           (dbm/migrate ConnectionPool)

           (log/info (format "backend connecting to database: %s" ConnectionPool))

           ;; construct the component that is going to be returned, this is what
           ;; is injected when ':refer [Database]' is used.
           (postgres/->PostgreSQL-DB ConnectionPool))
  :stop (log/info "stopping database component"))
