-- src/playground/server/db/sql/requirements.sql
-- requirements

-- :name get-requirement-by-id :? :1
-- :doc Get a single requirement by its id.
SELECT re.* FROM requirement_edits re
WHERE re.requirement_id = :id
AND re.date_created = (SELECT MAX(date_created)
                       FROM requirement_edits
                       WHERE requirement_id = :id)

-- :name get-requirement-history-by-id :? :1
-- :doc Get the entire history of a single requirement
SELECT re.* from requirement_edits re
WHERE re.requirement_id = :id
ORDER BY re.date_created ASC

-- :name get-requirements-by-project :? :*
-- :doc Get all requirements associated with a specific project.
SELECT DISTINCT ON (re.requirement_id) re.* FROM requirement_edits re
JOIN requirements r
     ON re.requirement_id = r.id
WHERE r.project_id = :id
AND re.date_created = (SELECT MAX(date_created)
                       FROM requirement_edits
                       WHERE requirement_id = :id)
ORDER BY re.requirement_id ASC

-- :name get-top-level-requirements-by-project :? :*
-- :doc Get all top level requirements in project.
SELECT DISTINCT ON (re.requirement_id) re.* FROM requirement_edits re
JOIN requirements r
     ON re.requirement_id = r.id
JOIN requirements_paths rp
     ON r.id = rp.ancestor
WHERE r.project_id = :id
AND re.date_created = (SELECT MAX(date_created)
                       FROM requirement_edits
                       WHERE r.project_id = :id)
AND NOT EXISTS (SELECT * FROM requirements_paths rp
                WHERE r.id = rp.descendant
                AND r.id != rp.ancestor)
ORDER BY re.requirement_id ASC

-- :name get-top-level-requirements-in-project-ids :? :*
-- :doc Get all top level requirements in a group of projects.
SELECT DISTINCT ON (re.requirement_id) re.* FROM requirement_edits re
JOIN requirements r
     ON re.requirement_id = r.id
JOIN requirements_paths rp
     ON r.id = rp.ancestor
WHERE r.project_id IN ( :v*:project-ids )
      AND NOT EXISTS (SELECT * FROM requirements_paths rp
                      WHERE r.id = rp.descendant
                      AND r.id != rp.ancestor)
ORDER BY re.requirement_id ASC

-- :name get-descendants-by-id :? :*
-- :doc Get all children of a specified requirement.
SELECT DISTINCT ON (re.requirement_id) re.* FROM requirement_edits re
JOIN requirements_paths rp
ON (re.requirement_id = rp.descendant)
WHERE rp.ancestor = :id
AND re.date_created = (SELECT MAX(date_created)
                       FROM requirement_edits
                       WHERE requirement_id = :id)
ORDER BY re.requirement_id ASC

-- :name get-descendants-by-id-and-depth :? :*
-- :doc Get all children of a specified requirement at a specific depth.
SELECT DISTINCT ON (re.requirement_id) re.* FROM requirement_edits re
JOIN requirements_paths rp
ON (re.requirement_id = rp.descendant)
WHERE rp.ancestor = :id
AND re.date_created = (SELECT MAX(date_created)
                       FROM requirement_edits
                       WHERE requirement_id = :id)
AND rp.depth = :depth
ORDER BY re.requirement_id ASC

-- :name get-ancestors-by-id :? :*
-- :doc Get all ancestors of a specified requirement.
SELECT DISTINCT ON (re.requirement_id) re.* FROM requirement_edits re
JOIN requirements_paths rp
ON (re.requirement_id = rp.ancestor)
WHERE rp.descendant = :id
AND re.date_created = (SELECT MAX(date_created)
                       FROM requirement_edits
                       WHERE requirement_id = :id)
ORDER BY re.requirement_id ASC

-- :name insert-requirement! :i!
-- :doc Insert a single requirement.
INSERT INTO requirements (project_id)
VALUES (:requirement-project)

-- :name insert-requirement-creation! :i!
-- :doc Insert a requirement_edit on creation of a requirement record
INSERT INTO requirement_edits (requirement_id, edit_type, name, description)
VALUES (:requirement-id, 'create', :requirement-name, :requirement-description)

-- :name insert-requirement-deletion! :i!
-- :doc Insert a requirement_edit on attempted deletion of a requirement record.
INSERT INTO requirement_edits (requirement_id, edit_type, name, description)
VALUES (:requirement-id, 'delete', :requirement-name, :requirement-description)

-- :name insert-requirement-restore! :i!
-- :doc Insert a requirement_edit on restoration of a requirement record.
INSERT INTO requirement_edits (requirement_id, edit_type, name, description)
VALUES (:requirement-id, 'restore', :requirement-name, :requirement-description)

-- :name insert-requirement-edit! :i!
-- :doc Insert a requirement_edit on edit of a requirement record.
INSERT INTO requirement_edits (requirement_id, edit_type, name, description)
VALUES (:requirement-id, 'edit', :requirement-name, :requirement-description)

-- :name insert-requirement-child! :! :n
-- :doc Insert a child relation between two requirements.
INSERT INTO requirements_paths (ancestor, descendant, depth)
       SELECT ancestor, :descendant-id, depth+1 FROM requirements_paths
       WHERE descendant = :ancestor-id
       union all SELECT :descendant-id, :descendant-id, 0

-- :name insert-new-relation! :! :n
-- :doc Insert a brand new top level relation.
INSERT INTO requirements_paths (ancestor, descendant, depth)
VALUES (:id, :id, 0)

-- :name delete-requirement-child! :! :n
-- :doc Delete child relationships to a requirement.
DELETE FROM requirements_paths
       WHERE descendant = :id

-- :name delete-requirement-child-subtree! :! :n
-- :doc Delete relationship subtree FROM a given requirement.
DELETE FROM requirements_paths
WHERE descendant IN
      (SELECT descendant FROM requirements_paths
       WHERE ancestor = :id)

-- :name delete-requirement-relationships! :! :n
-- :doc Delete all relationships to a given requirement.
DELETE FROM requirements_paths
WHERE descendant = :id
OR descendant IN
   (SELECT descendant FROM requirements_paths
    WHERE ancestor = :id)
