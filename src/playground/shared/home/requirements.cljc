(ns playground.shared.home.requirements
  (:require
   [om.dom :as dom]
   [om.next :as om :refer [defui]]))

(defui ^:once RequirementEntry
  static om/Ident
  (ident [this {:keys [requirements/requirement-id]}]
    [:requirements/requirements-by-id requirement-id])
  static om/IQuery
  (query [this]
    '[:requirements/requirement-name
      :requirements/requirement-id
      :requirements/requirement-description
      [:projects/projects-by-id _]])
  Object
  (render
    [this]
    (let [{:keys [requirements/requirement-id
                  requirements/requirement-name
                  requirements/requirement-description] :as props} (om/props this)]
      (dom/li #js {:className "requirement-list-element"}
        (dom/div #js {:className "requirement-body"}
          (dom/a #js {:className "requirement-name"}
            requirement-name)
          (dom/p #js {:className "requirement-description"}
            requirement-description))))))

(defonce requirement-entry-factory (om/factory RequirementEntry {:keyfn :requirements/requirement-id}))
