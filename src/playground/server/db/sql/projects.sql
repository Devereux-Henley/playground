-- src/playground/server/db/sql/projects.sql
-- Projects

-- :name insert-project :! :n
-- :doc Insert a project into the table
insert into Projects (ID, Name, Description)
values (:id, :name, :description)

-- :name insert-projects :! :n
-- :doc Insert multiple projects into the table
insert into Projects (ID, Name, Description)
values :tuple*:projects

-- :name get-project-by-id :? :1
-- :doc Get projects by id
select ID, Name, Description from Projects
where ID = :id

-- :name get-projects :? :*
-- :doc Get all projects
select ID, Name, Description from Projects;

-- :name projects-by-ids-specify-cols :? :*
-- :doc Projects with returned columns specified
select :i*:cols from  Projects
where ID in (:v*:ids)
