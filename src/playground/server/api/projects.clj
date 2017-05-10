(ns playground.server.api.projects
  (:require
   [playground.server.db.projects :as db]))

(defrecord Project [name description organization-id])

(defn get-projects-in-organization
  [{:keys [db-spec]} organization-id]
  (db/get-all-projects-in-organization
    db-spec
    {:id organization-id}))

(defn get-all-user-projects
  [{:keys [db-spec]} user-name]
  (db/get-all-user-projects
    db-spec
    {:name user-name}))
