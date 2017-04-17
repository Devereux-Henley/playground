(ns playground.server.requirements
  (:require
   [playground.server.api.requirements :as api]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema Requirement
  {:requirement-name String
   :requirement-description String
   :requirement-project Integer})

(defschema RequirementsPath
  {:ancestor-id Integer
   :descendant-id Integer})

(defn new-requirement-base-resource
  [db-spec]
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
            :response (fn [ctx]
                        (if-let [parent (get-in ctx [:parameters :query :parent])]
                          (api/insert-requirement-child! db-spec parent (get-in ctx [:parameters :body]))
                          (api/insert-root-requirement! db-spec (get-in ctx [:parameters :body]))))}}
     }))

(defn new-requirement-target-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/requirements-target
     :description "Serves CRUD capabilities for requirements"
     :parameters {:path {:req-id Long}}
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :response (fn [ctx]
                        (api/get-requirement-by-id db-spec (get-in ctx [:parameters :path :req-id])))}
      :patch {:produces standard-outputs
              :response (fn [ctx]
                          (api/update-requirement!
                            db-spec
                            (get-in ctx [:parameters :path :req-id])))}
      :delete {:consumes standard-inputs
               :produces standard-outputs
               :response (fn [ctx]
                           (api/delete-requirement!
                             db-spec
                             (get-in ctx [:parameters :path :req-id])))}}}))

(defn requirement-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/requirements"
                    [
                     ["" (new-requirement-base-resource db-spec)]
                     ["/" (yada/redirect :playground.resources/requirements-base)]
                     [["/" [#"\d+" :req-id]] (new-requirement-target-resource db-spec)]
                     ]]]
    [""
     [
      api-routes
      ]]))
