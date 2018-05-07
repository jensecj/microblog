(ns backend.api
  (:require [environ.core :refer [env]]
            [taoensso.timbre :as log]
            [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [ring.util.http-response :refer :all]
            [ring.middleware.cors :refer [wrap-cors]]
            [buddy.auth :as ba]
            [buddy.hashers :as bh]
            [buddy.auth.middleware :as bam]
            [buddy.auth.accessrules :as baa]
            [buddy.auth.backends.session :as session]

            [backend.dbprotocol :as db]
            [backend.auth :as auth]
            [backend.db :refer [Database]]
            [backend.schema :refer [Post User]]
            ))

;; helpers
(defn- validate-registration
  "Validate a registration attempt. requirements:
  * the username and password fields are non-empty.
  * the user does not already exist."
  [db username password]
  (let [user_exists (db/get-user-by-name db username)]
    (and (not user_exists)
         (not (empty? username))
         (not (empty? password)))))

(defn- validate-login
  "Validate a login attempt. requirements:
  * user exists
  * hash of password matches the stored hash for the user"
  [db username password]
  (let [user (db/get-user-by-name db username)]
    ;; if the user does not exist, (:hash user) will return nil,
    ;; and bh/check will fail.
    (bh/check password (:hash user))))

;; handlers for database calls
(defn handle-get-user-by-id [db user_id]
  (let [user (db/get-user-by-id db user_id)]
    (if user
      (ok {:result user})
      (not-found "user not found"))))
(defn handle-get-user-by-name [db username]
  (let [user (db/get-user-by-name db username)]
    (if user
      (ok {:result user})
      (not-found "user not found"))))

(defn- auth-routes [db]
  (routes
   (POST "/login" []
     :summary "logs a user in"
     :body-params [username :- s/Str, password :- s/Str]
     (log/info "some tried to login! (" username "," password ")")
     (if (validate-login db username password)
       (do
         (log/info "login success!")
         (assoc-in (ok {}) [:session :identity] {:username username}))
       (do
         (log/info "login failed!")
         (assoc-in (forbidden {}) [:session :identity] nil))))

   (POST "/register" []
     :summary "registers a new user"
     :body-params [username :- s/Str, password :- s/Str]
     (log/info "some tried to register! (" username ":" password ")")
     (if (validate-registration db username password)
       (do
         (log/info "login success!")
         (db/create-user db username (bh/derive password))
         (assoc-in (ok {}) [:session :identity] {:username username}))
       (do
         (log/info "login failed!")
         (assoc-in (forbidden {}) [:session :identity] nil))))))

(defn- user-routes [db]
  (routes
   (GET "/get-user-by-name" []
     :auth-rules auth/authenticated?
     :summary "Get a users record by username"
     :return {:result User}
     :query-params [username :- s/Str]
     (handle-get-user-by-name db username))
   (GET "/get-user-by-id" []
     :auth-rules auth/authenticated?
     :summary "Get a users record by user id"
     :return {:result User}
     :query-params [user_id :- s/Int]
     (handle-get-user-by-id db user_id))))

(defn- post-routes [db]
  (routes
   (POST "/new-post" []
     :auth-rules auth/authenticated?
     :summary "Add a new post"
     :body-params [post :- s/Str]
     :current-user user
     (db/add-post db user post))
   (GET "/all-posts" []
     :auth-rules auth/authenticated?
     :summary "Get all available posts"
     :return {:result [Post]}
     (ok {:result (db/get-all-posts db)}))
   (GET "/posts-by-offset" []
     :auth-rules auth/authenticated?
     :summary "Get n posts, starting at offset"
     :query-params [num_posts :- s/Int, offset :- s/Int]
     :return {:result [Post]}
     (ok {:result (db/get-posts-by-offset db num_posts offset)}))))

;; ROUTES
(defn- api-routes [db]
  (context "/api" []
    :tags ["api"]
    (auth-routes db)
    (user-routes db)
    (post-routes db)))

(defn- app [db]
  (api
   {:swagger
    {:ui "/api-docs"
     :spec "/swagger.json"
     :data {:info {:title "Simple REST API"
                   :description "simple rest api example"}
            :tags [{:name "api", :description "some api endpoints"}]
            :consumes ["application/json"]
            :produces ["application/json"]}}}
   (api-routes db)
   (ANY "*" [] (not-found {:message "invalid request"}))))

(defn handler [db]
  (-> (app db)
      (wrap-cors :access-control-allow-origin [#"http://localhost:8080" #"http://localhost:3449"]
                 :access-control-allow-methods [:get :post]
                 :access-control-allow-credentials true)))
