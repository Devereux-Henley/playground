(ns playground.shared.home.organizations
  (:require
   [om.dom :as dom]
   [om.next :as om :refer [defui]]
   [playground.shared.home.projects :refer [project-entry-factory
                                            ProjectEntry]]))

(defui ^:once OrganizationEntry
  static om/Ident
  (ident [_ {:keys [organizations/organization-id]}]
    [:organizations/organization-by-id organization-id])
  static om/IQuery
  (query [this]
    '[:organizations/organization-name
      :organizations/organization-id
      :organizations/organization-description])
  Object
  (render
    [this]
    (let [{:keys [organizations/organization-name
                  organizations/organization-description
                  organizations/organization-id] :as props} (om/props this)
          {:keys [get-route]} (om/shared this)]
      (dom/li #js {:className "session-menu-item"}
        (dom/a #js {:className "nav-text"
                    :href (str (get-route :route/organizations) "/" organization-id)
                    :onClick (fn [_] (do
                                      (om/transact!
                                        (om/get-reconciler this)
                                        `[(organization/set-organization ~props)])
                                      true))}
          organization-name)))))

(defonce organization-entry-factory (om/factory OrganizationEntry {:keyfn :organizations/organization-id}))

(defui ^:once OrganizationPage
  static om/IQuery
  (query
    [this]
    [:organizations/current-organization])
  Object
  (render
    [this]
    (let [{:keys [organizations/current-organization] :as props} (om/props this)
          {:keys [organizations/organization-name
                  organizations/organization-description
                  organizations/organization-id]} (first current-organization)]
      (dom/div #js {:className "organization-page"}
        (dom/p #js {:className "organization-name"}
          organization-name)
        (dom/p #js {:className "organization-description"}
          organization-description)
        (dom/h2 #js {:className "project-header"}
          "Projects in this organization: ")))))
