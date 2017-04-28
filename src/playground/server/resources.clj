(ns playground.server.resources
  (:require
   [com.stuartsierra.component :refer [Lifecycle using]]
   [playground.server.specs.projects :as projects]
   [playground.server.specs.roles :as roles]
   [playground.server.specs.team-members :as team-members]
   [playground.server.specs.user-groups :as user-groups]
   [playground.server.specs.user-group-relations :as user-group-relations]
   [playground.server.specs.user-group-role-relations :as user-group-role-relations]
   [playground.server.api.standard :refer [map->StandardRestResource map->PivotRestResource]]
   [playground.server.specs.users :as users]))

(defn new-project-resource
  [db-spec]
  (map->StandardRestResource {:db-spec db-spec
                              :table "projects"
                              :record-spec ::projects/project
                              :update-spec ::projects/update-params}))

(defn new-team-member-resource
  [db-spec]
  (map->PivotRestResource {:db-spec db-spec
                           :table "team_members"
                           :record-spec ::team-members/team-member}))

(defn new-role-resource
  [db-spec]
  (map->StandardRestResource {:db-spec db-spec
                              :table "roles"
                              :record-spec ::roles/role
                              :update-spec ::roles/update-params}))

(defn new-user-resource
  [db-spec]
  (map->StandardRestResource {:db-spec db-spec
                              :table "users"
                              :record-spec ::users/user
                              :update-spec ::users/update-params}))

(defn new-user-group-resource
  [db-spec]
  (map->StandardRestResource {:db-spec db-spec
                              :table "user_groups"
                              :record-spec ::user-groups/user-group
                              :update-spec ::user-groups/update-params}))

(defn new-user-group-relation-resource
  [db-spec]
  (map->PivotRestResource {:db-spec db-spec
                           :table "user_group_relations"
                           :record-spec ::user-group-relations/user-group-relation}))

(defn new-user-group-role-relation-resource
  [db-spec]
  (map->PivotRestResource {:db-spec db-spec
                           :table "user_group_role_relations"
                           :record-spec ::user-group-role-relations/user-group-role-relation}))

(defn- get-full-resource-map
  [db-spec]
  {:projects (new-project-resource db-spec)
   :roles (new-role-resource db-spec)
   :team-members (new-team-member-resource db-spec)
   :users (new-user-resource db-spec)
   :user-groups (new-user-group-resource db-spec)
   :user-group-relations (new-user-group-relation-resource db-spec)
   :user-group-role-relations (new-user-group-role-relation-resource db-spec)})

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
