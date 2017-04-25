(ns playground.server.user-group-relations
  (:require
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.user-group-relations :refer [map->UserGroupRelation]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.server.util :refer [merge-base-defaults]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema UserGroupRelation
  {:user-id Integer
   :group-id Integer})

(defn new-user-group-relation-base-resource
  [user-group-relation-resource]
  (yada/resource
    (merge-base-defaults
      "user-group-relations"
      {:methods
       {:get {:produces standard-outputs
              :swagger/tags ["user-group-relations" "list"]
              :response (fn [ctx] (list-record user-group-relation-resource))}
        :put {:parameters {:body UserGroupRelation}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["user-group-relations" "create"]
              :response (fn [ctx]
                          (create-record
                            user-group-relation-resource
                            (map->UserGroupRelation
                              (get-in ctx [:parameters :body]))))}
        :delete {:parameters {:body UserGroupRelation}
                 :consumes standard-inputs
                 :produces standard-outputs
                 :swagger/tags ["user-group-relations" "delete"]
                 :response (fn [ctx]
                             (delete-record
                               user-group-relation-resource
                               (map->UserGroupRelation
                                 (get-in ctx [:parameters :body]))))}}})))

(defn user-group-relation-api-routes
  [user-group-relation-resource {:keys [port]}]
  (let [api-routes ["/user-group-relations"
                    [
                     ["" (new-user-group-relation-base-resource user-group-relation-resource)]
                     ["/" (yada/redirect :playground.resources/user-group-relations-base)]
                     ]]]
    api-routes
    ))
