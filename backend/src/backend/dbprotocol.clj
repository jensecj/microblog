(ns backend.dbprotocol)

(defprotocol DbActions
  (add-post [this new-post] "add a new post to the database")
  (get-all-posts [this] "get all posts from the database")
  (get-posts-by-offset [this n offset] "get n posts, starting at (n * offset)")
  (get-user-by-name [this username] "get a user record by username")
  )
