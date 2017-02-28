(ns playground.client.main
  (:require 
    [goog.dom :as gdom]
    [om.next :as om]))

(defn init
  []
  (om/add-root!
    reconciler
    Counter
    (gdom/getElement "app")))
