(ns playground.server.specs.team-projects
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::project-id #(spec/valid? ::standard/valid-id))

(spec/def ::team-id #(spec/valid? ::standard/valid-id))

(spec/def ::team-project (spec/keys
                           :req-un [::project-id
                                    ::team-id]))
