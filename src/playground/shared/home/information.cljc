(ns playground.shared.home.information
    (:require [om.dom :as dom]
              [om.next :as om :refer [defui]]))

(defui ^:once InformationPage
  Object
  (render
    [this]
    (dom/div #js {:className "main"}
      (dom/h1 nil "Information")
      (dom/div nil
        (dom/h3 nil "Data Flow")
        (dom/p nil "Data Flow Caption"))
      (dom/div nil
        (dom/h3 nil "Namespace Structure")
        (dom/p nil "Namespace Structure Caption"))
      (dom/div nil
        (dom/h3 nil "Database Structure")
        (dom/p nil "Database Structure Caption")))))
