(ns playground.server.organization-users
  (:require
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.organization-users :as api :refer [map->OrganizationUser]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema OrganizationUser
  {:user-id Integer
   :organization-id Integer})

(defn new-organization-user-base-resource
  [organization-user-resource]
  (yada/resource
    {:id :playground.resources/organization-users-base
     :description "Serves CREATE, LIST, and DELETE capabilities for organization-user data."
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["organization-users" "list"]
            :response (fn [ctx] (list-record organization-user-resource))}
      :put {:parameters {:body OrganizationUser}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["organization-users" "create"]
            :response (fn [ctx]
                        (create-record organization-user-resource (map->OrganizationUser
                                                              (get-in ctx [:parameters :body]))))}
      :delete {:parameters {:body OrganizationUser}
               :consumes standard-inputs
               :produces standard-outputs
               :swagger/tags ["organization-users" "delete"]
               :response (fn [ctx]
                           (delete-record organization-user-resource (map->OrganizationUser
                                                                 (get-in ctx [:parameters :body]))))}}}))

(defn organization-user-api-routes
  [organization-user-resource {:keys [port]}]
  (let [api-routes ["/organization-users"
                    [
                     ["" (new-organization-user-base-resource organization-user-resource)]
                     ["/" (yada/redirect :playground.resources/organization-users-base)]
                     ]]]
    api-routes
    ))
