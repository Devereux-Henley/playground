(ns playground.server.db
  (:require
    [clojure.spec :as s]
    [com.stuartsierra.component :refer [Lifecycle]]
    [hugsql.core :as hugsql]))

(s/def :unq/classname string?)
(s/def :unq/subprotocol string?)
(s/def :unq/subname string?)
(s/def :unq/user string?)
(s/def :unq/password string?)
(s/def :unq/serverTimezone string?)
(s/def ::db (s/keys
              :req-un
              [:unq/classname :unq/subprotocol :unq/subname :unq/user :unq/password :unq/serverTimezone]))

(defrecord db
    [classname
     subprotocol
     subname
     user
     password
     serverTimezone]
  Lifecycle
  (start
    [component]
    component)
  (stop
    [component]
    component))

(defn create-db
  []
  (map->db {}))
  
