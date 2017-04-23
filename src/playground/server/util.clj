(ns playground.server.util
  (:require
   [playground.server.constants :refer [standard-outputs]]))

(defn merge-base-defaults
  [resource configured-map]
  (merge
    {:id (keyword (str "playground.resources/" resource "-base"))
     :description (str "Serves LIST and CREATE capabilities for " resource)
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]}
    configured-map))

(defn merge-target-defaults
  [resource configured-map]
  (merge
    {:id (keyword (str "playground.resources/" resource "-target"))
     :description (str "Serves READ, UPDATE, and DELETE capabilities for " resource)
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]}
    configured-map))
