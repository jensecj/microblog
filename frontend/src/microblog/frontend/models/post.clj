(ns microblog.frontend.models.post
  (:require [clj-http.client :as client]))

(defn all []
  (:result (client/get "http://localhost:3000/api/all-posts")))

(defn paginate [offset]
  (:result (client/get (format "http://localhost:3000/api/page/%s" offset))))

(defn create [post]
  (client/post "http://localhost:3000/api/post"
               {:form-params {:post (:post post)}})
  {:status 200}
  )
