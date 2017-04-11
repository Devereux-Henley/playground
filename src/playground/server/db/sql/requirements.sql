-- src/playground/server/db/sql/requirements.sql
-- Requirements

-- :name get-requirement-by-id :? :1
-- :doc Get a single requirement by its id.
select r.* from Requirements r
where r.ID = :id

-- :name get-requirements-by-project :? :*
-- :doc Get all requirements associated with a specific project.
select r.* from Requirements r
where r.ProjectID = :id

-- :name get-top-level-requirements-by-project :? :*
-- :doc Get all top level requirements in project.
select r.* from Requirements r
join RequirementsPaths rp
on (r.ID = rp.Ancestor)
where r.ProjectID = :id
and not exists (select 1 from RequirementsPaths rp
                where r.ID = rp.Descendant)

-- :name get-descendants-by-id :? :*
-- :doc Get all children of a specified requirement.
select r.* from Requirements r
join RequirementsPaths rp
on (r.ID = rp.Descendant)
where rp.Ancestor = :id

-- :name get-descendants-by-id-and-depth :? :*
-- :doc Get all children of a specified requirement at a specific depth.
select r.* from Requirements r
join RequirementsPaths rp
on (r.ID = rp.Descendant)
where rp.Ancestor = :id
and rp.Depth = :depth

-- :name get-ancestors-by-id :? :*
-- :doc Get all ancestors of a specified requirement.
select r.* from Requirements r
join RequirementsPaths rp
on (r.ID = rp.Ancestor)
where rp.Descendant = :id

-- :name insert-requirement :! :n
-- :doc Insert a single requirements.
insert into Requirements
values (:requirement-name, :requirement-description, :requirement-project)

-- :name insert-requirement-child :! :n
-- :doc Insert a child relation between two requirements.
insert into RequirementsPaths (Ancestor, Descendant, Depth)
       select Ancestor, :descendant-id, Depth+1 from RequirementsPaths
       where Descendant = :ancestor-id
       union all select :descendant-id, :descendant-id, 0

-- :name delete-requirement-child :! :n
-- :doc Delete child relationships to a requirement.
delete from RequirementsPaths
       where Descendant = :id

-- :name delete-requirement-child-subtree :! :n
-- :doc Delete relationship subtree from a given requirement.
delete from RequirementsPaths
where Descendant in
      (select Descendant from RequirementsPaths
       where ancestor = :id)
