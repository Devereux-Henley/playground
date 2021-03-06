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
  {:requirement-name String
   :requirement-description String
   :requirement-project Integer})

(defschema RequirementUpdate
  {:requirement-name String
   :requirement-description String})

(defschema RequirementsPath
  {:ancestor-id Integer
   :descendant-id Integer})

(defn new-project-requirement-base-resource
  [requirement-resource]
  (yada/resource
    (merge-base-defaults
      project-requirement-resource-name
      {:parameters {:path {:project-id Long}
                    :query {(schema/optional-key :top) Boolean}}
       :access-control
       {:scheme :basic-auth
        :authorization {:methods {:get :user}}}
       :methods
       {:get {:produces standard-outputs
              :swagger/tags ["requirements"
                             "list"]
              :response (fn [ctx]
                          (let [project-id (get-in ctx [:parameters :path :project-id])
                                top (get-in ctx [:parameters :query :top])]
                            (if top
                              (api/get-top-level-requirements-in-project
                                requirement-resource
                                project-id)
                              (api/get-requirements-by-project-id
                                requirement-resource
                                project-id))))}}})))

(defn new-requirement-base-resource
  [requirement-resource]
  (yada/resource
    {:id :playground.resources/requirements-base
     :description "Serves CRUD capabilities for requirements"
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :access-control
     {:scheme :basic-auth
      :authorization {:methods {:put :user}}}
     :methods
     {:put {:parameters {:query {(schema/optional-key :parent) Long}
                         :body Requirement}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["requirements" "create"]
            :response (fn [ctx]
                        (let [body (get-in ctx [:parameters :body])]
                          (if-let [parent (get-in ctx [:parameters :query :parent])]
                            (api/insert-requirement-child! requirement-resource parent body)
                            (api/insert-root-requirement! requirement-resource body))))}}
     }))

(defn new-requirement-target-resource
  [requirement-resource]
  (yada/resource
    {:id :playground.resources/requirements-target
     :description "Serves CRUD capabilities for requirements"
     :parameters {:path {:requirement-id Long}}
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :access-control
     {:scheme :basic-auth
      :authorization {:methods {:get :user
                                :post :user
                                :delete :user}}}
     :methods
     {:get {:produces standard-outputs
            :parameters {:query {(schema/optional-key :ancestor) Boolean
                                 (schema/optional-key :descendant) Boolean}}
            :swagger/tags ["requirements" "read"]
            :response (fn [ctx]
                        (let [ancestor (get-in ctx [:parameters :query :ancestor])
                              descendant (get-in ctx [:parameters :query :descendant])
                              requirement-id (get-in ctx [:parameters :path :requirement-id])]
                          (cond
                            ancestor
                            (api/get-ancestors-by-id requirement-resource requirement-id)
                            descendant
                            (api/get-descendants-by-id requirement-resource requirement-id)
                            :else
                            (api/get-requirement-by-id requirement-resource requirement-id))))}
      :post {:consumes standard-inputs
             :produces standard-outputs
             :parameters {:body RequirementUpdate}
             :swagger/tags ["requirements" "update"]
             :response (fn [ctx]
                         (api/update-requirement!
                           requirement-resource
                           (get-in ctx [:parameters :path :requirement-id])
                           (get-in ctx [:parameters :body])))}
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
