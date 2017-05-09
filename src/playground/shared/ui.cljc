(ns playground.shared.ui
  (:require
   [om.dom :as dom]
   [om.next :as om :refer [defui]]))

(defui ^:once SessionMenu
  static om/IQuery
  (query
    [this]
    [:organization/organization-name :user/username])
  Object
  (render
    [this]
    (let [{:keys [organization/organization-name user/username]} (om/props this)]
      (dom/ul #js {:className "session-menu-list"}
        (dom/a #js {:className "navigation-bar-link"} username)
        (dom/li #js {:className "session-menu-item"} organization-name)))))

(defonce session-menu-factory (om/factory SessionMenu))

(defui ^:once LoginMenu
  Object
  (render
    [this]
    (dom/a #js {:className "navigation-bar-link" :href "/login"} "Login")))

(defonce login-menu-factory (om/factory LoginMenu))

(defui ^:once NavigationWrapper
  static om/IQueryParams
  (params
    [this]
    {:user/session (om/get-query SessionMenu)})
  static om/IQuery
  (query
    [this]
    '[{:user/session ?user/session}])
  Object
  (render
    [this]
    (let [{:keys [user/session]} (om/props this)
          {:keys [get-route]}    (om/shared this)
          {:keys [owner factory props] :as computed} (om/get-computed this)]
      (dom/div #js {:className "app-container"}
        (dom/nav #js {:className "navigation-bar"}
          (dom/div #js {:className "navigation-link-container"}
            (dom/a #js {:className "navigation-bar-link" :href (get-route :route/index)} "Home")
            (dom/a #js {:className "navigation-bar-link" :href (get-route :route/cards)} "Cards")
            (dom/a #js {:className "navigation-bar-link" :href (get-route :route/information)} "Information")
            (if session (session-menu-factory session) (login-menu-factory))))
        (factory props)))))

(defonce navigation-bar-factory (om/factory NavigationWrapper))
