(ns playground.server.users
  (:require
   [playground.server.api.users :as api :refer [map->User]]
   [playground.server.constants :refer [standard-inputs standard-outputs]]
   [schema.core :as schema :refer [defschema]]
   [yada.yada :as yada]))

(defschema User
  {:user-name String
   :first-name String
   :last-name String
   :password String})

(defn new-user-base-resource
  [db-spec]
  (yada/resource
    {:id :playground.resources/users-base
     :description "Serves LIST and CREATE capabilities for users"
     :produces [{:media-type standard-outputs
                 :charset "UTF-8"}]
     :methods
     {:get {:produces standard-outputs
            :swagger/tags ["users" "list"]
            :response (fn [ctx] (api/get-all-users db-spec))}
      :put {:parameters {:body User}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["users" "create"]
            :response (fn [ctx]
                        (api/insert-user! db-spec (map->User
                                                    (get-in ctx [:parameters :body]))))}}}))

(defn new-user-target-resource
  [db-spec]
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
                        (api/get-user-by-id db-spec
                          (get-in ctx [:parameters :path :user-id])))}
      :put {:parameters {:body User}
            :consumes standard-inputs
            :produces standard-outputs
            :swagger/tags ["users" "update"]
            :response (fn [ctx]
                        (api/update-user-by-id!
                          db-spec
                          (get-in ctx [:parameters :path :user-id])
                          (map->User (get-in ctx [:parameters :body]))))}
      :delete {:consumes standard-inputs
               :produces standard-outputs
               :swagger/tags ["users" "delete"]
               :response (fn [ctx]
                           (api/delete-user-by-id!
                             db-spec
                             (get-in ctx [:parameters :path :user-id])))}}}))

(defn user-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/users"
                    [
                     ["" (new-user-base-resource db-spec)]
                     ["/" (yada/redirect :playground.resources/users-base)]
                     [["/" [#"\d+" :user-id]] (new-user-target-resource db-spec)]
                     ]]]
    api-routes
    ))
