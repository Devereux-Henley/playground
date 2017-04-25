(ns playground.server.api.standard
  (:require
   [playground.server.api.protocols :refer [Create Read Update Delete List]]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record
                                                            assoc-table
                                                            assoc-inserts
                                                            assoc-updates]]
   [playground.server.db.standard :as standard-db]))

(defn standard-create
  [db-spec table record-spec record]
  (mutate-call-wrapper
    #(validate-single-record
       (comp (partial standard-db/insert! db-spec)
         (fn [input-map] (assoc-table table input-map))
         (fn [input-map] {:inserts input-map}))
       record-spec
       record)))

(defn standard-read
  [db-spec table record-id]
  (read-call-wrapper
    #(validate-single-id
       (comp (partial standard-db/get-by-id db-spec)
         (fn [input-map] (assoc-table table input-map)))
       record-id)))

(defn standard-update
  [db-spec table update-spec record-id record]
  (mutate-call-wrapper
    #(validate-single-record
       (comp (partial standard-db/update-by-id! db-spec)
         (fn [input-map] (assoc-table table input-map)))
       update-spec
       {:id record-id
        :updates record})))

(defn standard-delete
  [db-spec table record-id]
  (mutate-call-wrapper
    #(validate-single-id
       (comp (partial standard-db/delete-by-id! db-spec)
         (fn [input-map] (assoc-table table input-map)))
       record-id)))

(defn pivot-delete
  [db-spec record-spec table pivot]
  (mutate-call-wrapper
    #(validate-single-record
       (comp (partial standard-db/delete-by-pivot! db-spec)
         (fn [input-map] (assoc-table table input-map))
         (fn [input-map] {:deletes input-map}))
       record-spec
       pivot)))

(defn standard-list
  [db-spec table]
  (read-call-wrapper
    #(standard-db/get-all db-spec {:table table})))

(defrecord StandardRestResource [db-spec table record-spec update-spec]
  Create
  (create-record
    [this record]
    (standard-create db-spec table record-spec record))
  Read
  (read-record
    [this record-id]
    (standard-read db-spec table record-id))
  Update
  (update-record
    [this record-id record]
    (standard-update db-spec table update-spec record-id record))
  Delete
  (delete-record
    [this record-id]
    (standard-delete db-spec table record-id))
  List
  (list-record
    [this]
    (standard-list db-spec table)))

(defrecord PivotRestResource [db-spec table record-spec]
  Create
  (create-record
    [this record]
    (standard-create db-spec table record-spec record))
  Delete
  (delete-record
    [this record]
    (pivot-delete db-spec table record-spec record))
  List
  (list-record
    [this]
    (standard-list db-spec table)))
