-- src/playground/server/db/sql/team_members.sql
-- team-members

-- :name delete-team-member! :? :1
-- :doc Delete a single team member by unique id combination.
DELETE FROM team_members
WHERE user_id = :user-id
AND team_id = :team-id
