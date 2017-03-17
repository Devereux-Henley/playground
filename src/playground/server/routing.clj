(ns playground.server.routing
  (:require [bidi.bidi :refer [tag]]
            [clojure.java.io :as io]
            [playground.server.projects :refer [project-content-routes project-api-routes]]
            [playground.server.ui :refer [navigation-api-routes]]
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
    [""
     (->
       (yada/as-resource (io/file "static"))
       (assoc :id :playground.resources/static))]]])

(defn routes
  [db config]
  [""
   [
    (project-content-routes db config)

    ["/api"
     [
      (project-api-routes db config)
      (navigation-api-routes db config)
      ]]

    (content-routes db)
    [true (handler nil)]]])
