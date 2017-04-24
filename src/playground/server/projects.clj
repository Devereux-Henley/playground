(ns playground.server.projects
  (:require
   [bidi.bidi :as bidi]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.next :as om]
   [playground.server.api.projects :as api :refer [map->Project]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.shared.projects :as r]
   [playground.shared.util :refer [create-om-string server-send]]
   [schema.core :as schema :refer [defschema]]
   [yada.swagger :as swagger]
   [yada.yada :as yada]))

;; Data Templating

(defn sanitize-project-response
  [response]
  (mapv
   (fn [{:keys [name description]} cnt]
     [cnt {:project/name name :project/description description}])
   response
   (iterate inc 1)))

(defn project-page
  [rec-send-func]
  (let [project-reconciler (r/make-reconciler rec-send-func)
        project-string (create-om-string project-reconciler r/ProjectList)]
    (html
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "X-UA-Compatible"}]
      [:title "Projects List"]]
     [:body
      [:section#projects project-string]
      (include-js "/requirements.js")
      (include-css "/requirements.css")])))

;; Om.Next Parsing.

(defmulti read-projects om/dispatch)

(defmethod read-projects :default
  [_ k _]
  {:value {:error (str "No handler for key" k)}})

(defmethod read-projects :organization/organization-name
  [_ _ _]
  {:value "Server Sent Inc."})

(defmethod read-projects :projects/all-projects
  [{:keys [db-spec]} _ _]
  {:value (sanitize-project-response (api/get-all-projects db-spec))})

(def project-parser
  (om/parser {:read read-projects}))

;; Schemas

(defschema Project
  {:name String
   :description String})

(defschema PartialProject
  {(schema/optional-key :name) String
   (schema/optional-key :description) String})

;; Yada Resources

(defn new-index-resource
  [db-spec]
  (let [configured-parser (partial project-parser {:db-spec db-spec})]
    (yada/resource
     {:id :playground.resources/projects-index
      :description "Requirements entries"
      :produces [{:media-type
                  #{"text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.9"}
                  :charset "UTF-8"}]
      :methods
      {:get {:parameters {:query {(schema/optional-key :id) String}}
             :swagger/tags ["default" "getters"]
             :response (fn [ctx]
                         (let [id (get-in ctx [:parameters :query :id])]
                           (case (yada/content-type ctx)
                             "text/html" (project-page
                                          (server-send configured-parser)))))}
       :post {:consumes #{"application/transit+json;q=0.9"}
              :produces #{"application/transit+json;q=0.9"}
              :response (fn [ctx]
                          (configured-parser (:body ctx)))}}})))

(defn new-project-post-resource
  [db-spec]
  (let [configured-parser (partial project-parser {:db-spec db-spec})]
    (yada/resource
     {:id :playground.resources/project-sync-post
      :description "Post route for syncing remote with project state."
      :produces [{:media-type #{"application/json;q=0.9" "application/edn;q=0.9" "application/transit+json;q=0.9"}
                  :charset "UTF-8"}]
      :methods
      {:post {:consumes #{"application/transit+json;q=0.9"}
              :produces #{"application/transit+json;q=0.9"}
              :response (fn [ctx]
                          (configured-parser (:body ctx)))}}})))

(defn new-project-base-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/projects-base
     :description "Serves CREATE and LIST capabilities for project data."
     :produces [{:media-type standard-outputs}]
     :methods
     {:get {:produces standard-outputs
            :response (fn [ctx] (api/get-all-projects db-spec))}
      :put {:parameters {:body Project}
            :consumes standard-inputs
            :produces standard-outputs
            :response (fn [ctx]
                        (api/insert-project! db-spec (map->Project
                                                       (get-in ctx [:parameters :body]))))}}}))

(defn new-project-target-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/projects-target
     :description "Serves READ, UPDATE, and DELETE capabilities for project data."
     :parameters {:path {:project-id Long}}
     :produces [{:media-type standard-outputs}]
     :methods
     {:get {:produces standard-outputs
            :response (fn [ctx]
                        (api/get-project-by-id
                          db-spec
                          (get-in ctx [:parameters :path :project-id])))}
      :put {:parameters {:body Project}
            :consumes standard-inputs
            :produces standard-outputs
            :response (fn [ctx]
                        (api/update-project-by-id!
                          db-spec
                          (get-in ctx [:parameters :path :project-id])
                          (map->Project (get-in ctx [:parameters :body]))))}}}))

(defn project-content-routes
  [db-spec {:keys [port]}]
  (let [content-routes ["/projects"
                        [
                         ["" (new-index-resource db-spec)]
                         ["/" (new-index-resource db-spec)]]]]
    content-routes
    ))

(defn project-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/projects"
                    [
                     ["" (new-project-base-resource db-spec)]
                     ["/" (yada/redirect :playground.resources/projects-base)]
                     [["/" [#"\d+" :project-id]] (new-project-target-resource db-spec)]
                     ]]]
    api-routes
    ))

(defn project-sync-routes
  [db-spec {:keys [port]}]
  (let [sync-routes ["/projects"
                    [
                     ["" (new-project-post-resource db-spec)]
                     ["/" (new-project-post-resource db-spec)]]]]
    [""
     [
      sync-routes
      ]]
    ))
