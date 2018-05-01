(ns frontend.views.register
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]))

(defn register-page []
  (let [username (reagent/atom "")
        password (reagent/atom "")]
    (fn []
      [:div {:class "container mt-5"}
       [:div {:class "row justify-content-center"}
        [:div {:class "col-md-4 col-md-offset-4"}
         [:h3 {:class "text-center mb-5"} "create account."]
         [:form {:role "form"}
          [:div {:class "form-group"}
           [:label {:for "inputUsernameEmail"} "Username"]
           [:input {:type "text", :class "form-control", :id "inputUsername",
                    :on-change #(reset! username (-> % .-target .-value))}]]
          [:div {:class "form-group"}
           [:label {:for "inputPassword"} "Password"]
           [:input {:type "password", :class "form-control", :id "inputPassword"
                    :on-change #(reset! password (-> % .-target .-value))}]]
          [:div {:class "btn btn-lg btn-block btn-primary"
                 :on-click #(dispatch [:register/post {:username @username :password @password}])} "create account"]
          [:div {:class "btn btn-sm btn-link float-right"} "create new account"]
          ]]]
       ])))
