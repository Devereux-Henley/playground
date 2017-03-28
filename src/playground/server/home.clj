(ns playground.server.home
  (:require
   [compassus.core :as compassus]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.dom :as dom]
   [om.next :as om]
   [playground.shared.home :as home]
   [playground.server.ui :as backend-ui]
   [playground.shared.ui :as frontend-ui]
   [playground.shared.util :refer [create-om-string server-send]]
   [yada.yada :as yada]))

(defmulti read-navigation-data om/dispatch)

(defmethod read-navigation-data :default
  [_ k _]
  {:value {:error (str "No handler for key" k)}})

(defmethod read-navigation-data :user/session
  [_ _ _]
  {:value {:organization/organization-name "Server Sent Inc."
           :user/username "Devo"
           :user/first-name "Devereux"
           :user/last-name "Henley"}})

(defmethod read-navigation-data :route/index
  [env _ params]
  {:value "Hey"})

(defn home-page
  [send-func]
  (let [app (home/make-app send-func)
        home-string (dom/render-to-str (compassus/mount! app nil))]
    (html
      [:head
       [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
       [:meta {:charset "utf-8"}]
       [:meta {:http-equiv "X-UA-Compatible"}]
       (include-css "/home.css")
       [:title "Home"]]
      [:body
       [:section#app home-string]
       (include-js "/home.js")])))

(defn new-home-index-resource
  [db-spec]
  (let [navigation-parser (partial backend-ui/navigation-parser {:db-spec db-spec})]
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
                                          (server-send navigation-parser))))}}})))

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
