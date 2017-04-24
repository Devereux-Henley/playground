(ns playground.server.middleware.authorization
  (:require
   [aero.core :as aero]
   [buddy.sign.jwt :as jwt]
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(def secret
  (get-in
    (aero/read-config (io/file "configuration/config.edn") {})
    [:secrets :jwt-secret]))

(defmulti verify)

(defmethod verify :basic-auth
  [ctx scheme]
  (some->
    (get-in ctx [:request :headers "authorization"])
    (jwt/unsign secret)
    :claims
    edn/read-string))
