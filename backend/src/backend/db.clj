(ns backend.db
  (:require [clojure.java.jdbc :as db]
            [backend.connectionpool :refer [ConnectionPool]]
            [backend.dbprotocol :as dbprotocol]
            [mount.core :refer [defstate]]
            [migratus.core :as migratus]
            [hugsql.core :as hugsql]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(defn post-body? [post]
  (and (s/validate s/Str post)
       (<= (count post) 200)
       (> (count post) 0)))

(s/defschema Post
  {:id s/Int
   :created_by s/Str
   :body s/Str
   :created_at s/Inst
   :creator_avatar s/Str
   })

(s/defschema InternalPost
  {:id s/Int
   :created_by s/Str
   :body s/Str
   :created_at s/Inst
   })

(s/defschema User
  {:id s/Int
   :username s/Str
   :hash s/Str
   :avatar-url s/Str})

(defn- migrate [connection]
  "Migrate the database using migratus"

  ;; construct the config format migratus expects
  (def config
    {:store         :database
     :migration-dir "migrations/"
     :db            connection})

  (log/info "handling database migrations")
  ;; migratus will automatically apply any migrations that have
  ;; not yet been applied, it skips all others.
  (migratus/migrate config))

;; import queries as clojure code into this namespace, based on
;; the SQL from queries.sql
(hugsql/def-db-fns "queries.sql")

;; helpers
(defn- user-id-to-name [connection user_id]
  (let [username (:username (handle-get-user-by-id connection user_id))]
    username))

(defn- post-middleware-change-post-created-by-id-to-name [connection post]
  (let [user_id (:created_by post)
        username (:username (handle-get-user-by-id connection user_id))]
    (-> post
        (dissoc :created_by)
        (assoc :created_by username))))

(defn- post-middleware-add-creator-avatar [connection post]
  (let [user (handle-get-user-by-name connection (:username post))]
    (assoc post :creator_avatar
           (rand-nth '("http://www.avatarsdb.com/avatars/hidden_cat.jpg"
                       "http://www.avatarsdb.com/avatars/dennis.jpg"
                       "http://www.avatarsdb.com/avatars/funny_penguin.gif"
                       "http://www.avatarsdb.com/avatars/playful_cat.gif"))))
  )

(defn- wrap-post [connection post]
  (->> post
       (post-middleware-change-post-created-by-id-to-name connection)
       (post-middleware-add-creator-avatar connection)))


;; handlers
(defn- handle-add-post [connection user new-post]
  (let [validation (s/check (s/pred post-body?) new-post)]
    (if (= validation nil)
      (let [user (handle-get-user-by-name connection (:username user))]
        (db/insert! connection :posts {:created_by (:id user)
                                       :body new-post})))))

(defn- handle-get-all-posts [connection]
  (let [raw-posts (into [] (db/query connection ["SELECT * FROM posts ORDER BY ID DESC"]))]
    (map (partial wrap-post connection) raw-posts)))


(defn- handle-get-posts-by-offset [connection n offset]
  (into [] (db/query connection ["SELECT * FROM posts ORDER BY ID DESC OFFSET ? LIMIT ?" (* offset n) n])))

(defn- handle-get-user-by-name [connection username]
  (first (db/query connection ["SELECT username,hash,id FROM users WHERE username = ?" username])))
(defn- handle-get-user-by-id [connection user_id]
  (first (db/query connection ["SELECT username,hash,id FROM users WHERE id = ?" user_id])))

(defn handle-create-user [connection username hash]
  (db/insert! connection :users {:username username :hash hash}))

(defrecord PostgreSQL-DB [connection]
  backend.dbprotocol/DbActions

  (add-post [this user new-post]
    (handle-add-post connection user new-post))

  (get-all-posts [this]
    (handle-get-all-posts connection))

  (get-posts-by-offset [this n offset]
    (handle-get-posts-by-offset connection n offset))

  (get-user-by-name [this username]
    (handle-get-user-by-name connection username))
  (get-user-by-id [this user_id]
    (handle-get-user-by-id connection user_id))

  (create-user [this username hash]
    (handle-create-user connection username hash))
  )

(defstate Database
  :start (do
           (log/info "starting database component")

           (migrate ConnectionPool)

           (log/info (format "backend connecting to database: %s" ConnectionPool))

           ;; construct the component that is going to be returned, this is what
           ;; is injected when ':refer [Database]' is used.
           (->PostgreSQL-DB ConnectionPool))
  :stop (log/info "stopping database component"))
