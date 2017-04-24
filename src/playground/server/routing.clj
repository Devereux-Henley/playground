(ns playground.server.routing
  (:require [bidi.bidi :refer [tag]]
            [clojure.java.io :as io]
            [playground.server.authorization :refer [authorization-api-routes]]
            [playground.server.home :refer [home-content-routes home-api-routes]]
            [playground.server.organizations :refer [organization-api-routes]]
            [playground.server.projects :refer [project-content-routes project-api-routes]]
            [playground.server.requirements :refer [requirement-api-routes]]
            [playground.server.roles :refer [role-api-routes]]
            [playground.server.team-members :refer [team-member-api-routes]]
            [playground.server.users :refer [user-api-routes]]
            [playground.shared.util :refer [server-send]]
            [yada.resources.webjar-resource :refer [new-webjar-resource]]
            [yada.swagger :as swagger]
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

;; Routes of the entire application

(defn routes
  [db-spec {:keys [port] :as config}]
  (let [api-routes ["/api"
                    [
                     (authorization-api-routes db-spec config)
                     (user-api-routes db-spec config)
                     (organization-api-routes db-spec config)
                     (team-member-api-routes db-spec config)
                     (requirement-api-routes db-spec config)
                     (role-api-routes db-spec config)
                     (project-api-routes db-spec config)
                     (home-api-routes db-spec config)
                     ]]]
    [""
     [
      (home-content-routes db-spec config)
      (project-content-routes db-spec config)
      api-routes
      ["/swagger"
       (->
         api-routes
         (yada/swaggered
           {:info {:title "Playground"
                   :version "1.0"
                   :description "API routes for playground application."}
            :host (format "localhost:%d" port)
            :schemes ["http"]
            :basePath "/swagger"})
         (tag :playground.resources/swagger-json))]

      ["/swagger-ui" (-> (new-webjar-resource "/swagger-ui")
                   (tag :playground.resources/swagger))]

      (content-routes db-spec config)
      [true (handler nil)]]]))
