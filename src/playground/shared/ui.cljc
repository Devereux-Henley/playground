(ns playground.shared.ui
  (:require
   [clojure.string :as string]
   [om.dom :as dom]
   [om.next :as om :refer [defui]]
   [playground.shared.home.organizations :refer [organization-entry-factory OrganizationEntry]]))

(defui ^:once NavigationWrapper
  static om/IQuery
  (query
    [this]
    (let [organization-query (om/get-query OrganizationEntry)]
      `[{:current/user [:user/name]}
        {:organizations/organizations-by-id ~organization-query}]))
  Object
  (render
    [this]
    (let [{:keys [current/user
                  organizations/organizations-by-id]} (om/props this)
          {:keys [get-route]}    (om/shared this)
          {:keys [owner factory props] :as computed} (om/get-computed this)]
      (dom/div #js {:className "app-container"}
        (dom/nav #js {:className "navigation-bar"}
          (dom/div #js {:className "navigation-link-container"}
            (dom/a #js {:className "navigation-bar-link" :href (get-route :route/index)} "Home")
            (dom/a #js {:className "navigation-bar-link" :href (get-route :route/cards)} "Cards")
            (dom/a #js {:className "navigation-bar-link" :href (get-route :route/information)} "Information")
            (if user
              (apply
                dom/ul #js {:className "session-menu-list"}
                (cons
                  (dom/a #js {:className "navigation-bar-link"} (string/capitalize (:user/name user)))
                  (map organization-entry-factory (vals organizations-by-id))))
              (dom/a #js {:className "navigation-bar-link" :href (get-route :route/login)} "Login"))))
        (factory props)))))

(defonce navigation-bar-factory (om/factory NavigationWrapper))
