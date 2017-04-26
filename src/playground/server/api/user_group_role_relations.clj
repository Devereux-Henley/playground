(ns playground.server.api.user-group-role-relations
  (:require
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation]))

(defonce table "user_group_role_relations")

(spec/def ::role-id #(spec/valid? ::validation/valid-id))

(spec/def ::group-id #(spec/valid? ::validation/valid-id))

(spec/def ::user-group-role-relation
  (spec/keys
    :req-un [::role-id
             ::group-id]))

(defrecord UserGroupRoleRelation [role-id group-id])
