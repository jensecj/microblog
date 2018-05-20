(ns frontend.views.common
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]))

(defn header [username]
  (fn []
    [:header {:class "m-3 pb-4"}
     [:div {:class "float-left"}
      [:a {:on-click #(dispatch [:active-page :timeline]) :href "#"} "microblog"]]
     [:a {:class "float-right ml-4" :href "#"
          :on-click #(dispatch [:active-page :settings])}
      "settings"]
     [:div
      [:a {:href "#" :class "ml-2 float-right"
           :on-click #(dispatch [:active-page :user])}
       (str "hello, " username)]]
     ]))

(defn footer []
  (fn []
    [:footer]))
