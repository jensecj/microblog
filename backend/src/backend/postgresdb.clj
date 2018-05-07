(ns backend.postgresdb
  (:require [clojure.java.jdbc :as db]
            [schema.core :as schema]
            [taoensso.timbre :as log]

            [backend.schema :as s]
            [backend.dbprotocol :as dbprotocol]
            ))

;; SQL queries, TODO: replace with hugsql
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


;; helpers for request wrappers
(def fake-avatar-list
  ["http://www.avatarsdb.com/avatars/funny_penguin.gif"
   "http://www.avatarsdb.com/avatars/alias_bad_robot.gif"
   "http://www.avatarsdb.com/avatars/funny_confused_frog.gif"
   "http://www.avatarsdb.com/avatars/hidden_cat.jpg"
   "http://www.avatarsdb.com/avatars/dennis.jpg"
   "http://www.avatarsdb.com/avatars/playful_cat.gif"])

(defn random-avatar [user_id]
  (nth fake-avatar-list (- user_id 1)))

(defn add-avatar-url [user]
  (assoc user :avatar_url (random-avatar (:id user))))

;; wrappers for incoming requests
(defn- wrap-get-user-by-name
  "Get a user by their name"
  [connection username]
  (some-> (sql-get-user-by-name connection username)
          (add-avatar-url)))

(defn- wrap-get-user-by-id
  "Get a user by their ID"
  [connection user_id]
  (some-> (sql-get-user-by-id connection user_id)
          (add-avatar-url)))

(defn- wrap-create-user [connection username hash]
  (sql-create-user connection username hash))

(defn- wrap-post [connection post]
  (let [user (wrap-get-user-by-id connection (:created_by post))]
    (assoc post :created_by (dissoc user :hash))))

(defn- wrap-get-all-posts
  "Get all available posts"
  [connection]
  (let [raw-posts (sql-get-all-posts connection)]
    (map (partial wrap-post connection) raw-posts)))

(defn- wrap-get-posts-by-offset
  "Get n posts, starting at offset"
  [connection n offset]
  (let [raw-posts (sql-get-posts-by-offset connection n offset)]
    (map (partial wrap-post connection) raw-posts)))

(defn- wrap-add-post
  "Validate that the incoming post conforms to spec, then attempt to create it."
  [connection current-user new-post]
  (let [validation (schema/check (schema/pred s/post-body?) new-post)]
    (if (= validation nil)
      (let [user (wrap-get-user-by-name connection (:username current-user))]
        (sql-add-post connection user new-post)))))

(defrecord PostgresDb [connection]
  dbprotocol/PostActions
  (add-post [this user new-post]
    (wrap-add-post connection user new-post))
  (get-all-posts [this]
    (wrap-get-all-posts connection))
  (get-posts-by-offset [this n offset]
    (wrap-get-posts-by-offset connection n offset))

  dbprotocol/UserActions
  (get-user-by-name [this username]
    (wrap-get-user-by-name connection username))
  (get-user-by-id [this user_id]
    (wrap-get-user-by-id connection user_id))
  (create-user [this username hash]
    (wrap-create-user connection username hash)))
