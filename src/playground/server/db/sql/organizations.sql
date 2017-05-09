-- src/playground/server/db/sql/organizations.sql
-- organizations

-- :name get-organizations-by-user-name :? :*
-- :doc Get all organizations associated with a specific user.
SELECT o.* FROM organizations o
JOIN organization_users ou
     ON o.id = ou.organization_id
JOIN users u
     ON ou.user_id = u.id
WHERE u.user_name = :user-name

-- :name get-all-organizations :? :*
-- :doc Get all organizations.
SELECT o.* FROM organizations o

-- :name get-organization-by-id :? :1
-- :doc Get a single organization by its id.
SELECT o.* FROM organizations o
WHERE o.id = :id

-- :name insert-organization! :! :1
-- :doc Insert a single organization
INSERT INTO organizations (name, description)
VALUES (:name, :description)

-- :name update-organization-by-id! :! :1
-- :doc Update a single organization by its id.
/* :require [clojure.string :as string]
[hugsql.parameters :refer [identifier-param-quote]] */
UPDATE organizations o SET
/*~
(string/join ","
(for [[field _] (:organization-updates params)]
(str (identifier-param-quote (name field) options)
" = :v:organization-updates." (name field))))
~*/
WHERE id = :id

-- :name delete-organization-by-id! :! :1
-- :doc Delete a single organization by its id.
DELETE FROM organizations
WHERE id = :id
