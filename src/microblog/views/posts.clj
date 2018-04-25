(ns microblog.views.posts
  (:require
   [hiccup2.core :as h]
   [hiccup.form :as form]
   [ring.util.anti-forgery :as anti-forgery]
   [microblog.views.layout :as layout]))

(defn blog-form []
  [:div {:class "row"}
   [:div {:id "post-form" :class "form-group mx-auto col-xs-12 col-sm-10 col-md-10 col-lg-8"}
    (form/form-to
     [:post "/"]
     (anti-forgery/anti-forgery-field)
     [:textarea {:name "post" :class "form-control" :rows "4"}]
     [:input {:type "submit" :class "btn btn-primary btn-lg float-right m-1" :value "submit"}])]])

(defn render-posts [posts]
  [:div {:class "posts col-10 col-lg-6 mx-auto"}
   (map
    (fn [post]
      [:div {:class "card mb-2"}
       [:div {:class "post card-body"}
        [:image {:src "http://via.placeholder.com/100x100" :class "float-left mr-3"}]
        [:div {:class "card-title d-flex w-80 justify-content-between" :height 30}
         [:h4 {:class "card-title text-muted"} "@username"]
         [:small {:class "text-muted"} (h/html (subs (str (:created_at post)) 0 10))]]
        [:div {:class "clear"}]
        [:p {:class "card-text"} (h/html (:body post))]
        [:a {:href "#" :class "card-link float-right"} "like &#128077;"]]])
    posts)])

(defn index [posts]
  (layout/common
   "microblog - index"
   (blog-form)
   [:div {:class "clear"}]
   (render-posts posts)))
