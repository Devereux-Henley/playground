(ns playground.server.system
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [com.stuartsierra.component :refer [system-map system-using]]
    [playground.server.server :refer [new-server]]
    [playground.server.db :as db]))

(defn config
  [profile]
  (aero/read-config (io/file "configuration/config.edn") {:profile profile}))

(defn configure-components
  [system config]
  (merge-with merge system config))

(defn new-system-map
  [config]
  (system-map
    :server (new-server)
    :db (db/create-db)))

(defn new-dependency-map
  []
  {})

(defn new-system
  [profile]
  (let [config (config profile)]
    (-> 
      (new-system-map config)
      (configure-components config)
      (system-using (new-dependency-map)))))
