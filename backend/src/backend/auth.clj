(ns backend.auth
  (:require
   [ring.util.http-response :refer :all]
   [compojure.api.sweet :refer :all]
   [compojure.api.meta :refer [restructure-param]]
   [buddy.auth :as ba]
   [buddy.auth.accessrules :as baa]
   ))

(defn authenticated? [req] (ba/authenticated? req))
(defn access-error [request value] (unauthorized value))

(defn wrap-rule [handler rule]
  (-> handler
      (baa/wrap-access-rules
       {:rules [{:pattern #".*" :handler rule}]
        :on-error access-error})))

;; easy :auth-rules for the api
(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-rule rule]))

;; grab the user sending the request, and bind it to :current-user
(defmethod restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))
