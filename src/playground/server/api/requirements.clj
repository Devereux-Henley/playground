(ns playground.server.api.requirements
  (:require
   [clojure.spec :as spec]
   [playground.server.db.requirements :as db]))

(spec/def ::valid-id (spec/and integer? #(> 0 %)))

(defn get-requirements-in-project
  [db-spec project-id]
  (if (spec/valid? ::valid-id project-id)
    (db/get-requirements-by-project db-spec {:id project-id})
    (spec/explain-data ::valid-id project-id)))

(defn get-top-level-requirements-in-projects
  [db-spec project-id]
  (if (spec/valid? ::valid-id project-id)
    (db/get-top-level-requirements-by-project db-spec {:id project-id})
    (spec/explain-data ::valid-id project-id)))

(defn get-descendants-by-id
  [db-spec requirement-id]
  (if (spec/valid? ::valid-id requirement-id)
    (db/get-descendants-by-id db-spec {:id requirement-id})
    (spec/explain-data ::valid-id requirement-id)))

(defn get-requirement-by-id
  [db-spec requirement-id]
  (if (spec/valid? ::valid-id requirement-id)
    (db/get-requirement-by-id {:id requirement-id})
    (spec/explain-data ::valid-id requirement-id)))
