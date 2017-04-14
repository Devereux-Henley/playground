-- migration to be applied
CREATE TABLE team_members(
team_id integer NOT NULL REFERENCES teams(id) ON DELETE CASCADE ON UPDATE CASCADE,
user_id integer NOT NULL REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
PRIMARY KEY (team_id, user_id)
);
