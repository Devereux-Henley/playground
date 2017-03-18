(ns playground.client.requirements
  (:require
   [om.next :as om]
   [playground.shared.projects :as p]
   [playground.shared.ui :as ui]))

(defn init
  []
  (ui/navigation-init ui/navigation-reconciler)
  (p/projects-init p/project-reconciler))
