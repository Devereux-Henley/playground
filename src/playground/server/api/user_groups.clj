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

(defrecord UserGroup [name project-id])
