(ns microblog.db
  (:require [clojure.java.jdbc :as db]))

(def pg-db
  {:dbtype "postgresql"
   :dbname "docker"
   :host (or (System/getenv "POSTGRES_PORT_5432_TCP_ADDR") "localhost")
   :port 5432
   :user "postgres"
   :password "secretpw"})

(db/execute! pg-db
             (db/drop-table-ddl :cities))

(db/execute! pg-db
             (db/create-table-ddl
              :cities
              [[:name :text]]))

(db/insert! pg-db
            :cities {:name "Copenhagen"})

(db/insert-multi! pg-db
                  :cities
                  [{:name "Aarhus"}
                   {:name "New York"}])

(let [cities (db/query pg-db
                       ["select * from cities"])]
  (println cities))
