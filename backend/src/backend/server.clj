(ns backend.server
  (:gen-class)
  (:require [environ.core :refer [env]]
            [compojure.api.sweet :refer :all]
            [backend.db :refer [Database Post]]
            [backend.dbprotocol :as db]
            [mount.core :as mount :refer [defstate]]
            [org.httpkit.server :as serv]
            [ring.util.http-response :refer :all]
            [ring.middleware.cors :refer [wrap-cors]]
            [schema.core :as s]
            [taoensso.timbre :as log]
            [buddy.hashers :as bh]
            ))

(defn- get-user-by-name [username]
  {:username "jens" :hash (bh/derive "123")})

(defn- api-routes [db]
  (context "/api" []
           :tags ["api"]

           (POST "/login" []
                 :summary "logs a user in"
                 :body-params [username :- s/Str, hash :- s/Str]
                 (let [user (get-user-by-name username)]
                   (if (bh/check hash (:hash user))
                     (assoc-in (ok) [:session :identity] {:username (:username user)})
                     (assoc-in (forbidden) [:session :identity] nil)))
                 )

           (GET "/all-posts" []
                :summary "Get all available posts"
                :return {:result [Post]}
                (ok {:result (db/get-all-posts db)}))
           (GET "/posts-by-offset" []
                :summary "Get n posts, starting at offset"
                :query-params [num_posts :- s/Int, offset :- s/Int]
                :return {:result [Post]}
                (ok {:result (db/get-posts-by-offset db num_posts offset)}))
           (POST "/new-post" []
                 :summary "Add a new post"
                 :body-params [post :- s/Str]
                 (db/add-post db post))

           (GET "/page/:p" []
                :path-params [p :- s/Int]
                :return {:result [Post]}
                (ok {:result (db/get-posts-by-offset db 3 p)}))
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
  (wrap-cors (app db)
             :access-control-allow-origin [#"http://localhost:3449"]
             :access-control-allow-methods [:get :post]))

(def config {:host (env :microblog-api-url)
             :port (read-string (env :microblog-api-port))})

(defstate Server
  :start (do
           (log/info "starting server component")
           (log/info (format "backend is running: %s" config))
           (serv/run-server (handler Database) config))
  :stop (do
          (log/info "stopping server component")
          (Server :timeout 100)))

(defn reset []
  (mount/stop)
  (mount/start))

(comment
  (mount/start)
  (reset)
  (mount/stop)
  )

(defn -main [& args]
  (log/set-level! :info)
  (mount/start))
