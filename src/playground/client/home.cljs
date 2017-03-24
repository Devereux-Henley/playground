(ns playground.client.home
  (:require
   [bidi.bidi :as bidi]
   [compassus.core :as compassus]
   [goog.dom :as gdom]
   [om.next :as om]
   [playground.shared.home :as home]))

(defonce mounted? (atom false))

(defn init
  []
  (if-not @mounted?
    (do
      (compassus/mount! home/app (gdom/getElement "app"))
      (swap! mounted? not))
    (let [route->component (-> home/app :config :route-component)
          component (om/class->any (compassus/get-reconciler home/app) (get route->component (compassus/current-route home/app)))]
      (.forceUpdate component))))
