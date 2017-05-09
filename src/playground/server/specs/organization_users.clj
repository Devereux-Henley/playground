(ns playground.server.specs.organization-users
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::user-id #(spec/valid? ::standard/valid-id))

(spec/def ::organization-id #(spec/valid? ::standard/valid-id))

(spec/def ::organization-user (spec/keys
                                :req-un [::user-id
                                         ::organization-id]))
