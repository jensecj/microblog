(ns microblog.web
  (:require
   [clojure.java.io :as io]
   [org.httpkit.server :as s]
   [compojure.core :refer [defroutes routes POST GET ANY]]
   [compojure.route :as route]
   [hiccup2.core :as h]
   [hiccup.page :refer [html5]]

   [microblog.middleware :as m]
   [microblog.controllers.posts :as posts]
   [microblog.views.layout :as layout]
   [microblog.models.migration :as schema])
  (:gen-class))


(defn create-server []
  (s/run-server handler {:port 8080}))

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
