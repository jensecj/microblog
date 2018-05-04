(ns backend.dbprotocol)

(defprotocol UserActions
  "Actions that query, create, or manipulate users"
  (create-user [this username hash] "add a new user to the database")
  (get-user-by-id [this user_id] "get a user record by user_id")
  (get-user-by-name [this username] "get a user record by username")
  )

(defprotocol PostActions
  "Actions that query, create, or manipulate posts"
  (add-post [this user new-post] "add a new post to the database")
  (get-all-posts [this] "get all posts from the database")
  (get-posts-by-offset [this n offset] "get n posts, starting at (n * offset)")
  )
