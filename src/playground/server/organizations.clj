(ns playground.server.organizations
  (:require
   [playground.server.api.organizations :as api :refer [map->Organization]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema Organization
  {:organization-name String
   :organization-description String})

(defn new-organization-base-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/organizations-base
     :description "Serves CREATE and LIST capabilities for organization data"
     :produces [{:media-type
                 #{"application/json;q=0.8" "application/edn;q=0.9" "application/transit+json;q=0.9"}}]
     :methods
     {:get {:produces #{"application/json"}
            :response (fn [ctx] (api/get-all-organizations db-spec))}
      :put {:parameters {:body Organization}
            :consumes #{"application/json;q=0.9" "application/transit+json;q=0.8"}
            :produces #{"application/json;q=0.9" "application/transit+json;q=0.8"}
            :response (fn [ctx]
                        (api/insert-organization! db-spec (map->Organization
                                                            (get-in ctx [:parameters :body]))))}}}))

(defn new-requirement-target-resource
  [db-spec])

(defn organization-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/organizations"
                    [
                     ["" (new-organization-base-resource db-spec)]
                     ["/" (yada/redirect :playground.resources/organizations-base)]
                     ]]]
    [""
     [
      api-routes
      ]]))
