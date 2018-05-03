(ns backend.dbprotocol)

(defprotocol DbActions
  (add-post [this user new-post] "add a new post to the database")
  (get-all-posts [this] "get all posts from the database")
  (get-posts-by-offset [this n offset] "get n posts, starting at (n * offset)")

  (get-user-by-name [this username] "get a user record by username")
  (get-user-by-id [this user_id] "get a user record by user_id")

  (create-user [this username hash] "add a new user to the database"))
