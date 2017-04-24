(ns playground.server.organizations
  (:require
   [playground.server.api.organizations :as api :refer [map->Organization]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema Organization
  {:name String
   :description String})

(defschema PartialOrganization
  {(schema/optional-key :name) String
   (schema/optional-key :description) String})

(defn new-organization-base-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/organizations-base
     :description "Serves CREATE and LIST capabilities for organization data."
     :produces [{:media-type standard-outputs}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["organizations" "list"]
            :response (fn [ctx] (api/get-all-organizations db-spec))}
      :put {:parameters {:body Organization}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["organizations" "create"]
            :response (fn [ctx]
                        (api/insert-organization! db-spec (map->Organization
                                                            (get-in ctx [:parameters :body]))))}}}))

(defn new-organization-target-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/organizations-target
     :description "Serves READ, UPDATE, and DELETE capabilities for organization data."
     :parameters {:path {:org-id Long}}
     :produces [{:media-type standard-outputs}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["organizations" "read"]
            :response (fn [ctx]
                        (api/get-organization-by-id db-spec
                          (get-in ctx [:parameters :path :org-id])))}
      :put   {:parameters {:body Organization}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["organizations" "update"]
              :response (fn [ctx]
                          (api/update-organization-by-id!
                            db-spec
                            (get-in ctx [:parameters :path :org-id])
                            (map->Organization (get-in ctx [:parameters :body]))))}
      :patch {:parameters {:body PartialOrganization}
              :consumes standard-inputs
              :produces standard-outputs
              :response (fn [ctx]
                          (api/update-organization-by-id!
                            db-spec
                            (get-in ctx [:parameters :path :org-id])
                            (get-in ctx [:parameters :body])))}}}))

(defn organization-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/organizations"
                    [
                     ["" (new-organization-base-resource db-spec)]
                     ["/" (yada/redirect :playground.resources/organizations-base)]
                     [["/" [#"\d+" :org-id]] (new-organization-target-resource db-spec)]
                     ]]]
    api-routes
    ))
