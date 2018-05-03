(ns backend.schema
  (:require [schema.core :as s]))

(defn post-body? [post]
  (and (s/validate s/Str post)
       (<= (count post) 200)
       (> (count post) 0)))

(s/defschema Post
  {:id s/Int
   :created_by s/Str
   :body s/Str
   :created_at s/Inst
   :creator_avatar s/Str
   })

(s/defschema InternalPost
  {:id s/Int
   :created_by s/Str
   :body s/Str
   :created_at s/Inst
   })

(s/defschema User
  {:id s/Int
   :username s/Str
   :hash s/Str
   :avatar-url s/Str})
