(ns playground.server.projects
  (:require
   [bidi.bidi :as bidi]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.next :as om]
   [om.dom :as dom]
   [playground.server.db.requirements :as db]
   [playground.shared.projects :as r]
   [schema.core :as s]
   [yada.swagger :as swagger]
   [yada.yada :as yada]))

(declare server-send)

(defn sanitize-project-response
  [response]  
  (if (empty? response)
    response
    (mapv
      (fn [{:keys [name description]} cnt]
        [cnt {:project/name name :project/description description}])
      response
      (iterate inc 0))))

(defn project-page
  [send-func]  
  (let [reconciler (r/make-reconciler send-func)
        root (om/add-root! reconciler r/ProjectList nil)
        html-string (dom/render-to-str root)]    
    (html
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:http-equiv "X-UA-Compatible"}]
       [:title "Projects List"]]
      [:body
       [:section#projects html-string]       
       (include-js "/playground.js")])))

;; Om.Next parsing.

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

(defn server-send
  [db-spec]
  (fn [{:keys [remote]} callback]
    (let [response (project-parser {:db-spec db-spec} remote)]      
      (callback response remote))))

;; Yada Resources

(defn new-index-resource
  [db-spec]
  (yada/resource
    {:id :edge.resources/projects-index
     :description "Requirements entries"
     :produces [{:media-type
                 #{"text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.9"}
                 :charset "UTF-8"}]
     :methods
     {:get {:parameters {:query {(s/optional-key :id) String}}
            :swagger/tags ["default" "getters"]
            :response (fn [ctx]
                        (let [id (get-in ctx [:parameters :query :id])
                              response (if (or (nil? id) (empty? id))                                
                                         (db/get-projects db-spec)
                                         (db/get-project-by-id db-spec {:id id}))]
                          (case (yada/content-type ctx)
                            "text/html" (project-page (server-send db-spec)))))}
      :post {:consumes #{"application/transit+json;q=0.9"}
             :produces #{"application/transit+json;q=0.9"}
             :response (fn [ctx]                                                  
                         (project-parser {:db-spec db-spec} (:body ctx)))}}}))

(defn projects-routes
  [db-spec {:keys [port]}]  
  (let [routes ["/projects"
                [
                 ["" (new-index-resource db-spec)]
                 ["/" (new-index-resource db-spec)]]]]    
    [""
     [
      routes
      ["/projects-api/swagger.json"
       (bidi/tag
         (yada/handler
           (swagger/swagger-spec-resource
             (swagger/swagger-spec
               routes
               {:info {:title "Projects"
                       :version "1.0"
                       :description "A simple application for displaying requirements"}
                :host (format "localhost:%d" port)
                :schemes ["http"]
                :tags [{:name "getters"
                        :description "All paths that support GET"}]
                :basePath ""})))
         :edge.resources/projects-swagger)]]]))
