(ns playground.client.main
  (:require 
   [goog.dom :as gdom]
   [om.next :as om]
   [playground.shared.requirements :as r]))

(defn init
  []
  (r/projects-init r/project-reconciler))

(init)
