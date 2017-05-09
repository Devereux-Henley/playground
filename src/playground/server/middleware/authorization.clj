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

(defn check-cookie
  [cookie secret]
  (try
    (some->
      cookie
      (jwt/unsign secret)
      :claims
      edn/read-string)
    (catch Exception e false)))

(defmethod security/verify :basic-auth
  [ctx scheme]
  (let [cookie (get-in ctx [:cookies "session"])]
    (check-cookie cookie secret)))
