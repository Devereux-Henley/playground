(ns playground.server.parsers.home
  (:require
   [om.next :as om]
   [playground.server.api.organizations :as organizations]
   [playground.server.api.protocols :refer [create-record
                                            read-record
                                            update-record
                                            delete-record
                                            list-record]]))

(defmulti read-home-data om/dispatch)

(defmethod read-home-data :default
  [_ k _]
  {:value {:error (str "No handler for key" k)}})

(defmethod read-home-data :page/title
  [_ _ _]
  {:value "Baz"})

(defmethod read-home-data :user/session
  [{:keys [user] :as env} _ _]
  (if user
    {:value {:user/username user}
     :user user}
    {:value nil}))

(defmethod read-home-data :organization/organization-list
  [{:keys [user resources] :as env} _ _]
  (if user
    (let [{:keys [organizations]} resources
          {:keys [db-spec]}       organizations]
      {:value (organizations/get-organizations-by-user-name
                db-spec
                user)})
    {:value nil}))

(defmulti mutate-home-data om/dispatch)

(defmethod mutate-home-data :default
  [_ _ _]
  {:value {:error "Cannot mutate this data."}})

(defonce home-parser
  (om/parser {:read read-home-data
              :mutate mutate-home-data}))
