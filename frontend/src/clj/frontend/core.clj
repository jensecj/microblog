(ns frontend.core
  (:require
   [environ.core :refer [env]]
   [taoensso.timbre :as log]
   [org.httpkit.server :as s]
   [ring.middleware.defaults :refer :all]
   [ring.util.response :refer [resource-response]]
   [compojure.core :refer [defroutes POST GET ANY]]
   [compojure.route :as route]
   [mount.core :as mount]
   [mount.core :refer [defstate]]
   ;; [frontend.middleware :as m]
   )
  (:gen-class))

(defroutes app-routes
  (GET "/" []
       (resource-response "index.html" {:root "public"}))
  (route/resources "/")
  (route/not-found {:status 404}))

(def app
  (-> #'app-routes
      ;; (m/remove-trailing-slashes)
      ;; (wrap-defaults site-defaults)
      ))

(def config {:host (env :microblog-url)
             :port (read-string (env :microblog-port))})

(defstate Webserver
  :start (do
           (log/info "starting webserver component")
           (log/info (format "frontend is running: %s" config))
           (s/run-server #'app config))
  :stop (do
          (log/info "stopping webserver component")
          (Webserver :timeout 100)))

(defn -main [& args]
  (mount/start))
