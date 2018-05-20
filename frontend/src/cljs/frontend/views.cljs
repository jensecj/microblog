(ns frontend.views
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [frontend.subs :as subs]
            [reagent.core :as reagent]
            [frontend.views.login]
            [frontend.views.timeline]
            [frontend.views.register]
            [frontend.views.settings]
            [frontend.views.user]
            [frontend.views.common :as common]
            ))

(defmulti pages identity)
(defmethod pages :login    [] [(frontend.views.login/login-page)])
(defmethod pages :timeline [] [(frontend.views.timeline/timeline-page)])
(defmethod pages :register [] [(frontend.views.register/register-page)])
(defmethod pages :settings [] [(frontend.views.settings/settings-page)])
(defmethod pages :user [] [(frontend.views.user/user-page)])

(defn show-page
  [page-name]
  (pages page-name))

(defn main-page []
  (let [active-page (re-frame/subscribe [:active-page])]
    (fn []
      [:div
       [(common/header "jens")]
       [:hr]
       [:div
        (show-page @active-page)]
       [(common/footer)]])))
