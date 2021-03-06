(ns frontend.views.login
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]))

(defn login-page []
  (let [username (reagent/atom "")
        password (reagent/atom "")]
    (fn []
      [:div {:class "container mt-5"}
       [:div {:class "row justify-content-center"}
        [:div {:class "col-md-4 col-md-offset-4"}
         [:h3 {:class "text-center mb-5"} "log in."]
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
                 :on-click #(dispatch [:login/post {:username @username :password @password}])} "Log In"]
          [:div {:class "btn btn-sm btn-link float-right"
                 :on-click #(dispatch [:active-page :register])} "create new account"]
          ]]]
       ])))
