(ns playground.server.api.user-group-relations
  (:require
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation]))

(defonce table "user_group_relations")

(spec/def ::user-id #(spec/valid? ::validation/valid-id))

(spec/def ::group-id #(spec/valid? ::validation/valid-id))

(spec/def ::user-group-relations
  (spec/keys
    :req-un [::user-id
             ::group-id]))

(defrecord UserGroupRelation [user-id group-id])
