(ns frontend.views.timeline
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]))

(defn- blog-form []
  (let [text (reagent/atom "")]
    (fn []
      [:div {:class "row"}
       [:div {:id "post-form" :class "form-group col-sm-12 col-md-10 col-lg-6 mx-auto"}
        [:textarea {:name "post" :class "form-control" :rows "4"
                    :on-change #(reset! text (-> % .-target .-value))}]
        [:input {:type "submit" :class "btn btn-primary btn-lg float-right m-1" :value "submit"
                 :on-click #(dispatch [:timeline/post {:post @text}])}]
        ]])))

(defn- posts [posts]
  (let []
    (fn []
      [:div {:class "posts col-sm-12 col-md-10 col-lg-6 mx-auto"}
       (map
        (fn [post]
          [:div {:key (:id post) :class "card mb-2"}
           [:div {:class "post card-body"}
            [:div {:class "row"}
             [:div {:class "col-3"}
              [:img {:src (:creator_avatar post) :width 100 :height 100 :class ""}]]
             [:div {:class "col-9 pr-1"}
              [:div {:class "card-title d-flex w-80 justify-content-between" :height 30}
               [:a {:href (str "#" (:id post)) }
                [:h4 {:class "card-title text-muted"} (str "@" (:created_by post))]]
               [:small {:class "text-muted"} (subs (:created_at post) 0 10)]
               ]
              [:p {:class "card-text"} (:body post)]
              [:a {:href "#" :class "card-link float-right"} "like"]]]]])
        posts)])))

(defn timeline-page []
  (let [;name (re-frame/subscribe [:name])
        all-posts (re-frame/subscribe [:posts])]
    (fn []
      [:div {:class "container mt-5"}
       [(blog-form)]
       [(posts @all-posts)]
       ])))
