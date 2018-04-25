(ns microblog.controllers.posts
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [compojure.coercions :refer :all]
   [clojure.string :as str]
   [ring.util.response :as ring]
   [microblog.views.posts :as view]
   [microblog.models.post :as model]))

(defn index []
  (view/index (model/all)))

(defn page [page]
  (view/index (model/paginate page)))

(defn create [post]
  (if (not (str/blank? post))
    (model/create post))
  (ring/redirect "/"))

(defroutes routes
  (GET "/" [] (page 0))
  (GET "/:p" [p :<< as-int] (page p))
  (POST "/" [post] (create post)))
