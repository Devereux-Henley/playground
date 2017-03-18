(ns playground.server.ui
  (:require [bidi.bidi :as bidi]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [om.next :as om]
            [playground.server.db.requirements :as db]
            [playground.shared.ui :as ui]
            [playground.shared.util :refer [create-om-string server-send]]
            [schema.core :as s]
            [yada.swagger :as swagger]
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

(def navigation-parser
  (om/parser {:read read-navigation-data}))

(defn navigation-post-resource
  [db-spec]
  (let [configured-parser (partial navigation-parser {:db-spec db-spec})]
    (yada/resource
     {:id :playground.resources/navigation-sync-post
      :description "Post route for syncing remote with navigation state."
      :produces [{:media-type #{"application/json;q=0.9" "application/edn;q=0.9" "application/transit+json;q=0.9"}
                  :charset "UTF-8"}]
      :methods
      {:post {:consumes #{"application/transit+json;q=0.9"}
              :produces #{"application/transit+json;q=0.9"}
              :response (fn [ctx]
                          (configured-parser (:body ctx)))}}})))

(defn navigation-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/navigation"
                    [
                     ["" (navigation-post-resource db-spec)]
                     ["/" (navigation-post-resource db-spec)]
                     ]]]
    [""
     [
      api-routes
      ]]))
