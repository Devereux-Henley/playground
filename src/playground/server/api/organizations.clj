(ns playground.server.api.organizations
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record]]
   [playground.server.db.organizations :as db]))

(spec/def ::id #(spec/valid? ::validation/valid-id %))

(spec/def ::name (spec/and
                   string?
                   #(not (empty %))
                   #(re-matches #"^[a-zA-Z0-9\-\.\s]*$" %)))

(spec/def ::description #(spec/valid? ::validation/standard-description %))

(spec/def ::organization (spec/keys :req-un [::name
                                             ::description]))

(spec/def ::organization-updates (spec/and
                                   #(not (empty? %))
                                   (spec/keys :opt-un [::name
                                                       ::description])))

(spec/def ::organization-update-params
  (spec/keys
    :req-un [::id ::organization-updates]))

(defn validate-single-organization
  [db-call organization]
  (validate-single-record db-call ::organization organization))

(defn validate-single-update
  [db-call organization-id organization]
  (validate-single-record db-call ::organization-update-params {:id organization-id
                                                                :organization-updates organization}))

(defrecord Organization [name description])

;; GET requests

(defn get-all-organizations
  [db-spec]
  (read-call-wrapper
    #(db/get-all-organizations db-spec)))

(defn get-organization-by-id
  [db-spec organization-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-organization-by-id db-spec) organization-id)))

;; PUT requests

(defn insert-organization!
  [db-spec organization]
  (mutate-call-wrapper
    #(validate-single-organization (partial db/insert-organization! db-spec) organization)))

(defn update-organization-by-id!
  [db-spec organization-id organization]
  (mutate-call-wrapper
    #(validate-single-update (partial db/update-organization-by-id! db-spec) organization-id organization)))
