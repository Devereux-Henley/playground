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
      :organization/organization-id])
  Object
  (render
    [{:keys [organization/organization-name]}]
    (dom/div nil (str "An organization by the name of " organization-name))))

(defui ^:once OrganizationList
  static om/IQuery
  (query
    [this]
    (let [subquery (om/get-query OrganizationEntry)]
      '[{:organization/organization-list ~subquery}]))
  Object
  (render
    [this]
    (let [{:keys [organization/organization-list]} (om/props this)]
      (apply
        dom/div nil
        (for [{:keys [organization-name]} organization-list]
          (dom/div nil organization-name))))))

(defonce organization-list-factory (om/factory OrganizationList))
