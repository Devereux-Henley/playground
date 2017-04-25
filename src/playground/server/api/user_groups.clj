(ns playground.server.api.user-groups
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
   [playground.server.db.standard :as standard-db]))

(defonce table "user_groups")

(spec/def ::id #(spec/valid? ::validation/valid-id %))

(spec/def ::name (spec/and
                   string?
                   #(not (empty? %))
                   #(re-matches #"^[a-zA-Z0-9]*$" %)))

(spec/def ::project-id #(spec/valid? ::id %))

(spec/def ::user-group (spec/keys
                         :req-un [::name
                                  ::project-id]))

(spec/def ::update-params (spec/keys
                            :req-un [::id
                                     ::user-group]))

(defn- assoc-user-group-table
  [input-map]
  (assoc-table table input-map))

(defn validate-single-user-group
  [db-call user-group]
  (validate-single-record db-call ::user-group user-group))

(defrecord UserGroup [name project-id])

;; GET requests

(defn get-all-user-groups
  [db-spec]
  (read-call-wrapper
    #(standard-db/get-all db-spec {:table table})))

(defn get-user-groups-by-id
  [db-spec user-group-id]
  (read-call-wrapper
    #(validate-single-id
       (comp (partial standard-db/get-by-id db-spec)
         assoc-user-group-table)
       user-group-id)))

;; PUT requests

(defn insert-user-group!
  [db-spec user-group]
  (mutate-call-wrapper
    #(validate-single-user-group
       (comp (partial standard-db/insert! db-spec
            assoc-user-group-table
            assoc-inserts)
         user-group))))

;; UPDATE requests

(defn update-user-by-id!
  [db-spec user-group-id user-group]
  (mutate-call-wrapper
    #(validate-single-user-group)))
