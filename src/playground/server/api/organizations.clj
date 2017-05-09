(ns playground.server.api.organizations
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec :as spec]
   [playground.server.api.validation :as validation :refer [read-call-wrapper
                                                            mutate-call-wrapper
                                                            validate-single-id
                                                            validate-single-record]]
   [playground.server.db.organizations :as db]
   [playground.server.specs.organizations :as specs]))

(defrecord Organization [name description])

(defn get-organizations-by-user-name
  [{:keys [db-spec]} user-name]
  (db/get-organizations-by-user-name
    db-spec
    {:user-name user-name}))
