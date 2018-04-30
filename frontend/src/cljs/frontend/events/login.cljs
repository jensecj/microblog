(ns frontend.events.login
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]))

(re-frame/reg-event-fx
 :login/post
 (fn [cofx [event data]]
   {:db (assoc (:db cofx) :name (:username data))
    :dispatch-n (list
                 [:login/success])
    }))

(re-frame/reg-event-fx
 :login/success
 (fn [cofx]
   (prn "login success!")
   {:dispatch-n (list
                 [:timeline/get-all-posts]
                 [:active-page :timeline]
                 )}))
