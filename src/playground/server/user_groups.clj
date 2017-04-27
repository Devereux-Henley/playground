(ns playground.server.user-groups
  (:require
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.user-groups :as api :refer [map->UserGroup]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.server.util :refer [merge-base-defaults merge-target-defaults]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defonce resource-name "user-groups")

(defschema UserGroup
  {:name String
   :project-id Integer})

(defn new-user-group-base-resource
  [user-group-resource]
  (yada/resource
    (merge-base-defaults
      resource-name
      {:methods
       {:get {:produces standard-outputs
              :swagger/tags ["user-groups" "list"]
              :response (fn [ctx]
                          (list-record user-group-resource))}
        :put {:parameters {:body UserGroup}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["user-groups" "create"]
              :response (fn [ctx]
                          (create-record user-group-resource (map->UserGroup
                                                      (get-in ctx [:parameters :body]))))}}})))

(defn new-user-group-target-resource
  [user-group-resource]
  (yada/resource
    (merge-target-defaults
      resource-name
      {:parameters {:path {:user-group-id Long}}
       :methods
       {:get {:produces standard-outputs
              :swagger/tags ["user-groups" "read"]
              :response (fn [ctx]
                          (read-record user-group-resource
                            (get-in ctx [:parameters :path :user-group-id])))}
        :put {:parameters {:body UserGroup}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["user-groups" "update"]
              :response (fn [ctx]
                          (update-record user-group-resource
                            (get-in ctx[:parameters :path :user-group-id])
                            (map->UserGroup (get-in ctx [:parameters :body]))))}
        :delete {:produces standard-outputs
                 :swagger/tags ["user-groups" "delete"]
                 :response (fn [ctx]
                             (delete-record user-group-resource
                               (get-in ctx [:parameters :path :user-group-id])))}}})))

(defn user-group-api-routes
  [user-group-resource {:keys [port]}]
  (let [api-routes [(str "/" resource-name)
                    [
                     ["" (new-user-group-base-resource user-group-resource)]
                     ["/" (yada/redirect :playground.resources/user-groups-base)]
                     [["/" [#"\d+" :user-group-id]] (new-user-group-target-resource user-group-resource)]
                     ]]]
    [""
     [
      api-routes
      ]]))
