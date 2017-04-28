(ns playground.server.specs.user-group-relations
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::user-id #(spec/valid? ::standard/valid-id))

(spec/def ::group-id #(spec/valid? ::standard/valid-id))

(spec/def ::user-group-relations
  (spec/keys
    :req-un [::user-id
             ::group-id]))
