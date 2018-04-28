(ns microblog.frontend.web
  (:require
   [clojure.java.io :as io]
   [org.httpkit.server :as s]
   [compojure.core :refer [defroutes POST GET ANY]]
   [compojure.route :as route]
   [hiccup2.core :as h]
   [hiccup.page :refer [html5]]
   [ring.middleware.defaults :refer :all]
   [clj-http.client :as client]
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
  (-> app-routes
      (m/remove-trailing-slashes)
      (wrap-defaults site-defaults)))

(defonce server (atom nil))
(def config {:port 3001})

(defn stop-server []
  (when-not (nil? server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (reset! server
          (s/run-server app config)))

(defn -main []
  (println (format "frontend running on port localhost:%s" (:port config)))
  (start-server))
