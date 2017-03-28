(ns playground.shared.ui
  (:require
   #?(:cljs [goog.dom :as gdom])
   [om.dom :as dom]
   [om.next :as om :refer [IQuery IQueryParams defui]]
   [playground.shared.util :as util]))

(defmulti read-navigation om/dispatch)

(defmethod read-navigation :default
  [env key _]
  (util/default-parser env key))

(defui ^:once SessionMenu
  static IQuery
  (query
   [this]
   [:organization/organization-name :user/username :user/first-name :user/last-name])
  Object
  (render
   [this]
   (let [{:keys [:organization/organization-name :user/username :user/first-name :user/last-name]} (om/props this)]
     (dom/ul #js {:className "session-menu-list"}
             (dom/a #js {:className "navigation-bar-link"} username)
             (dom/li #js {:className "session-menu-item"} organization-name)
             (dom/li #js {:className "session-menu-item"} (str first-name " " last-name))))))

(def session-menu-factory (om/factory SessionMenu))

(defui ^:once LoginMenu
  Object
  (render
   [this]
   (dom/a #js {:className "navigation-bar-link" :href "/login"} "Login")))

(def login-menu-factory (om/factory LoginMenu))

(defui ^:once NavigationWrapper
  static IQuery
  (query
   [this]
   [:user/session])
  Object
  (render
   [this]
   (let [{:keys [:user/session]} (om/props this)
         {:keys [owner factory props]} (om/get-computed this)]
     (dom/nav #js {:className "navigation-bar"}
              (dom/a #js {:className "navigation-bar-link"} "Home")
              (dom/a #js {:className "navigation-bar-link"} "Cards")
              (dom/a #js {:className "navigation-bar-link"} "Information")
              (if (nil? session) (login-menu-factory) (session-menu-factory session))
              (factory props)))))

(def navigation-bar-factory (om/factory NavigationWrapper))

#?(:clj
   (defn make-reconciler
     [server-send]
     (om/reconciler
      {:state (atom {})
       :normalize true
       :parser (om/parser {:read read-navigation})
       :send server-send})))

#?(:cljs
   (defonce navigation-reconciler
     (om/reconciler
      {:state (atom {})
       :normalize true
       :parser (om/parser {:read read-navigation})
       :send (util/transit-post "/api/navigation")})))

#?(:cljs
   (defn navigation-init
     [reconciler]
     (om/add-root! reconciler NavigationWrapper (gdom/getElement "navigation"))))
