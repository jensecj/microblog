(ns backend.server
  (:gen-class)
  (:require [environ.core :refer [env]]
            [backend.db :refer [Database Post User]]
            [backend.dbprotocol :as db]
            [mount.core :as mount :refer [defstate]]
            [taoensso.timbre :as log]
            [backend.api :as api]
            [ring.middleware.session :as rms]
            [ring.util.http-response :as response]
            [compojure.api.meta :refer [restructure-param]]
            [buddy.auth :as ba]
            [buddy.auth.accessrules :as baa]
            [buddy.auth.backends.session :as session]
            [buddy.auth.middleware :as bam]
            [buddy.hashers :as bh]
            ))

(def config {:host (env :microblog-api-url)
             :port (read-string (env :microblog-api-port))})

(def cookie-name "microblog-cookie")
(def auth-backend (session/session-backend))

(defn wrap-app-session [handler]
  (-> handler
      (bam/wrap-authorization auth-backend)
      (bam/wrap-authentication auth-backend)
      (rms/wrap-session {:cookie cookie-name})))

(defn- handle [handler]
  (-> (wrap-app-session handler)))

(defstate Server
  :start (do
           (log/info "starting server component")
           (log/info (format "backend is running: %s" config))
           (serv/run-server (handle (api/handler Database)) config))
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
