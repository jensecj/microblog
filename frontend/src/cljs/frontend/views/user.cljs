(ns frontend.views.user
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]))

(defn- user-head [user]
  (fn []
    [:div.container
     [:div.col-md-4
      [:div.profile-container
       [:div.profile-header.row
        [:div.col-md-12.col-sm-12.text-center
         [:img.header-avatar
          {:alt "", :src "http://bootdey.com/img/Content/user_3.jpg"}]]
        [:div.col-md-12.col-sm-12.profile-info
         [:div.row
          [:div.col-md-8
           [:div.header-fullname "Jens"]]
          [:div.col-md-4
           [:a
            {:href "#" :class "p-1 btn btn-palegreen pull-right"}
            "FOLLOW"]]]
         [:div.header-information
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna."]]
        [:div.col-md-12.col-sm-12.col-xs-12.profile-stats
         [:div.row
          [:div.col-md-4.col-sm-4.col-xs-12.stats-col
           [:div.stats-value.pink "284"]
           [:div.stats-title "FOLLOWING"]]
          [:div.col-md-4.col-sm-4.col-xs-12.stats-col
           [:div.stats-value.pink "803"]
           [:div.stats-title "FOLLOWERS"]]
          [:div.col-md-4.col-sm-4.col-xs-12.stats-col
           [:div.stats-value.pink "1207"]
           [:div.stats-title "POSTS"]]]
         [:div.row
          [:div.col-md-4.col-sm-4.col-xs-4.inlinestats-col
           "🌎 Denmark"]
          [:div.col-md-4.col-sm-4.col-xs-4.inlinestats-col
           "Adventurer"]
          [:div.col-md-4.col-sm-4.col-xs-4.inlinestats-col
           "N/A"]]]]]]]
    ))

(defn user-page []
  (let [user (re-frame/subscribe [:user])]
    (fn []
      [:div {:class "container mt-5"}
       [(user-head @user)]])))
