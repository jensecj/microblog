(ns microblog.models.post
  (:require [clojure.java.jdbc :as db]))

(def spec
  {:dbtype "postgresql"
   :dbname "docker"
   :host (or (System/getenv "POSTGRES_PORT_5432_TCP_ADDR") "localhost")
   :port 5432
   :user "postgres"
   :password "secretpw"})

(defn all []
  (into []
        (db/query spec ["SELECT * FROM posts ORDER BY ID DESC"])))

(defn create [post]
  (db/insert! spec :posts [:body] [post]))
