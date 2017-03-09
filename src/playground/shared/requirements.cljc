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

(defmethod read-projects :projects/list
  [{:keys [state] :as env} key {:keys [start end]}]
  {:value (subvec (:projects/all-projects @state) start end)})

(defmulti mutate-projects om/dispatch)

(defmethod mutate-projects :default
  [_ _ _] {:remote true})

(defui Project
  Object
  (render
    [this]
    (let [{:keys [name description]} (om/props this)]
      (dom/div nil
        (dom/p nil name)
        (dom/p nil description)))))

(def project-factory (om/factory Project))

(defui ProjectList  
  Object
  (render
    [this]
    (let [{:keys [organization-name all-projects]} (om/props this)]
      (dom/div nil
        (dom/h2 nil organization-name)
        (apply dom/ul nil
          (map
            (fn [project-data]
              (dom/li nil (project-factory project-data)))
            all-projects))))))

(def app-state (atom {:count 0}))

(defui Counter
  Object
  (render
    [this]
    (let [{:keys [count]} (om/props this)]
      (dom/div nil
        (dom/span nil (str "Count: " count))
        (dom/button
          #js {:onClick
               (fn [e]
                 (swap! app-state update-in [:count] inc))}
          "Click me!")))))

#?(:clj
   (defn make-reconciler
     []
     (om/reconciler
       {:state app-state})))

#?(:cljs
   (def project-reconciler
     (om/reconciler
       {:state app-state})))

#?(:cljs
   (defn projects-init
     [reconciler]
     (om/add-root! reconciler Counter (gdom/getElement "projects"))))
