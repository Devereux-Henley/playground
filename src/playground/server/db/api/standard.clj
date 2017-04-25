(ns playground.server.db.api.standard
  (:require
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record
                                                            assoc-table
                                                            assoc-inserts
                                                            assoc-updates]]
   [playground.server.db.standard :as db]
   [playground.server.db.standard :as standard-db]))

(defrecord CRUDL [db-spec table record-spec update-spec])

(defprotocol Create (create-record [this]))

(extend-protocol Create
  CRUDL (create-record [{:keys [db-spec table record-spec]} record]
          (mutate-call-wrapper
            #(validate-single-record
               (comp (partial db/insert! db-spec)
                 (fn [input-map] (assoc-table table input-map)))
               record-spec
               record))))

(defprotocol Read (read-record [this]))

(extend-protocol Read
  CRUDL (read-record [{:keys [db-spec table]} record]
          (read-call-wrapper
            #(validate-single-id
               (comp (partial db/get-by-id db-spec)
                 (fn [input-map] (assoc-table table input-map)))
               record))))

(defprotocol Update (update-record [this]))

(extend-protocol Update
  CRUDL (update-record [{:keys [db-spec table update-spec]} record-id record]
          (mutate-call-wrapper
            #(validate-single-record
               (comp (partial db/update-by-id! db-spec)
                 (fn [input-map] (assoc-table table input-map)))
               update-spec
               {:id record-id
                :record record}))))

(defprotocol Delete (delete-record [this] "nephew"))

(extend-protocol Delete
  CRUDL (delete-record [{:keys [db-spec table]} record-id]
          (mutate-call-wrapper
            #(validate-single-id
               (comp (partial standard-db/delete-by-id! db-spec)
                 (fn [input-map] (assoc-table table input-map)))
               record-id))))

(defprotocol List (list-record [this]))

(extend-protocol List
  CRUDL (list-record [{:keys [db-spec table]}]
          (read-call-wrapper
            #(db/get-all db-spec {:table table}))))
