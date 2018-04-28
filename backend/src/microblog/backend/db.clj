(ns microblog.backend.db
  (:require
   [clojure.java.jdbc :as db]
   [environ.core :refer [env]]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as log]
   [microblog.backend.connectionpool :refer [ConnectionPool]]
   [microblog.backend.migrations :refer [Migrate]]
   ))

(defn add-post [post]
  (db/insert! ConnectionPool :posts [:body] [post]))

(defn get-all-posts []
  (into []
        (db/query ConnectionPool ["SELECT * FROM posts ORDER BY ID DESC"])))

(defn get-posts-by-page [page]
  (into []
        (db/query ConnectionPool ["SELECT * FROM posts ORDER BY ID DESC OFFSET ? LIMIT 3" (* page 3)])))

(defstate Database
  :start (do
           (log/info "starting database component")
           (log/info (format "backend connecting to database: %s" ConnectionPool)))
  :stop (log/info "stopping database component"))

(defn -main []
  (comment
    ;; posts-down
    (db/execute! ConnectionPool
                 (db/drop-table-ddl :posts))

    ;; posts-up
    (db/db-do-commands ConnectionPool
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
