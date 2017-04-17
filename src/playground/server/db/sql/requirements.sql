-- src/playground/server/db/sql/requirements.sql
-- requirements

-- :name get-requirement-by-id :? :1
-- :doc Get a single requirement by its id.
SELECT r.* FROM requirements r
WHERE r.id = :id

-- :name get-requirements-by-project :? :*
-- :doc Get all requirements associated with a specific project.
SELECT r.* FROM requirements r
WHERE r.project_id = :id

-- :name get-top-level-requirements-by-project :? :*
-- :doc Get all top level requirements in project.
SELECT r.* FROM requirements r
JOIN requirements_paths rp
ON (r.id = rp.ancestor)
WHERE r.project_id = :id
and not exists (SELECT 1 FROM requirements_paths rp
                WHERE r.id = rp.descendant)

-- :name get-descendants-by-id :? :*
-- :doc Get all children of a specified requirement.
SELECT r.* FROM requirements r
JOIN requirements_paths rp
ON (r.id = rp.descendant)
WHERE rp.ancestor = :id

-- :name get-descendants-by-id-and-depth :? :*
-- :doc Get all children of a specified requirement at a specific depth.
SELECT r.* FROM requirements r
JOIN requirements_paths rp
ON (r.id = rp.descendant)
WHERE rp.ancestor = :id
and rp.depth = :depth

-- :name get-ancestors-by-id :? :*
-- :doc Get all ancestors of a specified requirement.
SELECT r.* FROM requirements r
JOIN requirements_paths rp
ON (r.id = rp.ancestor)
WHERE rp.descendant = :id

-- :name insert-requirement! :i!
-- :doc Insert a single requirement.
INSERT INTO requirements (name, description, project_id)
VALUES (:requirement-name, :requirement-description, :requirement-project)

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

-- :name update-requirement! :! :n
-- :doc Update a single requirement.
/* :require [clojure.string :as string]
[hugsql.parameters :refer [identifier-param-quote]] */
UPDATE requirements SET
/*~
(string/join ","
(for [[field _] (:requirement-updates params)]
(str (identifier-param-quote (name field) options)
" = :v:requirement-updates." (name field))))
~*/
WHERE id = :requirement-id

-- :name delete-requirement-by-id! :! :n
-- :doc Delete a single requirement by it's id.
DELETE FROM requirements
WHERE id = :id

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
