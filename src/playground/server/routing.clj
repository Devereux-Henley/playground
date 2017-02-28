(ns playground.server.routing
  (:require [bidi.bidi :refer [tag]]
            [playground.server.requirements :refer [requirements-routes]]
            [yada.yada :refer [handler resource] :as yada]))

(defn content-routes
  []
  ["/"
   [
    ["index.html"
     (yada/resource
      {:id :edge.resources/index
       :methods
       {:get
        {:produces #{"text/html"}
         :response (fn [ctx] "<div> Hello World </div>")}}})] 
    ["" (assoc (yada/redirect :edge.resources/index) :id :edge.resources/content)]]])

(defn routes
  [db config]
  [""
   [
    (content-routes)
    (requirements-routes db config)
    [true (handler nil)]]])
                      

    
