(ns playground.server.middleware.authorization
  (:require
   [aero.core :as aero]
   [buddy.sign.jwt :as jwt]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [yada.security :as security]))

(def secret
  (get-in
    (aero/read-config (io/file "configuration/config.edn") {})
    [:secrets :jwt-secret]))

(defmethod security/verify :basic-auth
  [ctx scheme]
  (try
    (some->
      (get-in ctx [:cookies "session"])
      (jwt/unsign secret)
      :claims
      edn/read-string)
    (catch Exception e false)))
