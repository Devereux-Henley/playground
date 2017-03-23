(ns playground.server.home
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [om.next :as om]
            [playground.server.ui :as backend-ui]
            [playground.shared.ui :as frontend-ui]
            [playground.shared.util :refer [create-om-string server-send]]
            [yada.yada :as yada]))

(defn home-page
  [send-func]
  (let [nav-reconciler (frontend-ui/make-reconciler send-func)
        nav-string (create-om-string nav-reconciler frontend-ui/NavigationBar)]
    (html
     [:head
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "X-UA-Compatible"}]
      [:title "Home"]]
     [:body
      [:section#nav nav-string]
      [:section#home
       [:div
        [:p "Welcome to the home page!"]]]
      (include-js "/home.js")
      (include-css "/home.css")])))

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
