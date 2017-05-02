(ns playground.server.requirements
  (:require
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.requirements :as api]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.server.util :refer [merge-base-defaults merge-target-defaults]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defonce project-requirement-resource-name "project-requirements")

(defschema Requirement
  {:requirement-project Integer})

(defschema RequirementEdit
  {:name String
   :description String
   :requirement-id Integer
   :edit-type (schema/enum "edit" "delete" "restore")})

(defschema RequirementsPath
  {:ancestor-id Integer
   :descendant-id Integer})

(defn new-project-requirement-base-resource
  [requirement-resource]
  (yada/resource
    (merge-base-defaults
      project-requirement-resource-name
      {:parameters {:path {:project-id Long}}
       :methods
       {:get {:produces standard-outputs
              :swagger/tags ["requirements"
                             "list"]
              :response (fn [ctx]
                          (api/get-requirements-by-project-id
                            requirement-resource
                            (get-in ctx [:parameters :path :project-id])))}}})))

(defn new-requirement-base-resource
  [requirement-resource]
  (yada/resource
    {:id :playground.resources/requirements-base
     :description "Serves CRUD capabilities for requirements"
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:put {:parameters {:query {(schema/optional-key :parent) Integer}
                         :body Requirement}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["requirements" "create"]
            :response (fn [ctx]
                        (if-let [parent (get-in ctx [:parameters :query :parent])]
                          (api/insert-requirement-child! requirement-resource parent (get-in ctx [:parameters :body]))
                          (api/insert-root-requirement! requirement-resource (get-in ctx [:parameters :body]))))}}
     }))

(defn new-requirement-target-resource
  [requirement-resource]
  (yada/resource
    {:id :playground.resources/requirements-target
     :description "Serves CRUD capabilities for requirements"
     :parameters {:path {:requirement-id Long}}
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["requirements" "read"]
            :response (fn [ctx]
                        (api/get-requirement-by-id requirement-resource (get-in ctx [:parameters :path :requirement-id])))}
      :patch {:produces standard-outputs
              :swagger/tags ["requirements" "update"]
              :response (fn [ctx]
                          (api/update-requirement!
                            requirement-resource
                            (get-in ctx [:parameters :path :requirement-id])))}
      :delete {:consumes standard-inputs
               :produces standard-outputs
               :swagger/tags ["requirements" "delete"]
               :response (fn [ctx]
                           (api/delete-requirement!
                             requirement-resource
                             (get-in ctx [:parameters :path :requirement-id])))}}}))

(defn requirement-api-routes
  [requirement-resource {:keys [port]}]
  (let [api-routes ["/requirements"
                    [
                     ["" (new-requirement-base-resource requirement-resource)]
                     ["/" (yada/redirect :playground.resources/requirements-base)]
                     [["/" [#"\d+" :requirement-id]] (new-requirement-target-resource requirement-resource)]
                     ]]]
    api-routes
    ))
