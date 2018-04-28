(ns microblog.backend.server
  (:require
   [environ.core :refer [env]]
   [org.httpkit.server :as serv]
   [compojure.api.sweet :refer :all]
   [ring.util.http-response :refer :all]
   [schema.core :as s]
   [mount.core :as mount]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as log]
   [microblog.backend.db :as db]
   [microblog.backend.db :refer [Database]]
   )
  (:gen-class))

(s/defschema Post
  {:id s/Int
   :body s/Str
   :created_at s/Inst})

(def api-routes
  (context "/api" []
           :tags ["api"]
           (GET "/" []
                :return {:message s/Str}
                :summary "welcomes a get request to root!"
                (ok {:message "welcome to the api"}))
           (GET "/all-posts" []
                :return {:result [Post]}
                (ok {:result (db/get-all-posts)}))
           (GET "/page/:p" []
                :path-params [p :- s/Int]
                :return {:result [Post]}
                (ok {:result (db/get-posts-by-page p)}))
           (POST "/post" []
                 :body-params [post :- s/Str]
                 (db/add-post post))
           ))

(def app
  (api
   {:swagger
    {:ui "/api-docs"
     :spec "/swagger.json"
     :data {:info {:title "Simple REST API"
                   :description "simple rest api example"}
            :tags [{:name "api", :description "some api endpoints"}]
            :consumes ["application/json"]
            :produces ["application/json"]}}}
   api-routes
   (ANY "*" [] (not-found {:message "invalid request"}))))

(defonce server (atom nil))
(def config {:host (env :microblog-api-url)
             :port (read-string (env :microblog-api-port))})

(defn start-server []
  (reset! server (serv/run-server #'app config)))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)))

(defstate Server
  :start (do
           (log/info "starting server component")
           (log/info (format "backend is running: %s" config))
           (start-server))
  :stop (do
          (log/info "stopping server component")
          (stop-server)))

(defn -main [& args]
  (mount/start))
