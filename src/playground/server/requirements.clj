(ns playground.server.requirements
  (:require
   [bidi.bidi :as bidi]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.next :as om]
   [om.dom :as dom]
   [playground.server.db.requirements :as db]
   [playground.shared.requirements :as r]
   [schema.core :as s]
   [yada.swagger :as swagger]
   [yada.yada :as yada]))

(defn project-page
  []  
  (let [reconciler (r/make-reconciler)
        root (om/add-root! reconciler r/Counter nil)
        html-string (dom/render-to-str root)]
    (html
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:http-equiv "X-UA-Compatible"}]
       [:title "Projects List"]]
      [:body
       [:section#projects html-string]       
       (include-js "playground.js")])))

(defn new-index-resource
  [db-spec]
  (yada/resource
    {:id :edge.resources/requirements-index
     :description "Requirements entries"
     :produces [{:media-type
                 #{"text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.7"}
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
                            "text/html" (project-page) 
                            response)))}}}))

(defn requirements-routes
  [db-spec {:keys [port]}]  
  (let [routes ["/requirements"
                [
                 ["" (new-index-resource db-spec)]]]]    
    [""
     [
      routes
      ["/requirements-api/swagger.json"
       (bidi/tag
         (yada/handler
           (swagger/swagger-spec-resource
             (swagger/swagger-spec
               routes
               {:info {:title "Requirements"
                       :version "1.0"
                       :description "A simple application for displaying requirements"}
                :host (format "localhost:%d" port)
                :schemes ["http"]
                :tags [{:name "getters"
                        :description "All paths that support GET"}]
                :basePath ""})))
         :edge.resources/phonebook-swagger)]]]))
