(ns playground.server.specs.user-group-role-relations
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::role-id #(spec/valid? ::standard/valid-id))

(spec/def ::group-id #(spec/valid? ::standard/valid-id))

(spec/def ::user-group-role-relation
  (spec/keys
    :req-un [::role-id
             ::group-id]))
