(ns frontend.events.login
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(re-frame/reg-event-fx
 :login/post
 (fn [cofx [event data]]
   {:db (assoc (:db cofx) :name (:username data))
    :http-xhrio {:method :post
                 :uri (str "http://localhost:3000/api/login")
                 :with-credentials true
                 :params data
                 :format (ajax/json-request-format)
                 :timeout 2000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:login/success]
                 :on-failure [:request-failure]}
    }))

(re-frame/reg-event-fx
 :login/success
 (fn [cofx]
   (prn "login success!")
   {:dispatch-n (list
                 [:timeline/get-all-posts]
                 [:active-page :timeline]
                 )}))

(re-frame/reg-event-fx
 :login/failure
 (fn [cofx]
   (prn "login failure!")
   {:dispatch-n (list
                 [:timeline/get-all-posts]
                 [:active-page :timeline]
                 )}))
