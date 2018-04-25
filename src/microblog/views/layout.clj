(ns microblog.views.layout
  (:require
   [hiccup.page :refer [html5 include-css]]))

(defn common [title & body]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"}]
    [:title title]
    (include-css
     "/css/bootswatch-flatly.min.css"
     "/css/style.css")]
   [:body
    [:div {:class "container"}
     [:div {:id "header" :class "pb-5"} [:a {:href "/"} [:h1 "microblog"]]]
     [:div {:id "content"} body]
     [:div {:id "footer"} "footer"]
     ]
    ]))

(defn simple [content]
  (common "microblog web app" content))

(defn not-found []
  (common "Page Not Found"
          [:div {:id "not-found"}
           "404: not found"]))
