(ns frontend.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :active-page
 (fn [db]
   (:active-page db)))

(re-frame/reg-sub
 :name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 :posts
 (fn [db _]
   (:posts db)))
