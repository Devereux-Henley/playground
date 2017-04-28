(ns playground.server.specs.user-groups
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::id #(spec/valid? ::standard/valid-id %))

(spec/def ::name (spec/and
                   string?
                   #(not (empty? %))
                   #(re-matches #"^[a-zA-Z0-9]*$" %)))

(spec/def ::project-id #(spec/valid? ::id %))

(spec/def ::user-group (spec/keys
                         :req-un [::name
                                  ::project-id]))

(spec/def ::update-params (spec/keys
                            :req-un [::id
                                     ::user-group]))
