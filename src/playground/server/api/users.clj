(ns playground.server.api.users
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record]]
   [playground.server.db.standard :as db]))

(spec/def ::id #(spec/valid? ::validation/valid-id))

(spec/def ::user_name (spec/and
                       string?
                       #(not (empty? %))
                       #(re-matches #"^[a-zA-Z0-9]*$" %)))

(spec/def ::first_name (spec/and
                         string?
                         #(not (empty? %))
                         #(re-matches #"^[a-zA-Z\-]" %)))

(spec/def ::last_name (spec/and
                        string?
                        #(not (empty? %))
                        #(re-matches #"^[a-zA-Z\-]" %)))

(spec/def ::password (spec/and
                       string?
                       #(< 8 (count %))
                       #(re-matches #"^(?!.*([A-Za-z0-9])\1{2})(?=.*[a-z])(?=.*\d)[A-Za-z0-9]{8,16}$")))

(spec/def ::user (spec/keys
                   :req-un [::first_name
                            ::last_name
                            ::user_name
                            ::password]))

(spec/def ::user-update-params (spec/keys
                                 :opt-un [::first_name
                                          ::last_name
                                          ::user_name
                                          ::password]))
