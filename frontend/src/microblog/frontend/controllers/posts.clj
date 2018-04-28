(ns microblog.frontend.controllers.posts
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [compojure.coercions :refer :all]
   [clojure.string :as str]
   [ring.util.response :as ring]
   [microblog.frontend.views.posts :as view]
   [microblog.frontend.models.post :as model]))

(defn index []
  (view/index (model/all)))

(defn page [page]
  (view/paged-index (model/paginate page) page))

(defn create [post]
  (if (not (str/blank? post))
    (model/create post))
  (ring/redirect "/"))

(defroutes routes
  (GET "/" [] (index))
  (GET "/:p" [p :<< as-int] (page p))
  (POST "/" [post] (create post)))
