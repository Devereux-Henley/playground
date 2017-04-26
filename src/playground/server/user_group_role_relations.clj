(ns playground.server.user-group-role-relations
  (:require
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.user-group-role-relations :refer [map->UserGroupRoleRelation]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.server.util :refer [merge-base-defaults]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema UserGroupRoleRelation
  {:role-id Integer
   :group-id Integer})

(defn new-user-group-role-relation-base-resource
  [user-group-role-relation-resource]
  (yada/resource
    (merge-base-defaults
      "user-group-role-relations"
      {:methods
       {:get {:produces standard-outputs
              :swagger/tags ["user-group-role-relations" "list"]
              :response (fn [ctx] (list-record user-group-role-relation-resource))}
        :put {:parameters {:body UserGroupRoleRelation}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["user-group-role-relations" "create"]
              :response (fn [ctx]
                          (create-record
                            user-group-role-relation-resource
                            (map->UserGroupRoleRelation
                              (get-in ctx [:parameters :body]))))}
        :delete {:parameters {:body UserGroupRoleRelation}
                 :consumes standard-inputs
                 :produces standard-outputs
                 :swagger/tags ["user-group-role-relations" "delete"]
                 :response (fn [ctx]
                             (delete-record
                               user-group-role-relation-resource
                               (map->UserGroupRoleRelation
                                 (get-in ctx [:parameters :body]))))}}})))

(defn user-group-role-relation-api-routes
  [user-group-role-relation-resource {:keys [port]}]
  (let [api-routes ["/user-group-role-relations"
                    [
                     ["" (new-user-group-role-relation-base-resource user-group-role-relation-resource)]
                     ["/" (yada/redirect :playground.resources/user-group-role-relations-base)]
                     ]]]
    api-routes
    ))
