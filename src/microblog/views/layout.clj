(ns microblog.views.layout
  (:require
   [hiccup.page :refer [html5 include-css]]))

(defn common [title & body]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
    [:title title]
    (include-css
     "/css/reset.css"
     "/css/base.css"
     "/css/skeleton.css"
     "/css/screen.css")]
   [:body
    [:div {:id "header"}
     [:h1 {:class "container"} "microblog"]]
    [:div {:id "content" :class "container"} body]
    [:div {:id "footer"}]]))

(defn simple [content]
  (common "microblog web app" content))

(defn not-found []
  (common "Page Not Found"
          [:div {:id "not-found"}
           "404: not found"]))
