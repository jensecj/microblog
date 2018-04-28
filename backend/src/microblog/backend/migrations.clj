(ns microblog.backend.migrations
  (:require
   [clojure.java.jdbc :as db]
   [environ.core :refer [env]]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as log]
   [migratus.core :as migratus]
   ))

(def config {:store                :database
             :migration-dir        "migrations/"
             :init-script          "init.sql"
             ;; defaults to true, some databases do not support
             ;; schema initialization in a transaction
             :init-in-transaction? false
             :migration-table-name "foo_bar"
             :db {:classname   "org.h2.Driver"
                  :subprotocol "h2"
                  :subname     "site.db"}})

(defn- migrated? []
  false)

(defn migrate []
  (when (not (migrated?))
    (log/info "migrating database")))

(defstate Migrate
  :start (do
           (log/info "starting database migration component")
           (migrate))
  :stop (log/info "stopping database migration component"))
