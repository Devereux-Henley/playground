(ns playground.server.requirements
  (:require
   [playground.server.api.requirements :as api]
   [schema.core :as schema]
   [yada.yada :as yada]))

(defn new-requirement-list-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/requirements
     :description "Serves CRUD capabilities for requirements"
     :produces [{:media-type
                 #{"text/plain" "text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.9"}
                 :charset "UTF-8"}]
     :methods
     {:get {:response (fn [ctx]
                        "Hello")}}}))

(defn new-requirement-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/requirements-CRUD
     :description "Serves CRUD capabilities for requirements"
     :parameters {:path {:req-id Long}}
     :produces [{:media-type
                 #{"text/plain" "text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.9"}
                 :charset "UTF-8"}]
     :methods
     {:get {:response (fn [ctx]
                        (api/get-requirement-by-id db-spec (get-in ctx [:parameters :path :req-id])))}}}))

(defn requirement-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/requirements"
                    [
                     ["" (new-requirement-list-resource db-spec)]
                     ["/" (yada/redirect :playground.resources/requirements)]
                     [["/" [#"\d+" :req-id]] (new-requirement-resource db-spec)]
                     ]]]
    [""
     [
      api-routes
      ]]))
