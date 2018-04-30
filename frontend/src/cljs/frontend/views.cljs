(ns frontend.views
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [frontend.subs :as subs]
            [reagent.core :as reagent]
            [frontend.views.login]
            [frontend.views.timeline]
            ))

(defmulti pages identity)
(defmethod pages :login [] [(frontend.views.login/login-page)])
(defmethod pages :timeline [] [(frontend.views.timeline/timeline-page)])

(defn show-page
  [page-name]
  (pages page-name))

(defn main-page []
  (let [active-page (re-frame/subscribe [:active-page])
        name (re-frame/subscribe [:name])]
    (fn []
      [:div (show-page @active-page)])))
