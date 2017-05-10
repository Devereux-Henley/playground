(ns playground.server.resources
  (:require
   [com.stuartsierra.component :refer [Lifecycle using]]
   [playground.server.specs.organizations :as organizations]
   [playground.server.specs.organization-users :as organization-users]
   [playground.server.specs.projects :as projects]
   [playground.server.specs.requirements :as requirements]
   [playground.server.specs.roles :as roles]
   [playground.server.specs.team-members :as team-members]
   [playground.server.specs.team-projects :as team-projects]
   [playground.server.specs.user-groups :as user-groups]
   [playground.server.specs.user-group-relations :as user-group-relations]
   [playground.server.specs.user-group-role-relations :as user-group-role-relations]
   [playground.server.api.standard :refer [map->StandardRestResource map->PivotRestResource]]
   [playground.server.specs.users :as users]))

(defn new-organization-resource
  [db-spec]
  (assoc
    (map->StandardRestResource   {:db-spec db-spec
                                  :table   "organizations"
                                  :record-spec ::organizations/organization
                                  :update-spec ::organizations/update-params})
    :db-mappings {:name        :organizations/organization-name
                  :description :organizations/organization-description
                  :id          :organizations/organization-id}))

(defn new-requirement-resource
  [db-spec]
  (assoc
    (map->StandardRestResource {:db-spec db-spec
                                :table "requirements"
                                :record-spec ::requirements/requirement
                                :update-spec ::requirements/requirement})
    :db-mappings {:requirement_id :requirement-id
                  :name           :requirement-name
                  :description    :requirement-description
                  :edit_type      :edit-type}))

(defn new-project-resource
  [db-spec]
  (assoc
    (map->StandardRestResource {:db-spec db-spec
                                :table "projects"
                                :record-spec ::projects/project
                                :update-spec ::projects/update-params})
    :db-mappings {:id              :projects/project-id
                  :name            :projects/project-name
                  :description     :projects/project-description
                  :organization_id :organizations/organization-id}))

(defn new-team-projects-resource
  [db-spec]
  (map->PivotRestResource {:db-spec db-spec
                           :table "team_projects"
                           :record-spec ::team-projects/team-project}))

(defn new-organization-user-resource
  [db-spec]
  (map->PivotRestResource {:db-spec db-spec
                           :table "organization_users"
                           :record-spec ::organization-users/organization-user}))

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
  {:organizations (new-organization-resource db-spec)
   :organization-users (new-organization-user-resource db-spec)
   :projects (new-project-resource db-spec)
   :requirements (new-requirement-resource db-spec)
   :roles (new-role-resource db-spec)
   :team-members (new-team-member-resource db-spec)
   :team-projects (new-team-projects-resource db-spec)
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
