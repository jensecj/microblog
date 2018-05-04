(ns backend.schema
  (:require [schema.core :as s]
            ))

(defn post-body? [post]
  (and (s/validate s/Str post)
       (<= (count post) 200)
       (> (count post) 0)))

(s/defschema User
  {:id s/Int
   :username s/Str
   :hash s/Str
   :avatar_url s/Str})

(s/defschema PostCreator
  (dissoc User :hash))

(s/defschema Post
  {:id s/Int
   :created_by PostCreator
   :body s/Str
   :created_at s/Inst
   })
