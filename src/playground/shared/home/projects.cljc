(ns playground.shared.home.projects
  (:require
   [om.dom :as dom]
   [om.next :as om :refer [defui]]))

(defui ^:once ProjectEntry
  static om/Ident
  (ident [this {:keys [project/project-name]}]
    [:project/by-project-name project-name])
  static om/IQuery
  (query [this]
    '[:project/project-name
      :project/project-id
      :project/project-description
      :organization/organization-id])
  Object
  (render
    [this]
    (let [{:keys [project/project-id
                  project/project-name
                  project/project-description] :as props} (om/props this)
          {:keys [get-route]} (om/shared this)]
      (dom/li #js {:className "project-list-element"}
        (dom/div #js {:className "project-body"}
          (dom/a #js {:className "project-name"
                      :href (str (get-route :route/projects) "/" project-id)
                      :onClick (fn [_] (do
                                        (om/transact! (om/get-reconciler this) `[(do/it!)])
                                        true))})
          (dom/p #js {:className "project-description"}
            project-description))))))

(defonce project-entry-factory (om/factory ProjectEntry {:keyfn :project/project-name}))
