(ns playground.shared.home.organizations
  (:require
   [om.dom :as dom]
   [om.next :as om :refer [defui]]))

(defui ^:once OrganizationEntry
  static om/Ident
  (ident [this {:keys [organization/organization-name]}]
    [:organization/by-organization-name organization-name])
  static om/IQuery
  (query [this]
    '[:organization/organization-name
      :organization/organization-id
      :organization/organization-description])
  Object
  (render
    [this]
    (let [{:keys [organization/organization-name
                  organization/organization-description]} (om/props this)]
      (dom/li #js {:className "session-menu-item"} organization-name))))

(defonce organization-entry-factory (om/factory OrganizationEntry {:keyfn :organization/organization-name}))
