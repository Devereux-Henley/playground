(ns playground.server.team-members
  (:require
   [playground.server.api.team-members :as api :refer [map->TeamMember]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema TeamMember
  {:user-id Integer
   :team-id Integer})

(defn new-team-member-base-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/team-members-base
     :description "Serves CREATE, LIST, and DELETE capabilities for team-member data."
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["team-members" "list"]
            :response (fn [ctx] (api/get-all-team-members db-spec))}
      :put {:parameters {:body TeamMember}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["team-members" "create"]
            :response (fn [ctx]
                        (api/insert-team-member! db-spec (map->TeamMember
                                                           (get-in ctx [:parameters :body]))))}
      :delete {:parameters {:body TeamMember}
               :consumes standard-inputs
               :produces standard-outputs
               :swagger/tags ["team-members" "delete"]
               :response (fn [ctx]
                           (api/delete-team-member! db-spec (map->TeamMember
                                                              (get-in ctx [:parameters :body]))))}}}))

(defn team-member-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/team-members"
                    [
                     ["" (new-team-member-base-resource db-spec)]
                     ["/" (yada/redirect :playground.resources/team-members-base)]
                     ]]]
    api-routes
    ))
