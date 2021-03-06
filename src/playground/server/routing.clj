(ns playground.server.routing
  (:require [bidi.bidi :refer [tag]]
            [clojure.java.io :as io]
            [playground.server.authorization :refer [authorization-api-routes]]
            [playground.server.home :refer [home-content-routes home-api-routes]]
            [playground.server.organizations :refer [organization-api-routes]]
            [playground.server.organization-users :refer [organization-user-api-routes]]
            [playground.server.projects :refer [project-api-routes]]
            [playground.server.requirements :refer [requirement-api-routes]]
            [playground.server.roles :refer [role-api-routes]]
            [playground.server.team-members :refer [team-member-api-routes]]
            [playground.server.team-projects :refer [team-project-api-routes]]
            [playground.server.users :refer [user-api-routes]]
            [playground.server.user-groups :refer [user-group-api-routes]]
            [playground.server.user-group-relations :refer [user-group-relation-api-routes]]
            [playground.server.user-group-role-relations :refer [user-group-role-relation-api-routes]]
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
  [resources jwt-secret db-spec {:keys [port] :as config}]
  (let [api-routes [""
                    [
                     (authorization-api-routes db-spec config)
                     (user-api-routes (:users resources) config)
                     (organization-api-routes (:organizations resources) config)
                     (organization-user-api-routes (:organization-users resources) config)
                     (team-member-api-routes (:team-members resources) config)
                     (team-project-api-routes (:team-projects resources) config)
                     (requirement-api-routes (:requirements resources) config)
                     (role-api-routes (:roles resources) config)
                     (user-group-api-routes (:user-groups resources) config)
                     (user-group-relation-api-routes (:user-group-relations resources) config)
                     (user-group-role-relation-api-routes (:user-group-role-relations resources) config)
                     (project-api-routes
                       (:projects resources)
                       (:requirements resources)
                       config)
                     (home-api-routes resources config)
                     ]]]
    [""
     [
      (home-content-routes resources jwt-secret config)
      ["/api"
       (->
         api-routes
         (yada/swaggered
           {:info {:title "Playground"
                   :version "1.0"
                   :description "API routes for playground application."}
            :host (format "localhost:%d" port)
            :schemes ["http"]
            :tags [{:name "organizations"
                    :description "All paths for organization resources."}
                   {:name "organization-users"
                    :description "All paths for organization-user relation resources"}
                   {:name "users"
                    :description "All paths for user resources."}
                   {:name "user-groups"
                    :description "All paths for user group resources"}
                   {:name "user-group-relations"
                    :description "All paths for user group relation resources."}
                   {:name "user-group-role-relations"
                    :description "All paths for user group role relation resources."}
                   {:name "roles"
                    :description "All paths for auth role resources."}
                   {:name "requirements"
                    :description "All paths for requirement resources."}
                   {:name "team-members"
                    :description "All paths for team-member resources."}
                   {:name "team-projects"
                    :description "All paths for team-project resources."}
                   {:name "projects"
                    :description "All paths for project resources."}
                   {:name "read"
                    :description "All READ routes."}
                   {:name "create"
                    :description "All CREATE routes."}
                   {:name "update"
                    :description "All UPDATE routes."}
                   {:name "list"
                    :description "All LIST routes."}
                   {:name "delete"
                    :description "All DELETE routes."}]
            :basePath "/api"})
         (tag :playground.resources/swagger-json))]

      ["/swagger-ui" (-> (new-webjar-resource "/swagger-ui")
                   (tag :playground.resources/swagger))]

      (content-routes db-spec config)
      [true (handler nil)]]]))
