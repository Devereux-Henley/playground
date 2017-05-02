(ns playground.server.projects
  (:require
   [bidi.bidi :as bidi]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.next :as om]
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.projects :as api :refer [map->Project]]
   [playground.server.requirements :refer [new-project-requirement-base-resource]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.server.util :refer [merge-base-defaults merge-target-defaults]]
   [playground.shared.projects :as r]
   [playground.shared.util :refer [create-om-string server-send]]
   [schema.core :as schema :refer [defschema]]
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
  [{:keys [project-resource]} _ _]
  {:value (sanitize-project-response (list-record project-resource))})

(def project-parser
  (om/parser {:read read-projects}))

;; Schemas

(defschema Project
  {:name String
   :description String
   :organization-id Integer})

(defschema PartialProject
  {(schema/optional-key :name) String
   (schema/optional-key :description) String
   (schema/optional-key :organization-id) Integer})

;; Yada Resources

(defn new-project-post-resource
  [project-resource]
  (let [configured-parser (partial project-parser {:project-resource project-resource})]
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
  [project-resource]
  (yada/resource
    (merge-base-defaults
      "projects"
      {:methods
       {:get {:produces standard-outputs
              :swagger/tags ["projects" "list"]
              :response (fn [ctx] (list-record project-resource))}
        :put {:parameters {:body Project}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["projects" "create"]
              :response (fn [ctx]
                          (create-record project-resource (map->Project
                                                            (get-in ctx [:parameters :body]))))}}})))

(defn new-project-target-resource
  [project-resource]
  (yada/resource
    (merge-target-defaults
      "projects"
      {:parameters {:path {:project-id Long}}
       :methods
       {:get {:produces standard-outputs
              :swagger/tags ["projects" "read"]
              :response (fn [ctx]
                          (read-record
                            project-resource
                            (get-in ctx [:parameters :path :project-id])))}
        :put {:parameters {:body Project}
              :consumes standard-inputs
              :produces standard-outputs
              :swagger/tags ["projects" "update"]
              :response (fn [ctx]
                          (update-record
                            project-resource
                            (get-in ctx [:parameters :path :project-id])
                            (map->Project (get-in ctx [:parameters :body]))))}}})))

(defn project-api-routes
  [project-resource requirement-resource {:keys [port]}]
  (let [api-routes ["/projects"
                    [
                     ["" (new-project-base-resource project-resource)]
                     ["/" (yada/redirect :playground.resources/projects-base)]
                     [["/" [#"\d+" :project-id]] (new-project-target-resource project-resource)]
                     [["/" [#"\d+" :project-id] "/requirements"] (new-project-requirement-base-resource
                                                                   requirement-resource)]
                     ]]]
    api-routes
    ))

(defn project-sync-routes
  [project-resource {:keys [port]}]
  (let [sync-routes ["/projects"
                     [
                      ["" (new-project-post-resource project-resource)]
                      ["/" (new-project-post-resource project-resource)]]]]
    [""
     [
      sync-routes
      ]]
    ))
