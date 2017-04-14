(ns playground.server.middleware.authorization
  (:require
   [aero.core :as aero]
   [buddy.sign.jwt :as jwt]
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(def secret
  (:sign-secret
   (aero/read-config (io/file "configuration/config.edn") {})))

(defmethod yada.security/verify :basic-auth
  [ctx scheme]
  (some->
    (get-in ctx [:request :headers "x-whoami"])
    (jwt/unsign secret)
    :claims
    edn/read-string))
