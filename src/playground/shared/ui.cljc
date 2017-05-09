(ns playground.shared.ui
  (:require
   [clojure.string :as string]
   [om.dom :as dom]
   [om.next :as om :refer [defui]]
   [playground.shared.home.organizations :refer [organization-list-factory OrganizationList]]))

(defui ^:once SessionMenu
  static om/IQuery
  (query
    [this]
    [:user/username (om/get-query OrganizationList)])
  Object
  (render
    [this]
    (let [{:keys [user/username organization/organization-list]} (om/props this)]
      (dom/ul #js {:className "session-menu-list"}
        (dom/a #js {:className "navigation-bar-link"} (string/capitalize username))
        (organization-list-factory organization-list)))))

(defonce session-menu-factory (om/factory SessionMenu))

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
            (if session
              (session-menu-factory session)
              (dom/a #js {:className "navigation-bar-link" :href (get-route :route/login)} "Login"))))
        (factory props)))))

(defonce navigation-bar-factory (om/factory NavigationWrapper))
