(ns playground.server.api.authorization
  (:require
   [buddy.hashers :refer [derive check]]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record
                                                            assoc-table
                                                            assoc-inserts
                                                            assoc-updates]]
   [playground.server.specs.users :as users]
   [playground.server.db.authorization :refer [get-hash get-roles-by-user-id]]))

;; Non-CRUD requests
(defn get-user-roles
  [db-spec id]
  (read-call-wrapper
    (get-roles-by-user-id db-spec {:id id})))

(defn auth-user
  [db-spec {:keys [user password]}]
  (read-call-wrapper
    (fn []
      (let [{:keys [id password_hash]} (validate-single-record
                                         (partial get-hash db-spec)
                                         ::users/user-auth
                                         {:user-name user})]
        {:id id
         :authenticated? (check password password_hash)}))))
