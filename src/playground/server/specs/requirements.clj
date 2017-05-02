(ns playground.server.specs.requirements
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

;; Requirements validation specs.

(spec/def ::requirement-id #(spec/valid? ::standard/valid-id %))

(spec/def ::requirement-name (spec/and
                               string?
                               #(not (empty %))
                               #(re-matches #"^[a-zA-Z0-9\-\.\s]*$" %)))

(spec/def ::requirement-description #(spec/valid? ::standard/standard-description %))

(spec/def ::requirement-project #(spec/valid? ::standard/valid-id %))


(spec/def ::requirement (spec/and #(not (empty? %))
                      (spec/keys
                        :req-un [::requirement-project])))

(spec/def ::requirement-insert (spec/keys :req [::requirement-id ::requirement]))

;; RequirementsPaths validation specs.

(spec/def ::ancestor-id #(spec/valid? ::standard/valid-id %))

(spec/def ::descendant-id #(spec/valid? ::standard/valid-id %))

(spec/def ::requirements-path (spec/keys :req-un [::ancestor-id
                                                  ::descendant-id]))
