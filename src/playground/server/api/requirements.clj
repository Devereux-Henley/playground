(ns playground.server.api.requirements
  (:require
   [clojure.spec :as spec]
   [playground.server.db.requirements :as db]))

(spec/def ::valid-id (spec/and integer?
                       #(> % 0)))

(spec/def ::requirement-name (spec/and
                               string?
                               #(not (empty %))
                               #(re-matches #"^[a-zA-Z0-9]*$" %)))

(spec/def ::requirement-description (spec/and
                                      string?
                                      #(not (empty %))))

(spec/def ::requirement-project #(spec/valid? ::valid-id %))

(spec/def ::requirement (spec/keys :req-un [::requirement-name
                                            ::requirement-description
                                            ::requirement-project]))

(spec/def ::ancestor-id #(spec/valid? ::valid-id %))

(spec/def ::descendant-id #(spec/valid? ::valid-id %))

(spec/def ::requirements-path (spec/keys :req-un [::ancestor-id
                                                  ::descendantid]))

(defn validate-single-id
  [db-call input-id]
  (if (spec/valid? ::valid-id input-id)
    (db-call {:id input-id})
    (spec/explain-data ::valid-id input-id)))

;; Records
(defrecord Requirement [requirement-name requirement-description requirement-project])

(defrecord RequirementsPath [ancestor-id descendant-id])

;; GET requests.

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

;; PUT requests
(defn insert-requirement!
  [db-spec requirement]
  (if (spec/valid? ::requirement requirement)
    (db/insert-requirement db-spec requirement)
    (spec/explain-data ::requirement requirement)))
