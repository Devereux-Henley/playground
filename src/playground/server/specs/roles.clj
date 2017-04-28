(ns playground.server.specs.roles
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::id #(spec/valid? ::standard/valid-id %))

(spec/def ::token (spec/and
                    string?
                    #(not (empty? %))
                    #(re-matches #"^[a-zA-Z_]*$" %)))

(spec/def ::description #(spec/valid? ::standard/standard-description %))

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
