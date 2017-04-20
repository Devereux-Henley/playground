(ns playground.server.roles
  (:require
   [playground.server.api.roles :as api :refer [map->Role]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.server.util :refer [merge-base-defaults]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema Role
  {:token String
   :description String
   :default-value Boolean})

(defn new-role-base-resource
  [db-spec]
  (yada/resource
    (merge-base-defaults
      {:methods
       {:get {:produces standard-outputs
              :response (fn [ctx]
                          (api/get-all-roles db-spec))}
        :put {:parameters {:body Role}
              :consumes standard-inputs
              :produces standard-outputs
              :response (fn [ctx]
                          (api/insert-role! db-spec (map->Role
                                                      (get-in ctx [:parameters :body]))))}}})))
