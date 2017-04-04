(ns playground.shared.home.information
    (:require [om.dom :as dom]
              [om.next :as om :refer [IQuery IQueryParams defui]]))

(defui ^:once InformationPage
  Object
  (render
    [this]
    (dom/div #js {:className "main"}
      (dom/h1 nil "Information"))))
