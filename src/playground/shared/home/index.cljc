(ns playground.shared.home.index
  (:require [om.dom :as dom]
            [om.next :as om :refer [IQuery IQueryParams defui]]))

(defui IndexPage
  static IQuery
  (query
    [this]
    [:index/title])
  Object
  (render
    [this]
    (let [{:keys [index/title]} (om/props this)]
      (dom/div #js {:className "main"}
        (dom/div nil title)
        (dom/div nil "Home")))))
