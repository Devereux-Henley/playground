(ns playground.server.specs.users
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]))

(spec/def ::id #(spec/valid? ::standard/valid-id %))

(spec/def ::user-name (spec/and
                        string?
                        #(not (empty? %))
                        #(re-matches #"^[a-zA-Z0-9]*$" %)))

(spec/def ::first-name (spec/and
                         string?
                         #(not (empty? %))
                         #(re-matches #"^[a-zA-Z]*$" %)))

(spec/def ::last-name (spec/and
                        string?
                        #(not (empty? %))
                        #(re-matches #"^[a-zA-Z]*$" %)))

(spec/def ::password (spec/and
                       string?
                       #(< 8 (count %))
                       #(re-matches #"^(?!.*([A-Za-z0-9])\1{2})(?=.*[a-z])(?=.*\d)[A-Za-z0-9]{8,16}$" %)))

(spec/def ::user (spec/keys
                   :req-un [::first-name
                            ::last-name
                            ::user-name
                            ::password]))

(spec/def ::user-auth (spec/keys
                        :req-un [::user-name]))

(spec/def ::updates (spec/keys
                      :opt-un [::first-name
                               ::last-name
                               ::user-name
                               ::password]))

(spec/def ::update-params (spec/keys
                            :req-un [::id
                                     ::updates]))
