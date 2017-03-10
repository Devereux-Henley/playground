(ns playground.shared.requirements
  (:require
   #?(:cljs [goog.dom :as gdom])
   [om.dom :as dom]
   [om.next :as om :refer [IQuery IQueryParams defui]]))

(defmulti read-projects om/dispatch)

(defmethod read-projects :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

(defmethod read-projects :projects/all-projects
  [{:keys [state] :as env} key {:keys [start end]}]
  {:value (subvec (:projects/all-projects @state) start end)})

(defmulti mutate-projects om/dispatch)

(defmethod mutate-projects :default
  [_ _ _] {:remote true})

(defui Project
  Object
  (render
    [this]
    (let [{:keys [project/name project/description]} (om/props this)]
      (dom/div nil
        (dom/p nil name)
        (dom/p nil description)))))

(def project-factory (om/factory Project))

(defui ProjectList
  static IQueryParams
  (params
    [this]
    {:start 0 :end 2})
  static IQuery
  (query
    [this]
    '[:organization/organization-name (:projects/all-projects {:start ?start :end ?end})])
  Object
  (render
    [this]
    (let [{:keys [organization/organization-name projects/all-projects]} (om/props this)]
      (dom/div nil
        (dom/h2 nil organization-name)
        (apply dom/ul nil
          (map
            (fn [[enumeration project-data]]
              (dom/li nil (project-factory project-data)))
            all-projects))))))

(def app-state (atom {:organization/organization-name "Fooworks"
                      :projects/all-projects [[1 {:project/name "Foo" :project/description "Bar"}]
                                              [2 {:project/name "Biz" :project/description "Sniz"}]
                                              [3 {:project/name "Wom" :project/description "Bomb"}]]}))

#?(:clj
   (defn make-reconciler
     []
     (om/reconciler
       {:state app-state
        :parser (om/parser {:read read-projects :mutate mutate-projects})})))

#?(:cljs
   (def project-reconciler
     (om/reconciler
       {:state app-state
        :parser (om/parser {:read read-projects :mutate mutate-projects})})))

#?(:cljs
   (defn projects-init
     [reconciler]
     (om/add-root! reconciler ProjectList (gdom/getElement "projects"))))
