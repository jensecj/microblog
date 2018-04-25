(ns microblog.db
  (:require [clojure.java.jdbc :as db]))

(def pg-db
  {:dbtype "postgresql"
   :dbname "docker"
   :host (or (System/getenv "POSTGRES_PORT_5432_TCP_ADDR") "localhost")
   :port 5432
   :user "postgres"
   :password "secretpw"})

(defn get-by-name [db username]
  (db/query pg-db ["SELECT * FROM users WHERE username = ?" username]))

(defn get-by-id [db id]
  db/query pg-db ["SELECT * FROM users WHERE id = ?" id])

(defn add-user [db username password-hash]
  (db/insert! pg-db
              :users {:username username, :password password-hash}))

(defn -main []
  ;; users-down
  (db/execute! pg-db
               (db/drop-table-ddl :users))

  ;; users-up
  (db/execute! pg-db
               (db/create-table-ddl
                :users
                [[:username :text]]))
  ;; users-add
  (db/insert! pg-db
              :users {:username "jens"})

  ;; users-get-by-name
  (get-by-name pg-db "jens"))
