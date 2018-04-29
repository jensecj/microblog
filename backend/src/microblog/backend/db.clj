(ns microblog.backend.db
  (:require [clojure.java.jdbc :as db]
            [microblog.backend.connectionpool :refer [ConnectionPool]]
            [microblog.backend.dbprotocol :as dbprotocol]
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
   :body s/Str
   :created_at s/Inst})

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

(defrecord PostgreSQL-DB [connection]
  microblog.backend.dbprotocol/DbActions

  (add-post [this new-post]
    (let [validation (s/check (s/pred post-body?) new-post)]
      (if (= validation nil)
        (db/insert! connection :posts [:body] [new-post])
        ;; (bad-request (str validation))
        ))
    )

  (get-all-posts [this]
    (get-all-posts connection))

  (get-posts-by-offset [this n offset]
    (into []
          (db/query connection ["SELECT * FROM posts ORDER BY ID DESC OFFSET ? LIMIT ?" (* offset n) n])))
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
