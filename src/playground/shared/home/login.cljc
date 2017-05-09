(ns playground.shared.home.login
  (:require
   [om.dom :as dom]
   [om.next :as om :refer [defui]]))

(defui ^:once LoginPage
  Object
  (render
    [this]
    (dom/div #js {:className "login"}
      (dom/h1 nil "Login Page")
      (dom/div nil
        (dom/label nil
          "Username"
          (dom/input
            #js {:type "text"}))
        (dom/label nil
          "Password"
          (dom/input
            #js {:type "password"}))))))
