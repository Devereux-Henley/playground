(ns playground.client.requirements
  (:require
   [om.next :as om]
   [playground.shared.projects :as p]))

(defn init
  []
  (p/projects-init p/project-reconciler))
