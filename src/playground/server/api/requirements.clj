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
  [db-call record]
  (validate-single-record db-call ::req-specs/requirement-update record))

;; Records

(defrecord Requirement [requirement-name
                        requirement-description
                        requirement-project])

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
       (partial db/insert-requirement-edit! db-spec)
       (assoc requirement :requirement-id requirement-id))))

;; PUT requests

(defn insert-root-requirement!
  [{:keys [db-spec]} requirement]
  (mutate-call-wrapper
    #(jdbc/with-db-transaction [tx db-spec]
       (validate-single-requirement
         (comp
           (fn [{:keys [id]}] (db/insert-new-relation! tx {:id id}) id)
           (fn [{:keys [id] :as requirement}]
             (let [requirement (assoc requirement :requirement-id id)]
               (db/insert-requirement-creation! tx requirement)
               requirement))
           (fn [requirement] (-> (db/insert-requirement! tx requirement) (merge requirement))))
         requirement))))

(defn insert-requirement-child!
  [{:keys [db-spec]} parent-id requirement]
  (mutate-call-wrapper
    #(jdbc/with-db-transaction [tx db-spec]
       (->
         (fn [{:keys [id]}]
           (->
             (comp
               (fn [db-result] (db/insert-requirement-child!
                                tx
                                {:ancestor-id id
                                 :descendant-id (:id db-result)}))
               (fn [{:keys [id] :as requirement}]
                 (let [requirement (assoc requirement :requirement-id id)]
                   (db/insert-requirement-creation! tx requirement)
                     requirement))
               (fn [requirement] (-> (db/insert-requirement! tx requirement) (merge requirement))))
             (validate-single-requirement requirement)))
         (validate-single-id parent-id)))))

;; DELETE requests

(defn delete-requirement!
  [{:keys [db-spec]} requirement-id]
  (mutate-call-wrapper
    #(jdbc/with-db-transaction [tx db-spec]
       (validate-single-id
         (juxt
           (partial db/delete-requirement-relationships! tx)
           (partial db/delete-requirement-by-id! tx))
         requirement-id))))
