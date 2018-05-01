(ns frontend.events.register
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(re-frame/reg-event-fx
 :register/post
 (fn [cofx [event data]]
   {:db (assoc (:db cofx) :name (:username data))
    :http-xhrio {:method :post
                 :uri (str "http://localhost:3000/api/register")
                 :with-credentials true
                 :params data
                 :format (ajax/json-request-format)
                 :timeout 2000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:register/success]
                 :on-failure [:request-failure]}
    }))

(re-frame/reg-event-fx
 :register/success
 (fn [cofx]
   (prn "registration success!")
   {:dispatch-n (list
                 [:timeline/get-all-posts]
                 [:active-page :timeline]
                 )}))

(re-frame/reg-event-fx
 :register/failure
 (fn [cofx]
   (prn "registration failure!")
   {:dispatch-n (list
                 [:request-failure]
                 [:active-page :register]
                 )}))
