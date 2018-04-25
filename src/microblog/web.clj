(ns microblog.web
  (:require
   [clojure.java.io :as io]
   [org.httpkit.server :as s]
   [compojure.core :refer [defroutes POST GET ANY]]
   [compojure.route :as route]
   [hiccup2.core :as h]
   [hiccup.page :refer [html5]]

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
  (route/resources "/")
  ;; (GET "/" [:as req] (index))

  auth-routes
  posts/routes

  (GET "/dashboard" [:as req]
       (layout/simple "dashboard"))
  (GET "/users/:user" [user :as req]
       (layout/simple (format "<h1>all posts of %s!</h1>" user)))
  ;; (ANY "*" []
  ;;      (route/not-found (slurp (io/resource "public/404.html"))))
  (ANY "*" []
       (route/not-found (layout/not-found)))
  )

(defn app []
  (-> app-routes))

(defn create-server []
  (s/run-server
   (m/remove-trailing-slashes (app)) {:port 8082}))

(defn stop-server [server]
  (server :timeout 10))

(defn -main []
  (try
    (def server (create-server))
    (catch Exception e (str "something exceptional occured: " (.getMessage e)))
    (finally (stop-server server))))

;; repl-driven development
(try
  (stop-server server)
  (def server (create-server)))
