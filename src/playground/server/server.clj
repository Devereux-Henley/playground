(ns playground.server.server
  (:require
    [bidi.vhosts :refer [vhosts-model]]
    [clojure.spec :as s]
    [clojure.tools.logging :refer :all]
    [com.stuartsierra.component :refer [Lifecycle using]]
    [playground.server.db :as db]
    [playground.server.routing :refer [routes]]
    [yada.yada :as yada]))

(s/def :unq/port integer?)
(s/def :unq/listener (s/keys :req-un [:unq/port :unq/close :unq/server]))
(s/def ::server (s/keys :req [:playground.server.db/db]
                           :req-un [:unq/port :unq/listener]))

(defrecord Server
  [port
   db
   listener]
  Lifecycle
  (start
    [component]
    (if
      listener
      component
      (let
        [vhosts-model (vhosts-model
                        [{:scheme :http :host (format "localhost:%d" port)}
                         (routes db {:port port})])
         listener (yada/listener vhosts-model {:port port})]
        (infof "Started server on port %s" (:port listener))
        (assoc component :listener listener))))
  (stop
    [component]
    (when-let
      [close (get-in component [:listener :close])]
      (close))
    (assoc component :listener nil)))

(defn new-server
  []
  (using
    (map->Server {})
    [:db]))

