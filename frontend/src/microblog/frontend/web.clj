(ns microblog.frontend.web
  (:require
   [clojure.java.io :as io]
   [org.httpkit.server :as s]
   [environ.core :refer [env]]
   [compojure.core :refer [defroutes POST GET ANY]]
   [compojure.route :as route]
   [hiccup2.core :as h]
   [hiccup.page :refer [html5]]
   [ring.middleware.defaults :refer :all]
   [clj-http.client :as client]
   [taoensso.timbre :as log]
   [mount.core :as mount]
   [mount.core :refer [defstate]]
   [microblog.frontend.middleware :as m]
   [microblog.frontend.controllers.posts :as posts]
   [microblog.frontend.views.layout :as layout])
  (:gen-class))

(defn index []
  (html5
   (h/html
    [:head [:title "clojure web app"]]
    [:body
     [:div {:id "content"}
      "something"]])))

(defroutes auth-routes
  (GET "/login" [:as req]
       (layout/simple "login"))
  (GET "/logout" [:as req]
       (layout/simple "logout"))
  (GET "/register" [:as req]
       (layout/simple "register")))

(defroutes app-routes
  auth-routes
  posts/routes

  (GET "/dashboard" [:as req]
       (layout/simple "dashboard"))
  (GET "/users/:user" [user :as req]
       (layout/simple (format "<h1>all posts of %s!</h1>" user)))
  (route/resources "/")
  (route/not-found (layout/not-found)))

(def app
  (-> #'app-routes
      (m/remove-trailing-slashes)
      (wrap-defaults site-defaults)))

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

(defn reset []
  (mount/stop)
  (mount/start))

(comment
  (mount/stop)
  (mount/start)
  (reset)
  )

(defn -main [& args]
  (mount/start))
