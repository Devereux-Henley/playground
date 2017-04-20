(ns playground.server.api.users
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record]]
   [playground.server.db.standard :as db]))

(defonce table "users")

(spec/def ::id #(spec/valid? ::validation/valid-id))

(spec/def ::user-name (spec/and
                        string?
                        #(not (empty? %))
                        #(re-matches #"^[a-zA-Z0-9]*$" %)))

(spec/def ::first-name (spec/and
                         string?
                         #(not (empty? %))
                         #(re-matches #"^[a-zA-Z\-]" %)))

(spec/def ::last-name (spec/and
                        string?
                        #(not (empty? %))
                        #(re-matches #"^[a-zA-Z\-]" %)))

(spec/def ::password (spec/and
                       string?
                       #(< 8 (count %))
                       #(re-matches #"^(?!.*([A-Za-z0-9])\1{2})(?=.*[a-z])(?=.*\d)[A-Za-z0-9]{8,16}$")))

(spec/def ::user (spec/keys
                   :req-un [::first-name
                            ::last-name
                            ::user-name
                            ::password]))

(spec/def ::user-update-params (spec/keys
                                 :opt-un [::first-name
                                          ::last-name
                                          ::user-name
                                          ::password]))

(defn assoc-table
  [input-map]
  (assoc input-map :table table))

(defn dissoc-password
  [output]
  (cond
    (nil? output) nil
    (seq? output) (map #(dissoc % :password) output)
    :else (dissoc output :password)))

(defn validate-single-user
  [db-call user]
  (validate-single-record db-call ::user user))

(defn validate-single-user-update
  [db-call user-id user]
  (validate-single-record db-call ::user-update-params {:id user-id
                                                        :updates user}))

(defrecord User [first-name last-name user-name password])

;; GET requests

(defn get-all-users
  [db-spec]
  (read-call-wrapper
    #(dissoc-password (db/get-all db-spec {:table table}))))

(defn get-user-by-id
  [db-spec user-id]
  (read-call-wrapper
    #(dissoc-password
       (validate-single-id
                        (comp (partial db/get-by-id db-spec)
                          assoc-table)
                        user-id))))

;; PUT requests

(defn insert-user!
  [db-spec user]
  (mutate-call-wrapper
    #(validate-single-user
       (comp (partial db/insert! db-spec)
         assoc-table)
       user)))

;; UPDATE requests

(defn update-user-by-id!
  [db-spec user-id user]
  (mutate-call-wrapper
    #(validate-single-user-update
       (comp (partial db/update-by-id! db-spec)
         assoc-table)
       user-id user)))

;; DELETE requests

(defn delete-user-by-id!
  [db-spec user-id]
  (mutate-call-wrapper
    #(validate-single-id
       (comp (partial db/delete-by-id! db-spec)
         assoc-table)
       user-id)))
