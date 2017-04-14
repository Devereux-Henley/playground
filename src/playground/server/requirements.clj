(ns playground.server.requirements
  (:require
   [playground.server.api.requirements :as api]
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
     :produces [{:media-type
                 #{"text/plain" "text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.9"}
                 :charset "UTF-8"}]
     :methods
     {:get {:response (fn [ctx]
                        "Goodbye")}
      :put {:parameters {:query {(schema/optional-key :parent) Integer}
                         :body Requirement}
            :consumes #{"application/json;q=0.8" "application/transit+json;q=0.9"}
            :produces #{"application/json;q=0.8" "application/transit+json;q=0.9"}
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
     :produces [{:media-type
                 #{"text/plain" "text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.9"}
                 :charset "UTF-8"}]
     :methods
     {:get {:produces #{"application/json;q=0.8"}
            :response (fn [ctx]
                        (api/get-requirement-by-id db-spec (get-in ctx [:parameters :path :req-id])))}
      :patch {:produces #{"application/json;q=0.8"}
              :response (fn [ctx]
                          (api/update-requirement!
                            db-spec
                            (get-in ctx [:parameters :path :req-id])))}
      :delete {:consumes #{"application/json;q=0.8" "application/transit+json;q=0.9"}
               :produces #{"application/json"}
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
