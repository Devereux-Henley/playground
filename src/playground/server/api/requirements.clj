(ns playground.server.api.requirements
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record]]
   [playground.server.specs.requirements :as req-specs]
   [playground.server.db.requirements :as db]))

;; Validation wrappers.

(defn validate-single-requirement
  [db-call record]
  (validate-single-record db-call ::req-specs/requirement record))

(defn validate-single-requirements-path
  [db-call record]
  (validate-single-record db-call ::req-specs/requirements-path record))

(defn validate-single-update
  [db-call update-spec]
  (validate-single-record db-call ::req-specs/update-params update-spec))

;; Records

(defrecord Requirement [requirement-project])

(defrecord RequirementEdit [name description requirement-id edit-type])

(defrecord RequirementsPath [ancestor-id descendant-id])

;; GET requests.
(defn get-requirements-by-project-id
  [{:keys [db-spec]} project-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-requirements-by-project db-spec) project-id)))

(defn get-top-level-requirements-in-projects
  [{:keys [db-spec]} project-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-top-level-requirements-by-project db-spec) project-id)))

(defn get-ancestors-by-id
  [{:keys [db-spec]} requirement-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-ancestors-by-id db-spec) requirement-id)))

(defn get-descendants-by-id
  [{:keys [db-spec]} requirement-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-descendants-by-id db-spec) requirement-id)))

(defn get-requirement-by-id
  [{:keys [db-spec]} requirement-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-requirement-by-id db-spec) requirement-id)))

;; UPDATE requests

(defn update-requirement!
  [db-spec requirement-id requirement]
  (mutate-call-wrapper
    #(validate-single-update
       (partial db/update-requirement! db-spec)
       {:requirement-id requirement-id
        :requirement-updates requirement})))

;; PUT requests

(defn insert-root-requirement!
  [db-spec requirement]
  (mutate-call-wrapper
    #(jdbc/with-db-transaction [tx db-spec]
       (validate-single-requirement
         (comp
           (fn [{:keys [id]}] (db/insert-new-relation! tx {:id id}))
           (partial db/insert-requirement! tx))
         requirement))))

(defn insert-requirement-child!
  [db-spec parent-id requirement]
  (mutate-call-wrapper
    (jdbc/with-db-transaction [tx db-spec]
      (->
        (fn [{:keys [id]}]
          (->
            (fn [db-result] (db/insert-new-relation!
                             tx
                             {:ancestor-id id
                              :descendant-id (:id db-result)}))
            (comp (partial db/insert-requirement! tx))
            (validate-single-requirement requirement)))
        (validate-single-id parent-id)))))

;; DELETE requests

(defn delete-requirement!
  [db-spec requirement-id]
  (mutate-call-wrapper
    #(jdbc/with-db-transaction [tx db-spec]
       (validate-single-id
         (juxt
           (partial db/delete-requirement-relationships! tx)
           (partial db/delete-requirement-by-id! tx))
         requirement-id))))
