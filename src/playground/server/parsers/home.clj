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

(defmethod read-home-data :project/projects-in-organization
  [{:keys [user resources state] :as env} _ {:keys [organization/organization-id] :as query}]
  (let [{:keys [projects]} resources]
    (let [value (mapv
                  (partial db-to-api (:db-mappings projects))
                  (projects/get-projects-in-organization
                    projects
                    organization-id))]
      {:value value})))

(defmulti mutate-home-data om/dispatch)

(defmethod mutate-home-data :default
  [_ _ _]
  {:value {:error "Cannot mutate this data."}})

(defonce home-parser
  (om/parser {:read read-home-data
              :mutate mutate-home-data}))
