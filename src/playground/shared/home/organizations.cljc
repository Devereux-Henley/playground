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
                  organization/organization-description
                  organization/organization-id] :as props} (om/props this)
          {:keys [get-route]} (om/shared this)]
      (dom/li #js {:className "session-menu-item"
                   :href (str (get-route :route/organizations) "/" organization-id)
                   :onClick (fn [_] (om/transact! this `[(organization/set-organization ~props)]))}
        organization-name))))

(defonce organization-entry-factory (om/factory OrganizationEntry {:keyfn :organization/organization-name}))

(defui ^:once OrganizationPage
  static om/IQuery
  (query
    [this]
    [:organization/current-organization])
  Object
  (render
    [this]
    (let [{:keys [organization/current-organization]} (om/props this)
          {:keys [organization/organization-name]} current-organization]
      (dom/div nil organization-name))))
