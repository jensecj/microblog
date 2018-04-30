(ns backend.server-test
  (:require [backend.server :as sut]
            [backend.dbprotocol :as db-protocol]
            [clojure.test :as t :refer [deftest testing is]]
            [ring.mock.request :as mock]
            [cheshire.core :refer :all]))

(defrecord DatabaseFixture []
  db-protocol/DbActions

  (add-post [this new-post]
    {:status 200})

  (get-all-posts [this]
    (list {:id 1, :body "from get-all-posts" :created_at (java.util.Date.)}))

  (get-posts-by-offset [this n offset]
    (list {:id 1, :body "from get-posts-by-offset" :created_at (java.util.Date.)}))
  )

(defn- understand-response [res]
  (parse-string (slurp (:body res)) true))

(deftest api-entrypoints-test
  (is (= (:status ((sut/app (->DatabaseFixture)) (mock/request :get "/some-404-url")))
         404))n
  (is (= (:status ((sut/app (->DatabaseFixture)) (mock/request :get "/api/all-posts")))
         200))
  (is (= (:status ((sut/app (->DatabaseFixture)) (mock/request :get "/api/page/1")))
         200))
  (is (= (:status ((sut/app (->DatabaseFixture)) (mock/request :get "/api/all-posts")))
         200))
  (is (= (:status ((sut/app (->DatabaseFixture))
                   (-> (mock/request :post "/api/post")
                       (mock/json-body {:post "some content"}))))
         200))
  )
