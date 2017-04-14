-- migration to be applied
CREATE TABLE team_projects(
team_id integer NOT NULL REFERENCES teams(id),
project_id integer NOT NULL REFERENCES projects(id),
PRIMARY KEY (team_id, project_id)
);
