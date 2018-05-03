(ns backend.db
  (:require [clojure.java.jdbc :as db]
            [backend.connectionpool :refer [ConnectionPool]]
            [backend.dbprotocol :as dbprotocol]
            [mount.core :refer [defstate]]
            [migratus.core :as migratus]
            [hugsql.core :as hugsql]
            [backend.schema :as s]
            [taoensso.timbre :as log]
            [backend.dbmigrations :as dbm]))

(defn- sql-get-posts-by-offset [connection n offset]
  (into [] (db/query connection ["SELECT * FROM posts ORDER BY ID DESC OFFSET ? LIMIT ?" (* offset n) n])))
(defn- sql-get-user-by-name [connection username]
  (first (db/query connection ["SELECT username,hash,id FROM users WHERE username = ?" username])))
(defn- sql-get-user-by-id [connection user_id]
  (first (db/query connection ["SELECT username,hash,id FROM users WHERE id = ?" user_id])))
(defn sql-create-user [connection username hash]
  (db/insert! connection :users {:username username :hash hash}))
(defn- sql-add-post [connection user new-post]
  (db/insert! connection :posts {:created_by (:id user) :body new-post}))
(defn- sql-get-all-posts [connection]
  (into [] (db/query connection ["SELECT * FROM posts ORDER BY ID DESC"])))


(defn- user-id-to-name [connection user_id]
  (let [username (:username (sql-get-user-by-id connection user_id))]
    username))

(defn- change-post-created-by-id-to-name [connection post]
  (let [user_id (:created_by post)
        username (:username (sql-get-user-by-id connection user_id))]
    (-> post
        (dissoc :created_by)
        (assoc :created_by username))))

(defn- add-creator-avatar [connection post]
  (let [user (sql-get-user-by-name connection (:username post))]
    (assoc post :creator_avatar
           (rand-nth '("http://www.avatarsdb.com/avatars/hidden_cat.jpg"
                       "http://www.avatarsdb.com/avatars/dennis.jpg"
                       "http://www.avatarsdb.com/avatars/funny_penguin.gif"
                       "http://www.avatarsdb.com/avatars/playful_cat.gif")))))

(defn- wrap-post [connection post]
  (->> post
       (change-post-created-by-id-to-name connection)
       (add-creator-avatar connection)))


(map (partial wrap-post (:connection Database))
     '({:id 1 :body "something" :created_by 1 :created_at 23}
       {:id 2 :body "other thing" :created_by 2 :created_at 81923}))

(defn wrap-add-post [connection user new-post]
  (sql-add-post connection user new-post)
  ;; (let [validation (s/check (s/pred post-body?) new-post)]
  ;;   (if (= validation nil)
  ;;     (let [user (sql-get-user-by-name connection (:username user))]
  ;;       )))
  )

(defn wrap-get-all-posts [connection]
  (let [raw-posts (sql-get-all-posts connection)]
    (map (partial wrap-post connection) raw-posts)))




;; import queries as clojure code into this namespace, based on
;; the SQL from queries.sql
(hugsql/def-db-fns "queries.sql")

(defrecord PostgreSQL-DB [connection]
  backend.dbprotocol/DbActions

  (add-post [this user new-post]
    (sql-add-post connection user new-post))

  (get-all-posts [this]
    (sql-get-all-posts connection))

  (get-posts-by-offset [this n offset]
    (sql-get-posts-by-offset connection n offset))

  (get-user-by-name [this username]
    (sql-get-user-by-name connection username))
  (get-user-by-id [this user_id]
    (sql-get-user-by-id connection user_id))

  (create-user [this username hash]
    (sql-create-user connection username hash))
  )

(defstate Database
  :start (do
           (log/info "starting database component")

           (dbm/migrate ConnectionPool)

           (log/info (format "backend connecting to database: %s" ConnectionPool))

           ;; construct the component that is going to be returned, this is what
           ;; is injected when ':refer [Database]' is used.
           (->PostgreSQL-DB ConnectionPool))
  :stop (log/info "stopping database component"))
