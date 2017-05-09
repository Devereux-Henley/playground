(ns playground.server.organizations
  (:require
   [playground.server.api.organizations :as api :refer [map->Organization]]
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
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
  [organization-resource]
  (yada/resource
    {:id :playground.resources/organizations-base
     :description "Serves CREATE and LIST capabilities for organization data."
     :produces [{:media-type standard-outputs}]
     :access-control
     {:scheme :basic-auth
      :authorization {:methods {:get :user
                                :put :user}}}
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["organizations" "list"]
            :response (fn [ctx]
                        (list-record organization-resource))}
      :put {:parameters {:body Organization}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["organizations" "create"]
            :response (fn [ctx]
                        (create-record organization-resource (map->Organization
                                                               (get-in ctx [:parameters :body]))))}}}))

(defn new-organization-target-resource
  [organization-resource]
  (yada/resource
    {:id :playground.resources/organizations-target
     :description "Serves READ, UPDATE, and DELETE capabilities for organization data."
     :parameters {:path {:org-id Long}}
     :produces [{:media-type standard-outputs}]
     :access-control
     {:scheme :basic-auth
      :authorization {:methods {:get :user
                                :put :user}}}
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["organizations" "read"]
            :response (fn [ctx]
                        (read-record organization-resource
                          (get-in ctx [:parameters :path :org-id])))}
      :put   {:parameters {:body Organization}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["organizations" "update"]
              :response (fn [ctx]
                          (update-record
                            organization-resource
                            (get-in ctx [:parameters :path :org-id])
                            (map->Organization (get-in ctx [:parameters :body]))))}}}))

(defn organization-api-routes
  [organization-resource {:keys [port]}]
  (let [api-routes ["/organizations"
                    [
                     ["" (new-organization-base-resource organization-resource)]
                     ["/" (yada/redirect :playground.resources/organizations-base)]
                     [["/" [#"\d+" :org-id]] (new-organization-target-resource organization-resource)]
                     ]]]
    api-routes
    ))
