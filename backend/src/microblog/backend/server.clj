(ns microblog.backend.server
  (:gen-class)
  (:require [environ.core :refer [env]]
            [compojure.api.sweet :refer :all]
            [microblog.backend.db :refer [Database]]
            [microblog.backend.dbprotocol :as db]
            [mount.core :as mount :refer [defstate]]
            [org.httpkit.server :as serv]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [taoensso.timbre :as log]))

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
      (ok {:message "welcome to my api"}))
    (GET "/all-posts" []
      :return {:result [Post]}
      (ok {:result (db/get-all-posts Database)}))
    (GET "/page/:p" []
      :path-params [p :- s/Int]
      :return {:result [Post]}
      (ok {:result (db/get-posts-by-offset Database 3 p)}))
    (POST "/post" []
      :body-params [post :- s/Str]
      (db/add-post Database post)
      )
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
   #'api-routes
   (ANY "*" [] (not-found {:message "invalid request"}))))

(def config {:host (env :microblog-api-url)
             :port (read-string (env :microblog-api-port))})

(defstate Server
  :start (do
           (log/info "starting server component")
           (log/info (format "backend is running: %s" config))
           (serv/run-server #'app config))
  :stop (do
          (log/info "stopping server component")
          (Server :timeout 100)))

(defn reset []
  (mount/stop)
  (mount/start))

(defn -main [& args]
  (mount/start)

  (comment
    (reset)
    (mount/stop)
    ))
