(ns playground.server.api.team-members
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
   [playground.server.db.standard :as standard-db]
   [playground.server.db.team-members :as db]))

(defonce table "team_members")

(spec/def ::user-id #(spec/valid? ::validation/valid-id))

(spec/def ::team-id #(spec/valid? ::validation/valid-id))

(spec/def ::team-member (spec/keys
                          :req-un [::user-id
                                   ::team-id]))

(defrecord TeamMember [user-id team-id])
