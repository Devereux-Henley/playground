(ns playground.server.home
  (:require
   [bidi.bidi :as bidi]
   [compassus.core :as compassus]
   [hiccup.core :refer [html]]
   [hiccup.page :refer [include-js include-css]]
   [om.dom :as dom]
   [om.next :as om]
   [playground.server.parsers.home :refer [home-parser]]
   [playground.shared.home :as home]
   [playground.shared.util :refer [create-om-string server-send]]
   [yada.yada :as yada]
   [playground.shared.util :as util]
   [playground.shared.ui :as ui]
   [schema.core :as schema]))

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
  [resources jwt-secret sub-route]
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
                                        (server-send (partial
                                                       home-parser
                                                       {:user (get-in ctx [:authentication "default" :user])
                                                        :resources resources}))
                                        sub-route)))}}}))

(defn home-post-resource
  [resources]
  (yada/resource
    {:id :playground.resources/home-sync-post
     :description "Post route for syncing remote with home state."
     :produces [{:media-type #{"application/json;q=0.9" "application/edn;q=0.9" "application/transit+json;q=0.9"}
                 :charset "UTF-8"}]
     :access-control
     {:scheme :basic-auth
      :authorization {:methods {:post :user}}}
     :methods
     {:post {:consumes #{"application/transit+json;q=0.9"}
             :produces #{"application/transit+json;q=0.9"}
             :response (fn [{:keys [authentication body]}]
                         (println body)
                         (home-parser
                           {:user (get-in authentication ["default" :user])
                            :resources resources}
                           body))}}}))

(defn home-content-routes
  [resources jwt-secret {:keys [port]}]
  (let [configured-resource (partial new-home-resource resources jwt-secret)
        content-routes ["/"
                        [["home" (configured-resource :route/index)]
                         ["home/" (yada/redirect :playground.resources/index)]
                         ["information" (configured-resource :route/information)]
                         ["information/" (yada/redirect :playground.resources/information)]
                         ["cards" (configured-resource :route/cards)]
                         ["cards/" (yada/redirect :playground.resources/cards)]
                         ["login" (configured-resource :route/login)]
                         ["login/" (yada/redirect :playground.resources/login)]]]]
    [""
     [
      content-routes
      ]]))

(defn home-api-routes
  [resources {:keys [port]}]
  (let [api-routes ["/home"
                    [
                     ["" (home-post-resource resources)]
                     ["/" (home-post-resource resources)]
                     ]]]
    api-routes
    ))
