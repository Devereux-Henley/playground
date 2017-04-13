(ns playground.server.api.requirements
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.db.requirements :as db]))

;; Generic database id spec.

(spec/def ::valid-id (spec/and integer?
                       #(> % 0)))

;; Requirements validation specs.

(spec/def ::requirement-id #(spec/valid? ::valid-id %))

(spec/def ::requirement-name (spec/and
                               string?
                               #(not (empty %))
                               #(re-matches #"^[a-zA-Z0-9]*$" %)))

(spec/def ::requirement-description (spec/and
                                      string?
                                      #(not (empty %))))

(spec/def ::requirement-project-id #(spec/valid? ::valid-id %))

(spec/def ::requirement (spec/keys :req-un [::requirement-name
                                            ::requirement-description
                                            ::requirement-project]))

(spec/def ::requirement-updates (spec/and #(not (empty? %))
                                  (spec/keys
                                    :opt-un [::requirement-name
                                             ::requirement-description
                                             ::requirement-project])))

(spec/def ::requirement-update-params
  (spec/keys
    :req-un [::requirement-id ::requirement-updates]))

(spec/def ::requirement-insert (spec/keys :req [::requirement-id ::requirement]))

;; RequirementsPaths validation specs.

(spec/def ::ancestor-id #(spec/valid? ::valid-id %))

(spec/def ::descendant-id #(spec/valid? ::valid-id %))

(spec/def ::requirements-path (spec/keys :req-un [::ancestor-id
                                                  ::descendantid]))

;; Validation wrappers.

(defn validate-single-record
  [db-call spec-key record]
  (if (spec/valid? spec-key record)
    (db-call record)
    (spec/explain-data spec-key record)))

(defn validate-single-requirement
  [db-call record]
  (validate-single-record db-call ::requirement record))

(defn validate-single-requirements-path
  [db-call record]
  (validate-single-record db-call ::requirements-path record))

(defn validate-single-update
  [db-call update-spec]
  (validate-single-record db-call ::requirement-update-params update-spec))

(defn validate-single-id
  [db-call input-id]
  (if (spec/valid? ::valid-id input-id)
    (db-call {:id input-id})
    (spec/explain-data ::valid-id input-id)))

;; Records

(defrecord Requirement [requirement-name requirement-description requirement-project-id])

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

;; UPDATE requests

(defn update-requirement!
  [db-spec requirement-id requirement]
  (validate-single-update
    (partial db/update-requirement! db-spec)
    {:requirement-id requirement-id
     :requirement-updates requirement}))

;; PUT requests

(defn insert-requirement!
  [db-spec requirement]
  (validate-single-requirement
    (do (partial db/insert-requirement! db-spec) {:status "Success"})
    requirement))

(defn insert-requirement-child-relation!
  [db-spec requirements-relation]
  (validate-single-requirements-path
    (partial db/insert-requirement-child! db-spec)
    requirements-relation))

;; DELETE requests

(defn delete-requirement-child-relation!
  [db-spec requirement-id]
  (validate-single-id (partial db/delete-requirement-child! db-spec) requirement-id))

(defn delete-requirement-children-relations!
  [db-spec requirement-id]
  (validate-single-id (partial db/delete-requirement-child-subtree! db-spec) requirement-id))
