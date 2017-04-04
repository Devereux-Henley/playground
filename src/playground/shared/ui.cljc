(ns playground.shared.ui
  (:require
   [compassus.core :as compassus]
   [om.dom :as dom]
   [om.next :as om :refer [IQuery IQueryParams defui]]))

(defn change-route
  [component route event]
  (.preventDefault event)
  (compassus/set-route! component route))

(defui ^:once SessionMenu
  static IQuery
  (query
    [this]
    [:organization/organization-name :user/username :user/first-name :user/last-name])
  Object
  (render
    [this]
    (let [{:keys [organization/organization-name user/username user/first-name user/last-name]} (om/props this)]
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
    (let [{:keys [user/session]} (om/props this)
          {:keys [owner factory props] :as computed} (om/get-computed this)]
      (dom/div #js {:className "app-container"}
        (dom/nav #js {:className "navigation-bar"}
          (dom/a #js {:className "navigation-bar-link" :onClick #(change-route owner :route/index %)} "Home")
          (dom/a #js {:className "navigation-bar-link" :onClick #(change-route owner :route/cards %)} "Cards")
          (dom/a #js {:className "navigation-bar-link" :onClick #(change-route owner :route/information %)} "Information")
          (if session (session-menu-factory session) (login-menu-factory)))
        (dom/div nil (factory props))))))

(def navigation-bar-factory (om/factory NavigationWrapper))
