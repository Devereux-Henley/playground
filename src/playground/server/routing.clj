(ns playground.server.routing
  (:require [bidi.bidi :refer [tag]]
            [clojure.java.io :as io]
            [playground.server.home :refer [home-content-routes home-api-routes]]
            [playground.server.projects :refer [project-content-routes project-api-routes]]
            [playground.server.requirements :refer [requirement-api-routes]]
            [playground.shared.util :refer [server-send]]
            [yada.yada :refer [handler resource] :as yada]))

(defn content-routes
  [db-spec config]
  [""
   [
    ["" (yada/redirect :playground.resources/index)]
    ["/" (yada/redirect :playground.resources/index)]
    ["/index.html" (yada/redirect :playground.resources/index)]
    ["/"
     (->
       (yada/as-resource (io/file "static"))
       (assoc :id :playground.resources/static))]]])

(defn routes
  [db-spec config]
  [""
   [
    (home-content-routes db-spec config)
    (project-content-routes db-spec config)
    ["/api"
     [
      (requirement-api-routes db-spec config)
      (project-api-routes db-spec config)
      (home-api-routes db-spec config)
      ]]

    (content-routes db-spec config)
    [true (handler nil)]]])
