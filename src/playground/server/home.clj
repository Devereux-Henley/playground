(ns playground.server.home
  (:require
   [compassus.core :as compassus]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.dom :as dom]
   [om.next :as om]
   [playground.shared.home :as home]
   [playground.shared.util :refer [create-om-string server-send]]
   [yada.yada :as yada]))

(defmulti read-home-data om/dispatch)

(defmethod read-home-data :default
  [_ k _]
  {:value {:error (str "No handler for key" k)}})

(defmethod read-home-data :user/session
  [{:keys [state query target] :as env} _ _]
  {:value {:organization/organization-name "Server Sent Inc."
           :user/username "Devo"
           :user/first-name "Devereux"
           :user/last-name "Henley"}})

(defmethod read-home-data :route/index
  [{:keys [state query target] :as env} _ _]
  {:value (select-keys @state query)})

(defmulti mutate-home-data om/dispatch)

(defmethod mutate-home-data :default
  [_ _ _]
  {:value {:error "Cannot mutate this data."}})

(def home-parser
  (om/parser {:read read-home-data
              :mutate mutate-home-data}))

(defn home-page
  [send-func]
  (let [app (home/make-app send-func)
        mounted-app (compassus/mount! app nil)]
    (html
      [:head
       [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
       [:meta {:charset "utf-8"}]
       [:meta {:http-equiv "X-UA-Compatible"}]
       (include-css "/home.css")
       [:title "Home"]]
      [:body
       [:section#app (dom/render-to-str mounted-app)]
       (include-js "/home.js")])))

(defn new-home-index-resource
  [db-spec]
  (let [configured-parser (partial home-parser {:db-spec db-spec})]
    (yada/resource
      {:id :playground.resources/index
       :description "Serves home SPA."
       :produces [{:media-type
                   #{"text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.9"}
                   :charset "UTF-8"}]
       :methods
       {:get {:response (fn [ctx]
                          (case (yada/content-type ctx)
                            "text/html" (home-page
                                          (server-send configured-parser))))}}})))

(defn home-post-resource
  [db-spec]
  (let [configured-parser (partial home-parser {:db-spec db-spec
                                          :state (atom {})})]
    (yada/resource
      {:id :playground.resources/home-sync-post
       :description "Post route for syncing remote with home state."
       :produces [{:media-type #{"application/json;q=0.9" "application/edn;q=0.9" "application/transit+json;q=0.9"}
                   :charset "UTF-8"}]
       :methods
       {:post {:consumes #{"application/transit+json;q=0.9"}
               :produces #{"application/transit+json;q=0.9"}
               :response (fn [ctx]
                           (configured-parser (:body ctx)))}}})))

(defn home-content-routes
  [db-spec {:keys [port]}]
  (let [content-routes ["/home"
                        [
                         ["" (new-home-index-resource db-spec)]
                         ["/" (new-home-index-resource db-spec)]
                         ]]]
    [""
     [
      content-routes
      ]]))

(defn home-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/home"
                    [
                     ["" (home-post-resource db-spec)]
                     ["/" (home-post-resource db-spec)]
                     ]]]
    [""
     [
      api-routes
      ]]))
