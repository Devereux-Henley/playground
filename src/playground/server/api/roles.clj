(ns playground.server.api.roles
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record
                                                            assoc-table
                                                            assoc-inserts
                                                            assoc-updates]]
   [playground.server.db.standard :as db]))

(defonce table "roles")

(spec/def ::id #(spec/valid? ::validation/valid-id %))

(spec/def ::token (spec/and
                    string?
                    #(not (empty? %))
                    #(re-matches #"^[a-zA-Z_]*$" %)))

(spec/def ::description #(spec/valid? ::validation/standard-description %))

(spec/def ::default-value boolean?)

(spec/def ::role (spec/keys
                   :req-un [::token
                            ::description
                            ::default-value]))

(spec/def ::updates (spec/keys
                                 :opt-un [::token
                                          ::description
                                          ::default-value]))

(spec/def ::update-params (spec/keys
                                 :req-un [::id
                                          ::updates]))

(defn- assoc-role-table
  [input-map]
  (assoc-table table input-map))

(defn validate-single-role
  [db-call role]
  (validate-single-record db-call ::role role))

(defn validate-single-role-update
  [db-call role-id role]
  (validate-single-record db-call ::update-params {:id role-id
                                                        :updates role}))

(defrecord Role [token description default-value])

(defn get-all-roles
  [db-spec]
  (read-call-wrapper
    #(db/get-all db-spec {:table table})))

(defn get-role-by-id
  [db-spec role-id]
  (read-call-wrapper
    #(validate-single-id
       (comp (partial db/get-by-id db-spec)
         assoc-role-table)
       role-id)))

(defn insert-role!
  [db-spec role]
  (mutate-call-wrapper
    #(validate-single-role
       (comp (partial db/insert! db-spec)
         assoc-role-table
         assoc-inserts)
       role)))

(defn update-role-by-id!
  [db-spec role-id role]
  (mutate-call-wrapper
    #(validate-single-role-update
       (comp (partial db/update-by-id! db-spec)
         assoc-role-table)
       role-id role)))

(defn delete-role-by-id!
  [db-spec role-id]
  (mutate-call-wrapper
    #(validate-single-id
       (comp (partial db/delete-by-id! db-spec)
         assoc-role-table)
       role-id)))
