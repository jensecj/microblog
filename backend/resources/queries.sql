-------------------
-- POSTS QUERIES --
-------------------

-- :name get-all-posts :? :*
-- :doc Returns a list of all posts made to date
SELECT *
  FROM posts
 ORDER BY ID DESC

-- :name get-posts-by-offset :? :*
-- :doc Returns n posts, starting at offset
SELECT *
  FROM posts
 ORDER BY ID
  DESC
OFFSET :offset
 LIMIT :n

-- :name add-post :!
-- :doc add a new post
INSERT into posts (created_by, body)
VALUES (:created_by, :body)


------------------
-- USER QUERIES --
------------------

-- :name get-user-by-name :? :1
-- :doc get a user by their name
SELECT *
  FROM users
 WHERE username = :username

-- :name get-user-by-id :? :1
-- :doc get a user by their id
SELECT *
  FROM users
 WHERE id = :id

-- :name create-user :!
-- :doc Create a new user
INSERT INTO users (username, hash)
VALUES (:username, :hash)
