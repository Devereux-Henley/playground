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

(defrecord Role [token description default-value])
