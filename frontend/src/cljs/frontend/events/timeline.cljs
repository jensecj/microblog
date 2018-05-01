(ns frontend.events.timeline
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            ))

(re-frame/reg-event-fx
 :timeline/get-all-posts
 (fn [_ _]
   {:http-xhrio {:method :get
                 :uri (str "http://localhost:3000/api/all-posts")
                 :timeout 2000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:timeline/get-all-posts-success]
                 :on-failure [:request-failure]}
    }))

(re-frame/reg-event-db
 :timeline/get-all-posts-success
 (fn [db [_ posts]]
   (prn "got all posts!")
   (assoc db :posts (:result posts))))

(re-frame/reg-event-fx
 :timeline/post
 (fn [cofx [_ data]]
   (prn "got some data: " data)
   {:http-xhrio {:method :post
                 :uri (str "http://localhost:3000/api/new-post")
                 :params data
                 :timeout 5000
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:timeline/post-success]
                 :on-failure [:request-failure]}}))

(re-frame/reg-event-fx
 :timeline/post-success
 (fn [cofx]
   (prn "timeline post success!")
   {:dispatch-n (list
                 [:timeline/get-all-posts]
                 [:active-page :timeline])}))
