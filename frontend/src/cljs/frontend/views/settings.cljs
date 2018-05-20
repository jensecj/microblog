(ns frontend.views.settings
  (:require [re-frame.core :as re-frame :refer [dispatch]]
            [reagent.core :as reagent]
            [frontend.views.common :as common]))

(defn- settings-pane []
  (fn []
    [:div {:class "container"}
     [:form
      [:div.form-group
       [:label {:for "emailInput"} "Email address"]
       [:input#emailInput.form-control
        {:placeholder "Enter email",
         :aria-describedby "emailHelp",
         :type "email"}]
       [:small#emailHelp.form-text.text-muted
        "We'll never share your email with anyone else."]]
      [:div.form-group
       [:label {:for "passwordInput"} "Password"]
       [:input#passwordInput.form-control
        {:placeholder "Password", :type "password"}]]
      [:div.form-group
       [:label {:for "someSelection"} "Example select"]
       [:select#someSelection.form-control
        [:option "1"]
        [:option "2"]
        [:option "3"]
        [:option "4"]
        [:option "5"]]]
      [:div.form-group
       [:label {:for "exampleTextarea"} "Signature"]
       [:textarea#exampleTextarea.form-control {:rows "3"}]]
      [:div.form-group
       [:label {:for "exampleInputFile"} "Avatar"]
       [:input#exampleInputFile.form-control-file
        {:aria-describedby "fileHelp", :type "file"}]
       [:small#fileHelp.form-text.text-muted
        "This is some placeholder block-level help text for the above input. It's a bit lighter and easily wraps to a new line."]]
      [:fieldset.form-group
       [:legend "Radio buttons"]
       [:div.form-check
        [:label.form-check-label
         [:input#optionsRadios1.form-check-input
          {:checked "checked",
           :value "option1",
           :name "optionsRadios",
           :type "radio"}]
         "Option one is this and that—be sure to include why it's great"]]
       [:div.form-check
        [:label.form-check-label
         [:input#optionsRadios2.form-check-input
          {:value "option2", :name "optionsRadios", :type "radio"}]
         "Option two can be something else and selecting it will deselect option one\n      "]]
       [:div.form-check.disabled
        [:label.form-check-label
         [:input#optionsRadios3.form-check-input
          {:disabled "disabled",
           :value "option3",
           :name "optionsRadios",
           :type "radio"}]
         "Option three is disabled"]]]
      [:div.form-check
       [:label.form-check-label
        [:input.form-check-input {:type "checkbox"}]
        "Use Two Factor Authentication"]]
      [:button.btn.btn-primary {:class "mt-2" :type "submit"} "Submit"]]]))

(defn- settings-tabs []
  (fn []
    [:div {:class "container mb-2"}
     [:ul {:class "nav nav-tabs"}
      [:li {:class "nav-item" :data-toggle "tab"}
       [:a {:class "nav-link active"} "item 1"]]
      [:li {:class "nav-item" :data-toggle "tab"}
       [:a {:class "nav-link"} "item 2"]]
      [:li {:class "nav-item" :data-toggle "tab"}
       [:a {:class "nav-link"} "item 3"]]
      ]]))

(defn settings-page []
  (fn []
    [:div {:class "container mt-5"}
     [(settings-pane)]]))
