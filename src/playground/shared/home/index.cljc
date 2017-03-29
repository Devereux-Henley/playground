(ns playground.shared.home.index
  (:require [om.dom :as dom]
            [om.next :as om :refer [IQuery IQueryParams defui]]))

(defui IndexPage
  Object
  (render
    [this]
    (dom/div #js {:className "main"}
      (dom/div nil "Navigation")
      (dom/div nil "Home"))))
