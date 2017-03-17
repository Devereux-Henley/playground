(ns playground.shared.projects
  (:require
   #?(:cljs [goog.dom :as gdom])
   [om.dom :as dom]
   [om.next :as om :refer [IQuery IQueryParams defui]]
   [playground.shared.util :as util]))

(defmulti read-projects om/dispatch)

(defmethod read-projects :default
  [env key _]
  (util/default-parser env key))

(defmethod read-projects :projects/all-projects
  [{:keys [state] :as env} key _]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value :remote (:ast env)}
      {:remote true})))

(defmulti mutate-projects om/dispatch)

(defmethod mutate-projects :default
  [_ _ _] {:remote true})

(defui ^:once Project
  static IQuery
  (query
    [this]
    [:project/name :project/description])
  Object
  (render
    [this]
    (let [{:keys [project/name project/description]} (om/props this)]
      (dom/div #js {:className "project"}
        (dom/p #js {:className "project-name"} name)
        (dom/p #js {:className "project-description"} description)))))

(def project-factory (om/factory Project))

(defui ^:once ProjectList
  static IQuery
  (query
    [this]
    [:organization/organization-name :projects/all-projects])
  Object
  (render
    [this]
    (let [{:keys [organization/organization-name projects/all-projects]} (om/props this)]
      (dom/div nil
        (dom/h2 nil organization-name)
        (apply dom/ul #js {:className "project-list"}
          (map
            (fn [[_ project-data]]
              (dom/li #js {:className "project-list-element"} (project-factory project-data)))
            all-projects))))))

(def project-list-factory (om/factory ProjectList))

#?(:clj
   (defn make-reconciler
     [server-send]
     (om/reconciler
       {:state (atom {})
        :normalize true
        :parser (om/parser {:read read-projects :mutate mutate-projects})
        :send server-send})))

#?(:cljs
   (defonce project-reconciler
     (om/reconciler
       {:state (atom {})
        :normalize true
        :parser (om/parser {:read read-projects :mutate mutate-projects})
        :send (util/transit-post "/api/projects")})))

#?(:cljs
   (defn projects-init
     [reconciler]
     (om/add-root! reconciler ProjectList (gdom/getElement "projects"))))
