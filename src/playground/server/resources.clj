(ns playground.server.resources
  (:require
   [com.stuartsierra.component :refer [Lifecycle using]]
   [playground.server.api.roles :as roles]
   [playground.server.api.team-members :as team-members]
   [playground.server.api.user-group-relations :as user-group-relations]
   [playground.server.api.standard :refer [map->StandardRestResource map->PivotRestResource]]))

(defn new-team-member-resource
  [db-spec]
  (map->PivotRestResource {:db-spec db-spec
                           :table team-members/table
                           :record-spec ::team-members/team-member}))

(defn new-role-resource
  [db-spec]
  (map->StandardRestResource {:db-spec db-spec
                              :table roles/table
                              :record-spec ::roles/role
                              :update-spec ::roles/update-params}))

(defn new-user-group-relation-resource
  [db-spec]
  (map->PivotRestResource {:db-spec db-spec
                           :table user-group-relations/table
                           :record-spec ::user-group-relations/user-group-relation}))

(defn- get-full-resource-map
  [db-spec]
  {:roles (new-role-resource db-spec)
   :team-members (new-team-member-resource db-spec)
   :user-group-relations (new-user-group-relation-resource db-spec)})

(defrecord ResourceMap [db]
  Lifecycle
  (start
    [this]
    (merge this (get-full-resource-map db)))
  (stop
    [this]
    this))

(defn new-resource-map
  []
  (using
    (map->ResourceMap {})
    [:db]))
