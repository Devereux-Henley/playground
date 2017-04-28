-- src/playground/server/db/sql/authorization.sql
-- auth

-- :name get-roles-by-user-id :? :*
-- :doc Get all roles associated to a user
SELECT roles.* FROM users
JOIN user_group_relations ON users.id = user_group_relations.user_id
JOIN user_groups ON user_group_relations.group_id = user_groups.id
JOIN user_group_role_relations ON user_groups.id = user_group_role_relations.group_id
JOIN roles ON user_group_role_relations.role_id = roles.id
WHERE users.id = :id

-- :name get-hash :? :1
-- :doc Get password hash by user name.
SELECT id, password_hash FROM users
WHERE user_name = :user-name
