-- src/playground/server/db/sql/users.sql
-- Users

-- :name get-hash :? :1
-- :doc Get password hash by user name.
SELECT password_hash FROM users
WHERE user_name = :user-name
