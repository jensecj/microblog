(ns microblog.views.posts
  (:require
   [microblog.views.layout :as layout]
   [hiccup2.core :as h]
   [hiccup.form :as form]
   [ring.util.anti-forgery :as anti-forgery]
   ))

(defn blog-form []
  (form/form-to
   [:post "/"]
   [:div {:id "blog-form" :class "form-group col-10"}
    (anti-forgery/anti-forgery-field)
    ;; (form/text-area "blog-content")
    [:textarea {:id "blog-content" :class "form-control" :rows "4"}]

    ;; (form/submit-button "post this")
    [:input {:type "submit" :class "btn btn-primary btn-lg float-right m-2" :value "post this"}]
    ]
   )
  )

(defn display-posts [posts]
  [:div {:class "posts col-6"}
   (map
    (fn [post] [:h2 {:class "post"} (h/html (:body post))])
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
