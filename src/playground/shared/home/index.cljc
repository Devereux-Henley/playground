(ns playground.shared.home.index
  (:require [om.dom :as dom]
            [om.next :as om :refer [IQuery IQueryParams defui]]))

(defui ^:once IndexPage
  Object
  (render
    [this]
    (dom/div #js {:className "main"}
      (dom/h1 nil "CIS 598 Requirements Application Project"))))
