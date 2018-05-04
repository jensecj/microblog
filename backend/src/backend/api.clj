(ns backend.api
  (:require [environ.core :refer [env]]
            [taoensso.timbre :as log]
            [schema.core :as s]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [ring.middleware.cors :refer [wrap-cors]]
            [buddy.auth :as ba]
            [buddy.auth.backends.session :as session]
            [buddy.auth.middleware :as bam]
            [buddy.hashers :as bh]
            [buddy.auth.accessrules :as baa]

            [backend.db :refer [Database]]
            [backend.schema :refer [Post User]]
            [backend.dbprotocol :as db]
            ))

;; helpers
(defn- validate-registration [db username password]
  (let [user_exists (get-user-by-name db username)]
    (not (or user_exists (empty? username) (empty? password)))))

;; AUTH
(defn authenticate-user [db username password]
  (let [user (get-user-by-name db username)]
    (bh/check password (:hash user))))

(defn access-error [request value]
  (unauthorized value))

(defn wrap-rule [handler rule]
  (-> handler
      (baa/wrap-access-rules
       {:rules [{:pattern #".*" :handler rule}]
        :on-error access-error})))

;; easy :auth-rules for the api
(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-rule rule]))

;; grab the user sending the request, and bind it to :current-user
(defmethod restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))

(defn authenticated? [req]
  (ba/authenticated? req))


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
     (let [user (get-user-by-name db username)]
       (log/info "some tried to login! (" username "," password ")")

       (if (authenticate-user db username password)
         (do
           (log/info "login success!")
           (assoc-in (ok {}) [:session :identity] {:username (:username user)}))
         (do
           (log/info "login failed!")
           (assoc-in (forbidden {}) [:session :identity] nil)))))

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
     :auth-rules authenticated?
     :summary "Get a users record by username"
     :return {:result User}
     :query-params [username :- s/Str]
     (handle-get-user-by-name db username))
   (GET "/get-user-by-id" []
     :auth-rules authenticated?
     :summary "Get a users record by user id"
     :return {:result User}
     :query-params [user_id :- s/Int]
     (handle-get-user-by-id db user_id))))

(defn- post-routes [db]
  (routes
   (POST "/new-post" []
     :auth-rules authenticated?
     :summary "Add a new post"
     :body-params [post :- s/Str]
     :current-user user
     (db/add-post db user post))
   (GET "/all-posts" []
     :auth-rules authenticated?
     :summary "Get all available posts"
     :return {:result [Post]}
     (ok {:result (db/get-all-posts db)}))
   (GET "/posts-by-offset" []
     :auth-rules authenticated?
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
