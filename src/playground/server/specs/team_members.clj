(ns playground.server.specs.team-members
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::user-id #(spec/valid? ::standard/valid-id))

(spec/def ::team-id #(spec/valid? ::standard/valid-id))

(spec/def ::team-member (spec/keys
                          :req-un [::user-id
                                   ::team-id]))
