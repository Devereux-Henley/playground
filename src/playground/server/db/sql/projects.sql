-- src/playground/server/db/sql/projects.sql
-- Projects

-- :name get-all-projects-in-organization
-- :doc Gets all projects that are in a specific organization
SELECT p.* FROM projects p
JOIN organizations o
     ON p.organization_id = o.id
WHERE o.id = :id

-- :name get-all-projects :? :n
-- :doc Get all projects.
SELECT p.* FROM projects p

-- :name get-project-by-id :? :1
-- :doc Get a single project by its id.
SELECT p.* FROM projects p
WHERE p.id = :id

-- :name insert-project! :! :1
-- :doc Insert a single project
INSERT INTO projects (name, description)
VALUES (:name, :description)

-- :name update-project-by-id! :! :1
-- :doc Update a single project by its id.
/* :require [clojure.string :as string]
[hugsql.parameters :refer [identifier-param-quote]] */
UPDATE projects p SET
/*~
(string/join ","
(for [[field _] (:project-updates params)]
(str (identifier-param-quote (name field) options)
" = :v:project-updates." (name field))))
~*/
WHERE id = :id

-- :name delete-project-by-id! :! :1
-- :doc Delete a single project by its id.
DELETE FROM projects
WHERE id = :id
