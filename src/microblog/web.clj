(ns microblog.web
  (:require [org.httpkit.server :as s]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<h1>Hello World!</h1>"})

(defn create-server []
  (s/run-server handler {:port 8080}))

(defn stop-server [server]
  (server :timeout 10))

(defn -main []
  (def server (create-server))
  (stop-server server))
