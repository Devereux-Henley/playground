(ns playground.server.home
  (:require
   [bidi.bidi :as bidi]
   [compassus.core :as compassus]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.dom :as dom]
   [om.next :as om]
   [playground.shared.home :as home]
   [playground.shared.util :refer [create-om-string server-send]]
   [yada.yada :as yada]
   [playground.shared.util :as util]
   [playground.shared.ui :as ui]))

(defmulti read-home-data om/dispatch)

(defmethod read-home-data :default
  [_ k _]
  {:value {:error (str "No handler for key" k)}})

(defmethod read-home-data :page/title
  [_ _ _]
  {:value "Baz"})

(defmethod read-home-data :user/session
  [{:keys [state query target] :as env} _ _]
  {:value {:organization/organization-name "Server Sent Inc."
           :user/username "Devo"
           :user/first-name "Devereux"
           :user/last-name "Henley"}})

(defmulti mutate-home-data om/dispatch)

(defmethod mutate-home-data :default
  [_ _ _]
  {:value {:error "Cannot mutate this data."}})

(defonce home-parser
  (compassus/parser {:read read-home-data
              :mutate mutate-home-data}))

(defn home-page
  [send-func current-route]
  (let [app (home/make-app send-func)
        mounted-app (do (compassus/set-route! app current-route)
                        (compassus/mount! app :target))]
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

(defn new-home-resource
  [db-spec sub-route]
  (let [configured-parser (partial home-parser {:db-spec db-spec :state (atom {})})]
    (yada/resource
      {:id (util/build-id "playground.resources" sub-route)
       :description "Serves home SPA."
       :produces [{:media-type
                   #{"text/html" "application/edn;q=0.9" "application/json;q=0.8" "application/transit+json;q=0.9"}
                   :charset "UTF-8"}]
       :methods
       {:get {:response (fn [ctx]
                          (case (yada/content-type ctx)
                            "text/html" (home-page
                                          (server-send configured-parser)
                                          sub-route)))}}})))

(defn home-post-resource
  [db-spec]
  (let [configured-parser (partial home-parser {:db-spec db-spec :state (atom {})})]
    (yada/resource
      {:id :playground.resources/home-sync-post
       :description "Post route for syncing remote with home state."
       :produces [{:media-type #{"application/json;q=0.9" "application/edn;q=0.9" "application/transit+json;q=0.9"}
                   :charset "UTF-8"}]
       :methods
       {:post {:consumes #{"application/transit+json;q=0.9"}
               :produces #{"application/transit+json;q=0.9"}
               :response (fn [{:keys [body]}]
                           (configured-parser body))}}})))

(defn home-content-routes
  [db-spec {:keys [port]}]
  (let [content-routes ["/"
                        [["home" (new-home-resource db-spec :route/index)]
                         ["home/" (yada/redirect :playground.resources/index)]
                         ["information" (new-home-resource db-spec :route/information)]
                         ["information/" (yada/redirect :playground.resources/information)]
                         ["cards" (new-home-resource db-spec :route/cards)]
                         ["cards/" (yada/redirect :playground.resources/cards)]]]]
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
