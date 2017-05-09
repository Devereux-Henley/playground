(ns playground.server.roles
  (:require
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.roles :as api :refer [map->Role]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.server.util :refer [merge-base-defaults merge-target-defaults]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defonce resource-name "roles")

(defschema Role
  {:token String
   :description String
   :default-value Boolean})

(defn new-role-base-resource
  [role-resource]
  (yada/resource
    (merge-base-defaults
      resource-name
      {:methods
       {:get {:produces standard-outputs
              :swagger/tags ["roles" "list"]
              :response (fn [ctx]
                          (list-record role-resource))}
        :put {:parameters {:body Role}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["roles" "create"]
              :response (fn [ctx]
                          (create-record role-resource (map->Role
                                                         (get-in ctx [:parameters :body]))))}}})))

(defn new-role-target-resource
  [role-resource]
  (yada/resource
    (merge-target-defaults
      resource-name
      {:parameters {:path {:role-id Long}}
       :methods
       {:get {:produces standard-outputs
              :swagger/tags ["roles" "read"]
              :response (fn [ctx]
                          (read-record role-resource
                            (get-in ctx [:parameters :path :role-id])))}
        :put {:parameters {:body Role}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["roles" "update"]
              :response (fn [ctx]
                          (update-record role-resource
                            (get-in ctx[:parameters :path :role-id])
                            (map->Role (get-in ctx [:parameters :body]))))}
        :delete {:produces standard-outputs
                 :swagger/tags ["roles" "delete"]
                 :response (fn [ctx]
                             (delete-record role-resource
                               (get-in ctx [:parameters :path :role-id])))}}})))

(defn role-api-routes
  [role-resource {:keys [port]}]
  (let [api-routes [(str "/" resource-name)
                    [
                     ["" (new-role-base-resource role-resource)]
                     ["/" (yada/redirect :playground.resources/roles-base)]
                     [["/" [#"\d+" :role-id]] (new-role-target-resource role-resource)]
                     ]]]
    [""
     [
      api-routes
      ]]))
