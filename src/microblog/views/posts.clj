(ns microblog.views.posts
  (:require
   [hiccup2.core :as h]
   [hiccup.form :as form]
   [ring.util.anti-forgery :as anti-forgery]
   [microblog.views.layout :as layout]))

(defn blog-form []
  [:div {:id "post-form" :class "form-group mx-auto col-xs-12 col-sm-10 col-md-10 col-lg-8"}
   (form/form-to
    [:post "/"]
    (anti-forgery/anti-forgery-field)
    [:textarea {:name "post" :class "form-control" :rows "4"}]
    [:input {:type "submit" :class "btn btn-primary btn-lg float-right m-1" :value "submit"}])])

(defn render-posts [posts]
  [:div {:class "posts col-8 col-lg-6 mx-auto"}
   (map
    (fn [post]
      [:div {:class "card m-2"}
       [:div {:class "post card-body m-2"}
        [:h4 {:class "card-title mb-2"} "Display Name"]
        [:h6 {:class "card-subtitle mb-4 text-muted"} "@username"]
        [:p {:class "card-text"} (h/html (:body post))]
        [:a {:href "#" :class "card-link float-right"} "like"]]])
    posts)])

(defn index [posts]
  (layout/common
   "microblog"
   [:div {:class "row"}
    [:div {:class "col-lg-8 mx-auto"}
     (blog-form)
     ]
    ]
   [:div {:class "clear"}]
   (render-posts posts)))
