(ns playground.server.api.user-group-relations
  (:require
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation]))

(defrecord UserGroupRelation [user-id group-id])
