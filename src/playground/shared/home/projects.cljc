(ns playground.shared.home.projects
  (:require
   [om.dom :as dom]
   [om.next :as om :refer [defui]]
   [playground.shared.home.requirements :refer [requirement-entry-factory]]))

(defui ^:once ProjectEntry
  static om/Ident
  (ident [this {:keys [projects/project-id]}]
    [:projects/projects-by-id project-id])
  static om/IQuery
  (query [this]
    '[:projects/project-name
      :projects/project-id
      :projects/project-description
      [:organizations/organizations-by-id _]])
  Object
  (render
    [this]
    (let [{:keys [projects/project-id
                  projects/project-name
                  projects/project-description] :as props} (om/props this)
          {:keys [get-route]} (om/shared this)]
      (dom/li #js {:className "project-list-element"}
        (dom/div #js {:className "project-body"}
          (dom/a #js {:className "project-name"
                      :href (str (get-route :route/projects) "/" project-id)
                      :onClick (fn [_] (do
                                        (om/transact! (om/get-reconciler this) `[(projects/set-project ~props)
                                                                                 :projects/current-project
                                                                                 :requirements/requirements-list])
                                        true))}
            project-name)
          (dom/p #js {:className "project-description"}
            project-description))))))

(defonce project-entry-factory (om/factory ProjectEntry {:keyfn :projects/project-id}))

(defui ^:once ProjectPage
  static om/IQueryParams
  (params
    [this]
    {:start 0 :end 10})
  static om/IQuery
  (query
    [this]
    '[:projects/current-project
      (:requirements/requirements-list {:start ?start :end ?end})])
  Object
  (render
    [this]
    (let [{:keys [projects/current-project
                  requirements/requirements-list] :as props} (om/props this)
          {:keys [projects/project-name
                  projects/project-id
                  projects/project-description]} (first current-project)]
      (dom/div #js {:className "project-page"}
        (dom/h2 #js {:className "project-page-title"}
          project-name)
        (apply
          dom/ul #js {:className "requirements-list"}
          (map requirement-entry-factory (vals requirements-list)))))))
