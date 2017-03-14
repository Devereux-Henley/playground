(ns playground.server.routing
  (:require [bidi.bidi :refer [tag]]
            [clojure.java.io :as io]
            [playground.server.projects :refer [projects-routes]]
            [playground.shared.util :refer [server-send]]
            [yada.yada :refer [handler resource] :as yada]))

(defn content-routes
  [db-spec]
  ["/"
   [
    [""
     (yada/resource
       {:id :playground.resources/index
        :methods
        {:get
         {:produces #{"text/html"}
          :response (fn [ctx] "<div> Hello World </div>")}}})]
    ["index.html" (assoc (yada/redirect :playground.resources/index) :id :playground.resources/content)]
    ["static/"
     (->
       (yada/as-resource (io/file "static"))
       (assoc :id :playground.resources/static))]]])

(defn routes
  [db config]
  [""
   [
    (projects-routes db config)
    (content-routes db)
    [true (handler nil)]]])
