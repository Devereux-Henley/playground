(ns playground.server.team-members
  (:require
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.team-members :as api :refer [map->TeamMember]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema TeamMember
  {:user-id Integer
   :team-id Integer})

(defn new-team-member-base-resource
  [team-member-resource]
  (yada/resource
    {:id :playground.resources/team-members-base
     :description "Serves CREATE, LIST, and DELETE capabilities for team-member data."
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["team-members" "list"]
            :response (fn [ctx] (list-record team-member-resource))}
      :put {:parameters {:body TeamMember}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["team-members" "create"]
            :response (fn [ctx]
                        (create-record team-member-resource (map->TeamMember
                                                              (get-in ctx [:parameters :body]))))}
      :delete {:parameters {:body TeamMember}
               :consumes standard-inputs
               :produces standard-outputs
               :swagger/tags ["team-members" "delete"]
               :response (fn [ctx]
                           (delete-record team-member-resource (map->TeamMember
                                                                 (get-in ctx [:parameters :body]))))}}}))

(defn team-member-api-routes
  [team-member-resource {:keys [port]}]
  (let [api-routes ["/team-members"
                    [
                     ["" (new-team-member-base-resource team-member-resource)]
                     ["/" (yada/redirect :playground.resources/team-members-base)]
                     ]]]
    api-routes
    ))
