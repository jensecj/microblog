(ns microblog.frontend.models.post
  (:require [clj-http.client :as client]
            [cheshire.core :refer :all]))

(defn all []
  (:result
   (parse-string
    (:body
     (client/get "http://localhost:3000/api/all-posts")) true)))

(defn paginate [offset]
  (:result
   (parse-string
    (:body
     (client/get (format "http://localhost:3000/api/page/%s" offset))) true)))

(defn create [post]
  (client/post "http://localhost:3000/api/post"
               {:content-type :json
                :body (format "{\"post\":\"%s\"}" post)}))
