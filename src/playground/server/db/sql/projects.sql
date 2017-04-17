-- src/playground/server/db/sql/projects.sql
-- Projects

-- :name get-all-projects :? :n
-- :doc Get all projects.
SELECT p.* FROM projects p

-- :name get-project-by-id :? :1
-- :doc Get a single project by its id.
SELECT p.* FROM projects p
WHERE p.id = :id

-- :name insert-organization! :! :1
-- :doc Insert a single organization
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
