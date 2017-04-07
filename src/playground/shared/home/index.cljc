(ns playground.shared.home.index
  (:require [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once IndexPage
  static om/IQuery
  (query
    [this]
    [:page/title])
  Object
  (render
    [this]
    (dom/div #js {:className "main"}
      (dom/h1 nil "CIS 598 Requirements Application Project")
      (dom/div nil
        (dom/div nil
          (dom/h3 nil "Section 1"))))))
