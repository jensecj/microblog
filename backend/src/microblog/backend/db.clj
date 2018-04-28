(ns microblog.backend.db
  (:require
   [clojure.java.jdbc :as db]
   [environ.core :refer [env]]
   [mount.core :refer [defstate]]
   ))

(def spec
  {:dbtype "postgresql"
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

(defn add-post [post]
  (db/insert! spec :posts [:body] [post]))

(defn get-all-posts []
  (into []
        (db/query spec ["SELECT * FROM posts ORDER BY ID DESC"])))

(defn get-posts-by-page [page]
  (into []
        (db/query spec ["SELECT * FROM posts ORDER BY ID DESC OFFSET ? LIMIT 3" (* page 3)])))

(defstate Database
  :start (do
           (println "starting database component")
           (println (format "backend connecting to database with: %s" spec)))
  :stop (println "stopping database component"))

(defn -main []
  (comment
    ;; posts-down
    (db/execute! spec
                 (db/drop-table-ddl :posts))

    ;; posts-up
    (db/db-do-commands spec
                       (db/create-table-ddl
                        :posts
                        [
                         [:id :serial "PRIMARY KEY"]
                         [:body :varchar "NOT NULL"]
                         [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
                         ]))

    (add-post "and then there was more")
    (get-all-posts)
    (get-posts-by-page 0)
    )
  )

(defn- migrated? []
  false)

(defn migrate []
  (when (not migrated?)
    (println "migrating")))
