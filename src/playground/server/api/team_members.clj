(ns playground.server.api.team-members
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record
                                                            assoc-table]]
   [playground.server.db.standard :as standard-db]
   [playground.server.db.team-members :as db]))

(defonce table "team_members")

(spec/def ::user-id #(spec/valid? ::validation/valid-id))

(spec/def ::team-id #(spec/valid? ::validation/valid-id))

(spec/def ::team-member (spec/keys
                          :req-un [::user-id
                                   ::team-id]))

(defrecord TeamMember [user-id team-id])

(defn- assoc-team-member-table
  [input-map]
  (assoc-table table input-map))

(defn validate-single-team-member
  [db-call team-member]
  (validate-single-record db-call ::team-member team-member))

(defn get-all-team-members
  [db-spec]
  (read-call-wrapper
    #(standard-db/get-all db-spec {:table table})))

(defn insert-team-member!
  [db-spec team-member]
  (mutate-call-wrapper
    #(validate-single-team-member
       (comp (partial standard-db/insert! db-spec)
         assoc-team-member-table)
       team-member)))

(defn delete-team-member!
  [db-spec team-member]
  (mutate-call-wrapper
    #(validate-single-team-member
       (comp (partial db/delete-team-member! db-spec)
         assoc-team-member-table)
       team-member)))
