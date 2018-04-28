(ns microblog.frontend.views.posts
  (:require
   [hiccup2.core :as h]
   [hiccup.form :as form]
   [ring.util.anti-forgery :as anti-forgery]
   [microblog.frontend.views.layout :as layout]))

(defn blog-form []
  [:div {:class "row"}
   [:div {:id "post-form" :class "form-group mx-auto col-xs-12 col-sm-10 col-md-10 col-lg-8"}
    (form/form-to
     [:post "/"]
     (anti-forgery/anti-forgery-field)
     [:textarea {:name "post" :class "form-control" :rows "4"}]
     [:input {:type "submit" :class "btn btn-primary btn-lg float-right m-1" :value "submit"}])]])

(defn render-posts [posts]
  [:div {:class "posts col-sm-10 col-md-9 col-lg-6 mx-auto"}
   (map
    (fn [post]
      [:div {:class "card mb-2"}
       [:div {:class "post card-body"}
        [:div {:class "row"}
         [:div {:class "col-3"}
          [:image {:src "http://via.placeholder.com/100x100" :class ""}]]
         [:div {:class "col-9 pr-1"}
          [:div {:class "card-title d-flex w-80 justify-content-between" :height 30}
           [:h4 {:class "card-title text-muted"} "@username"]
           [:small {:class "text-muted"} (h/html (subs (str (:created_at post)) 0 10))]]
          [:p {:class "card-text"} (h/html (:body post))]
          [:a {:href "#" :class "card-link float-right"} "like &#128077;"]
          [:a {:href "#" :class "card-link float-left ml-0"} "↺ follow"]]]]])
    posts)])

(defn render-pagination [offset]
  [:div {:class "container mt-3"}
   [:div {:class "d-flex w-80 justify-content-center"}
    [:ul {:class "pagination pagination-lg"}
     (if (= offset 0)
       [:li {:class "page-item disabled"} [:a {:class "page-link"} "&laquo;"]]
       [:li {:class "page-item"}
        [:a {:class "page-link" :href (format "/%s" (str (- offset 1)))} "&laquo;"]]  )
     [:li {:class "page-item"}
      [:a {:class "page-link" :href (format "/%s" (str (+ offset 1)))} "&raquo;"]]]]])

(defn index [posts]
  (layout/common
   "microblog - index"
   (blog-form)
   [:div {:class "clear"}]
   (render-posts posts)
   [:div {:class "clear"}]
   (render-pagination 0)))

(defn paged-index [posts offset]
  (layout/common
   (if (= offset 0)
     "microblog - index"
     (format "microblog - page %s" offset))
   (blog-form)
   [:div {:class "clear"}]
   (render-posts posts)
   [:div {:class "clear"}]
   (render-pagination offset)))
