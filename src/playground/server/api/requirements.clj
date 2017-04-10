(ns playground.server.api.requirements
  (:require
   [clojure.spec :as spec]
   [playground.server.db.requirements :as db]))

(spec/def ::valid-id (spec/and integer? #(> % 0)))

(defn validate-single-id
  [db-call input-id]
  (if (spec/valid? ::valid-id input-id)
    (db-call {:id input-id})
    (spec/explain-data ::valid-id input-id)))

(defrecord Requirement [name description project])

(defn get-requirements-in-project
  [db-spec project-id]
  (validate-single-id (partial db/get-requirements-by-project db-spec) project-id))

(defn get-top-level-requirements-in-projects
  [db-spec project-id]
  (validate-single-id (partial db/get-top-level-requirements-by-project db-spec) project-id))

(defn get-ancestors-by-id
  [db-spec requirement-id]
  (validate-single-id (partial db/get-ancestors-by-id db-spec) requirement-id))

(defn get-descendants-by-id
  [db-spec requirement-id]
  (validate-single-id (partial db/get-descendants-by-id db-spec) requirement-id))

(defn get-requirement-by-id
  [db-spec requirement-id]
  (validate-single-id (partial db/get-requirement-by-id db-spec) requirement-id))
