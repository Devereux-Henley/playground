(ns playground.server.routing
  (:require [bidi.bidi :refer [tag]]
            [clojure.java.io :as io]
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
    ["" (assoc (yada/redirect :edge.resources/index) :id :edge.resources/content)]
    [""
     (->
       (yada/as-resource (io/file "static"))
       (assoc :id :edge.resources/static))]]])

(defn routes
  [db config]
  [""
   [    
    (requirements-routes db config)
    (content-routes)
    [true (handler nil)]]])

