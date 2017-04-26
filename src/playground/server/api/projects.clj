(ns playground.server.api.projects
  (:require
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record]]
   [playground.server.db.projects :as db]))

(defonce table "projects")

(spec/def ::id #(spec/valid? ::validation/valid-id %))

(spec/def ::name (spec/and
                   string?
                   #(not (empty? %))
                   #(re-matches #"^[a-zA-Z0-9\-\.\s]*$" %)))

(spec/def ::description #(spec/valid? ::validation/standard-description %))

(spec/def ::organization-id #(spec/valid? ::validation/valid-id %))

(spec/def ::project (spec/keys :req-un [::name
                                        ::description
                                        ::organization-id]))

(spec/def ::updates (spec/and
                      #(not (empty? %))
                      (spec/keys :opt-un [::name
                                          ::description
                                          ::organization-id])))
(spec/def ::update-params
  (spec/keys
    :req-un [::id ::updates]))

(defrecord Project [name description organization-id])
