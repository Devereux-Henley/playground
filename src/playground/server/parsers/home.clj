(ns playground.server.parsers.home
  (:require
   [om.next :as om]
   [playground.server.api.organizations :as organizations]
   [playground.server.api.projects :as projects]
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]
   [playground.server.api.requirements :as requirements]
   [playground.server.util :refer [db-to-api]]))

(defmulti read-home-data om/dispatch)

(defmethod read-home-data :default
  [_ k _]
  {:value {:error (str "No handler for key" k)}})

(defmethod read-home-data :page/title
  [_ _ _]
  {:value "Baz"})

(defmethod read-home-data :current/user
  [{:keys [user resources] :as env} _ _]
  {:value {:user/name user}})

(defmethod read-home-data :organizations/organizations-by-id
  [{:keys [user resources] :as env} _ _]
  (let [{:keys [organizations]} resources]
    {:value
     (into {}
       (mapv
         (fn [{:keys [id] :as record}]
           {id (db-to-api (:db-mappings organizations) record)})
         (organizations/get-organizations-by-user-name
           organizations
           user)))}))

(defmethod read-home-data :projects/projects-by-id
  [{:keys [user resources] :as env} _ _]
  (let [{:keys [projects]} resources]
    (let [value
          (into {}
            (mapv
              (fn [{:keys [id] :as record}]
                {id (db-to-api (:db-mappings projects) record)})
              (projects/get-all-user-projects
                projects
                user)))]
      {:value value})))

(defmethod read-home-data :requirements/requirements-list
  [{:keys [user resources] :as env} _ {:keys [project-ids]}]
  (when project-ids
    (let [{:keys [requirements]} resources]
      (let [value
            (into {}
              (mapv
                (fn [{:keys [id] :as record}]
                  (clojure.pprint/pprint record)
                  {id (db-to-api (:secondary-mappings requirements) record)})
                (:results
                 (requirements/get-top-level-requirements-in-project-ids
                   requirements
                   (vec project-ids)))))]
        {:value value}))))

(defmulti mutate-home-data om/dispatch)

(defmethod mutate-home-data :default
  [_ _ _]
  {:value {:error "Cannot mutate this data."}})

(defonce home-parser
  (om/parser {:read read-home-data
              :mutate mutate-home-data}))
