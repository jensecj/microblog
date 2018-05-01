(ns frontend.events
  (:require [re-frame.core :as re-frame]
            [frontend.db :as db]
            [frontend.events.login]
            [frontend.events.register]
            [frontend.events.timeline]
            ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 :active-page
 (fn [db [_ data]]
   (assoc db :active-page data)))

(re-frame/reg-event-db
 :request-failure
 (fn [db [_ result]]
   (prn "Request failed: " result)
   db))
