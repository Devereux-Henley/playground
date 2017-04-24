(ns playground.server.roles
  (:require
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
  [db-spec]
  (yada/resource
    (merge-base-defaults
      resource-name
      {:methods
       {:get {:produces standard-outputs
              :swagger/tags ["roles" "list"]
              :response (fn [ctx]
                          (api/get-all-roles db-spec))}
        :put {:parameters {:body Role}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["roles" "create"]
              :response (fn [ctx]
                          (api/insert-role! db-spec (map->Role
                                                      (get-in ctx [:parameters :body]))))}}})))

(defn new-role-target-resource
  [db-spec]
  (yada/resource
    (merge-target-defaults
      resource-name
      {:parameters {:path {:role-id Long}}
       :methods
       {:get {:produces standard-outputs
              :swagger/tags ["roles" "read"]
              :response (fn [ctx]
                          (api/get-role-by-id db-spec
                            (get-in ctx [:parameters :path :role-id])))}
        :put {:parameters {:body Role}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["roles" "update"]
              :response (fn [ctx]
                          (api/update-role-by-id!
                            db-spec
                            (get-in ctx[:parameters :path :role-id])
                            (map->Role (get-in ctx [:parameters :body]))))}
        :delete {:produces standard-outputs
                 :swagger/tags ["roles" "delete"]
                 :response (fn [ctx]
                             (api/delete-role-by-id! db-spec
                               (get-in ctx [:parameters :path :role-id])))}}})))

(defn role-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes [(str "/" resource-name)
                    [
                     ["" (new-role-base-resource db-spec)]
                     ["/" (yada/redirect :playground.resources/roles-base)]
                     [["/" [#"\d+" :role-id]] (new-role-target-resource db-spec)]
                     ]]]
    [""
     [
      api-routes
      ]]))
