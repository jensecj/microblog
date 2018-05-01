(ns backend.api
  (:require [environ.core :refer [env]]
            [compojure.api.sweet :refer :all]
            [backend.db :refer [Database Post User]]
            [backend.dbprotocol :as db]
            [ring.util.http-response :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [ring.middleware.cors :refer [wrap-cors]]
            [taoensso.timbre :as log]
            [buddy.auth :as ba]
            [buddy.auth.backends.session :as session]
            [buddy.auth.middleware :as bam]
            [buddy.hashers :as bh]
            [buddy.auth.accessrules :as baa]
            ))

;; API
(defn get-user-by-name [db username]
  (db/get-user-by-name db username))

(defn get-all-posts [db]
  (db/get-all-posts db))

(defn authenticate-user [db username password]
  (let [user (get-user-by-name db username)]
    (bh/check password (:hash user))))

;; AUTH
(defn access-error [request value]
  (unauthorized value))

(defn wrap-rule [handler rule]
  (-> handler
      (baa/wrap-access-rules
       {:rules [{:pattern #".*" :handler rule}]
        :on-error access-error})))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-rule rule]))

(defn authenticated? [req]
  (ba/authenticated? req))

;; HANDLER

(defn- api-routes [db]
  (context "/api" []
    :tags ["api"]

    (POST "/login" []
      :summary "logs a user in"
      :body-params [username :- s/Str, password :- s/Str]
      (let [user (get-user-by-name db username)]
        (log/info "some tried to login! (" username "," password ")")

        (if (authenticate-user db username password)
          (do
            (log/info "login success!")
            (assoc-in (ok) [:session :identity] {:username (:username user)}))
          (do
            (log/info "login failed!")
            (assoc-in (forbidden) [:session :identity] nil)))
        )
      )

    (GET "/get-user-by-name" []
      :summary "Get a users record by username"
      :return {:result User}
      :query-params [username :- s/Str]
      (ok {:result (get-user-by-name db username)}))

    (POST "/new-post" []
      :summary "Add a new post"
      :body-params [post :- s/Str]
      (db/add-post db post))
    (GET "/all-posts" []
      :summary "Get all available posts"
      :return {:result [Post]}
      :auth-rules authenticated?
      (ok {:result (get-all-posts db)}))
    (GET "/posts-by-offset" []
      :summary "Get n posts, starting at offset"
      :query-params [num_posts :- s/Int, offset :- s/Int]
      :return {:result [Post]}
      (ok {:result (db/get-posts-by-offset db num_posts offset)}))

    ))

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
                 :access-control-allow-methods [:get :post]))
  )
