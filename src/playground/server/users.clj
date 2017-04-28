(ns playground.server.users
  (:require
   [playground.server.api.users :as api :refer [map->User]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [playground.server.api.protocols :refer [list-record
                                            read-record
                                            create-record
                                            update-record
                                            delete-record]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema User
  {:user-name String
   :first-name String
   :last-name String
   :password String})

(defn new-user-base-resource
  [users-resource]
  (yada/resource
    {:id :playground.resources/users-base
     :description "Serves LIST and CREATE capabilities for users"
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["users" "list"]
            :response (fn [ctx] (api/get-all-users users-resource))}
      :put {:parameters {:body User}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["users" "create"]
            :response (fn [ctx]
                        (api/insert-user! users-resource (map->User
                                                    (get-in ctx [:parameters :body]))))}}}))

(defn new-user-target-resource
  [users-resource]
  (yada/resource
    {:id :playground.resources/users-target
     :description "Serves READ, UPDATE, DELETE capabilities for users"
     :parameters {:path {:user-id Long}}
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["users" "read"]
            :response (fn [ctx]
                        (api/get-user-by-id users-resource
                          (get-in ctx [:parameters :path :user-id])))}
      :put {:parameters {:body User}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["users" "update"]
            :response (fn [ctx]
                        (api/update-user-by-id!
                          users-resource
                          (get-in ctx [:parameters :path :user-id])
                          (map->User (get-in ctx [:parameters :body]))))}
      :delete {:consumes standard-inputs
               :produces standard-outputs
               :swagger/tags ["users" "delete"]
               :response (fn [ctx]
                           (delete-record
                             users-resource
                             (get-in ctx [:parameters :path :user-id])))}}}))

(defn user-api-routes
  [users-resource {:keys [port]}]
  (let [api-routes ["/users"
                    [
                     ["" (new-user-base-resource users-resource)]
                     ["/" (yada/redirect :playground.resources/users-base)]
                     [["/" [#"\d+" :user-id]] (new-user-target-resource users-resource)]
                     ]]]
    api-routes
    ))
