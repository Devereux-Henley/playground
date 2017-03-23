(ns playground.client.home
  (:require
   [om.next :as om]
   [playground.shared.ui :as ui]))

(defn init
  []
  (ui/navigation-init ui/navigation-reconciler))
