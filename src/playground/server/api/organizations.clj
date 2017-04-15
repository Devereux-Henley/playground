(ns playground.server.api.organizations
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record]]
   [playground.server.db.organizations :as db]))

(spec/def ::organization-name (spec/and
                                string?
                                #(not (empty %))
                                #(re-matches #"^[a-zA-Z\-]*$" %)))

(spec/def ::organization-description (spec/and
                                       string?
                                       #(not (empty? %))))

(spec/def ::organization (spec/keys :req-un [::organization-name
                                             ::organization-description]))

(spec/def ::organization-updates (spec/and
                                   #(not (empty? %))
                                   (spec/keys :req-un [::organization-name
                                                       ::organization-description])))

(spec/def ::organization-update-params
  (spec/keys
    :req-un [:validation/valid-id ::organization-updates]))

(defn validate-single-organization
  [db-call organization]
  (validate-single-record db-call ::organization organization))

(defn validate-single-update
  [db-call organization-id organization]
  (validate-single-record db-call ::organization-update-params {:validation/valid-id organization-id
                                                                ::organization-updates organization}))

(defrecord Organization [organization-name organization-description])

;; GET requests

(defn get-organization-by-id
  [db-spec organization-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-organization-by-id db-spec) organization-id)))

;; PUT requests

(defn insert-organization!
  [db-spec organization]
  (mutate-call-wrapper
    #(validate-single-organization (partial db/insert-organization! db-spec) organization)))

(defn update-organization!
  [db-spec organization-id organization]
  (mutate-call-wrapper
    #(validate-single-update (partial db/update-organization-by-id! db-spec) organization-id organization)))
