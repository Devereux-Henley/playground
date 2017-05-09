(ns playground.server.specs.organizations
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::id #(spec/valid? ::standard/valid-id %))

(spec/def ::name (spec/and
                   string?
                   #(not (empty %))
                   #(re-matches #"^[a-zA-Z0-9\-\.\s]*$" %)))

(spec/def ::description #(spec/valid? ::standard/standard-description %))

(spec/def ::organization (spec/keys :req-un [::name
                                             ::description]))

(spec/def ::organization-updates (spec/and
                                   #(not (empty? %))
                                   (spec/keys :opt-un [::name
                                                       ::description])))

(spec/def ::organization-update-params
  (spec/keys
    :req-un [::id ::organization-updates]))
