(ns playground.server.team-projects
  (:require
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.team-projects :as api :refer [map->TeamProject]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema TeamProject
  {:project-id Integer
   :team-id Integer})

(defn new-team-project-base-resource
  [team-project-resource]
  (yada/resource
    {:id :playground.resources/team-projects-base
     :description "Serves CREATE, LIST, and DELETE capabilities for team-project data."
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["team-projects" "list"]
            :response (fn [ctx] (list-record team-project-resource))}
      :put {:parameters {:body TeamProject}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["team-projects" "create"]
            :response (fn [ctx]
                        (create-record team-project-resource (map->TeamProject
                                                               (get-in ctx [:parameters :body]))))}
      :delete {:parameters {:body TeamProject}
               :consumes standard-inputs
               :produces standard-outputs
               :swagger/tags ["team-projects" "delete"]
               :response (fn [ctx]
                           (delete-record team-project-resource (map->TeamProject
                                                                  (get-in ctx [:parameters :body]))))}}}))

(defn team-project-api-routes
  [team-project-resource {:keys [port]}]
  (let [api-routes ["/team-projects"
                    [
                     ["" (new-team-project-base-resource team-project-resource)]
                     ["/" (yada/redirect :playground.resources/team-projects-base)]
                     ]]]
    api-routes
    ))
