(ns playground.server.api.users
  (:require
   [buddy.hashers :refer [derive check]]
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record
                                                            assoc-table
                                                            assoc-inserts
                                                            assoc-updates]]
   [playground.server.db.standard :as standard-db]
   [playground.server.db.users :as user-db]
   [playground.server.specs.users :as user-specs]))

(defonce table "users")

(defn- dissoc-password
  [output]
  (cond
    (nil? output) nil
    (seq? output) (map #(dissoc % :password_hash) output)
    :else (dissoc output :password_hash)))

(defn- hash-password-in-map
  [{:keys [password] :as input-map}]
  (let [corrected-map (dissoc input-map :password)]
    (if password
      (assoc corrected-map :password-hash (derive password))
      input-map)))

(defn- assoc-user-table
  [input-map]
  (assoc-table table input-map))

(defn validate-single-user-name
  [db-call user-name]
  (validate-single-record db-call ::user-specs/user-auth {:user-name user-name}))

(defn validate-single-user
  [db-call user]
  (validate-single-record db-call ::user-specs/user user))

(defn validate-single-user-update
  [db-call user-id user]
  (validate-single-record db-call ::user-specs/update-params {:id user-id
                                                   :updates user}))

(defrecord User [first-name last-name user-name password])

;; GET requests

(defn get-all-users
  [{:keys [db-spec]}]
  (read-call-wrapper
    #(dissoc-password (standard-db/get-all db-spec {:table table}))))

(defn get-user-by-id
  [{:keys [db-spec]} user-id]
  (read-call-wrapper
    #(dissoc-password
       (validate-single-id
         (comp (partial standard-db/get-by-id db-spec)
           assoc-user-table)
         user-id))))

;; PUT requests

(defn insert-user!
  [{:keys [db-spec]} user]
  (mutate-call-wrapper
    #(validate-single-user
       (comp (partial standard-db/insert! db-spec)
         assoc-user-table
         assoc-inserts
         hash-password-in-map)
       user)))

;; UPDATE requests

(defn update-user-by-id!
  [{:keys [db-spec]} user-id user]
  (mutate-call-wrapper
    #(validate-single-user-update
       (comp (partial standard-db/update-by-id! db-spec)
         assoc-user-table
         (fn [{:keys [updates] :as update-map}]
           (assoc update-map :updates
             (hash-password-in-map updates))))
       user-id user)))
