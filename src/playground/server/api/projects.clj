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

(spec/def ::description #(spec/valid? ::validation/standard-description %))

(spec/def ::project (spec/keys :req-un [::name
                                        ::description]))

(spec/def ::project-updates (spec/and
                              #(not (empty? %))
                              (spec/keys :opt-un [::name
                                                  ::description])))
(spec/def ::project-update-params
  (spec/keys
    :req-un [::id ::project-updates]))

(defn validate-single-project
  [db-call project]
  (validate-single-record db-call ::project project))

(defn validate-single-update
  [db-call project-id project]
  (validate-single-record db-call ::project-update-params {:id project-id
                                                           :project-updates project}))

(defrecord Project [name description])

;; GET requests

(defn get-all-projects
  [db-spec]
  (read-call-wrapper
    #(db/get-all-projects db-spec)))

(defn get-project-by-id
  [db-spec project-id]
  (read-call-wrapper
    #(validate-single-id (partial db/get-project-by-id db-spec) project-id)))

;; PUT requests

(defn insert-project!
  [db-spec project]
  (mutate-call-wrapper
    #(validate-single-project (partial db/insert-project! db-spec) project)))

(defn update-project-by-id!
  [db-spec project-id project]
  (mutate-call-wrapper
    #(validate-single-update (partial db/update-project-by-id! db-spec) project-id project)))
