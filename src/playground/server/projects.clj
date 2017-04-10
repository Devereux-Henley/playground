(ns playground.server.projects
  (:require
   [bidi.bidi :as bidi]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.next :as om]
   [playground.server.db.projects :as db]
   [playground.shared.projects :as r]
   [playground.shared.util :refer [create-om-string server-send]]
   [schema.core :as s]
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
  {:value (sanitize-project-response (db/get-projects db-spec))})

(def project-parser
  (om/parser {:read read-projects}))

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
      {:get {:parameters {:query {(s/optional-key :id) String}}
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

(defn project-content-routes
  [db-spec {:keys [port]}]
  (let [content-routes ["/projects"
                        [
                         ["" (new-index-resource db-spec)]
                         ["/" (new-index-resource db-spec)]]]]
    [""
     [
      content-routes
      ["/projects-api/swagger.json"
       (bidi/tag
        (yada/handler
         (swagger/swagger-spec-resource
          (swagger/swagger-spec
           content-routes
           {:info {:title "Projects"
                   :version "1.0"
                   :description "A simple application for displaying requirements"}
            :host (format "localhost:%d" port)
            :schemes ["http"]
            :tags [{:name "getters"
                    :description "All paths that support GET"}]
            :basePath ""})))
        :playground.resources/projects-swagger)]]]))

(defn project-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/projects"
                    [
                     ["" (new-project-post-resource db-spec)]
                     ["/" (new-project-post-resource db-spec)]]]]
    [""
     [
      api-routes
      ]]
    ))
