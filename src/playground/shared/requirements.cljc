(ns playground.shared.requirements
  (:require
   #?(:cljs [goog.dom :as gdom])
   [om.dom :as dom]
   [om.next :as om :refer [IQuery IQueryParams defui]]
   [playground.shared.util :as util]))

(defmulti read-projects om/dispatch)

(defmethod read-projects :default
  [{:keys [state] :as env} key _]
  (let [st @state]    
    (if-let [[_ value] (find st key)]
      {:value value :remote (:ast env)}
      {:remote true})))

(defmethod read-projects :projects/all-projects
  [{:keys [state] :as env} key _]  
  (let [st @state]    
    (if-let [[_ value] (find st key)]
      {:value value :remote (:ast env)}
      {:remote true})))

(defmulti mutate-projects om/dispatch)

(defmethod mutate-projects :default
  [_ _ _] {:remote true})

(defui Project
  static IQuery
  (query
    [this]
    [:project/name :project/description])
  Object
  (render
    [this]
    (let [{:keys [project/name project/description]} (om/props this)]
      (dom/div nil
        (dom/p nil name)
        (dom/p nil description)))))

(def project-factory (om/factory Project))

(defui ProjectList
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
        (apply dom/ul nil
          (map
            (fn [[_ project-data]]
              (dom/li nil (project-factory project-data)))
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
   (def project-reconciler
     (om/reconciler
       {:state (atom {})
        :normalize true
        :parser (om/parser {:read read-projects :mutate mutate-projects})
        :send (util/transit-post "/projects")})))

#?(:cljs
   (defn projects-init
     [reconciler]
     (om/add-root! reconciler ProjectList (gdom/getElement "projects"))))
