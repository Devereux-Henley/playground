(ns playground.server.api.projects
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record]]
   [playground.server.db.projects :as db]))

(spec/def ::id #(spec/valid? ::validation/valid-id %))

(spec/def ::name (spec/and
                   string?
                   #(not (empty? %))
                   #(re-matches #"^[a-zA-Z0-9\-\.\s]*$" %)))

(spec/def ::description (spec/and
                          string?
                          #(not (empty? %))))

(spec/def ::project (spec/keys :req-un [::name
                                        ::description]))

(spec/def ::project-updates (spec/and
                              #(not (empty? %))
                              (spec/keys :opt-un [::name
                                                  ::description])))
(spec/def ::project-update-params
  (spec/keys
    :req-un [::id ::project-updates]))
