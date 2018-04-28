-- :name get-all-posts :? :*
-- :doc Returns a list of all posts made to date
SELECT *
  FROM posts
 ORDER BY ID DESC
