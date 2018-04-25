(ns microblog.views.posts
  (:require
   [hiccup2.core :as h]
   [hiccup.form :as form]
   [ring.util.anti-forgery :as anti-forgery]
   [microblog.views.layout :as layout]))

(defn blog-form []
  [:div {:id "post-form" :class "form-group col-xs-12 col-sm-10 col-md-10 col-lg-8"}
   (form/form-to
    [:post "/"]
    (anti-forgery/anti-forgery-field)
    [:textarea {:name "post" :class "form-control" :rows "4"}]
    [:input {:type "submit" :class "btn btn-primary btn-lg float-right m-1" :value "submit"}])])

(defn display-posts [posts]
  [:div {:class "posts col-6"}
   (map
    (fn [post] [:div {:class "mb-0 post"}
                [:p (h/html (:body post))]])
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
   (display-posts posts)))
