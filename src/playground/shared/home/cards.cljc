(ns playground.shared.home.cards
  (:require [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once CardsPage
  static om/IQuery
  (query
    [this]
    [:page/title])
  Object
  (render
    [this]
    (dom/div #js {:className "main"}
      (dom/h1 nil "Devcards!"))))
