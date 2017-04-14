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

(spec/def ::requirement-project #(spec/valid? ::valid-id %))

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
;; Results

(defonce success
  {:success "true"})

(defn failure
  [failure-message]
  {:success "false"
   :error failure-message})

(defn read-call-wrapper
  [unsafe-call]
  (try
    (merge
      {:results (unsafe-call)}
      success)
    (catch Exception e (failure (or (ex-data e) (.getMessage e))))))

(defn mutate-call-wrapper
  [unsafe-call]
  (try
    (do
      (unsafe-call)
      success)
    (catch Exception e (failure (or (ex-data e) (.getMessage e))))))

;; Validation wrappers.

(defn validate-single-record
  [db-call spec-key record]
  (if (spec/valid? spec-key record)
    (db-call record)
    (throw (ex-info "Invalid input" (spec/explain-data spec-key record)))))

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
    (throw (ex-info "Invalid input" (spec/explain-data ::valid-id input-id)))))

;; Records

(defrecord Requirement [requirement-name requirement-description requirement-project])

(defrecord RequirementsPath [ancestor-id descendant-id])

;; GET requests.
(defn get-requirements-in-project
  [db-spec project-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-requirements-by-project db-spec) project-id)))

(defn get-top-level-requirements-in-projects
  [db-spec project-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-top-level-requirements-by-project db-spec) project-id)))

(defn get-ancestors-by-id
  [db-spec requirement-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-ancestors-by-id db-spec) requirement-id)))

(defn get-descendants-by-id
  [db-spec requirement-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-descendants-by-id db-spec) requirement-id)))

(defn get-requirement-by-id
  [db-spec requirement-id]
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
