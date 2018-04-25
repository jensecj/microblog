(ns microblog.web
  (:require
   [clojure.java.io :as io]
   [org.httpkit.server :as s]
   [compojure.core :refer [defroutes POST GET ANY]]
   [compojure.route :as route]
   [hiccup2.core :as h]
   [hiccup.page :refer [html5]]
   [ring.middleware.defaults :refer :all]

   [microblog.middleware :as m]
   [microblog.controllers.posts :as posts]
   [microblog.views.layout :as layout]
   [microblog.models.migration :as schema])
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

(defn stop-server []
  (when-not (nil? server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (reset! server
          (s/run-server app {:port 8080})))

(defn -main []
  (schema/migrate)
  (start-server))
