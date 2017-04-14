-- migration to be applied
CREATE TABLE user_groups(
id SERIAL PRIMARY KEY,
name varchar(100) NOT NULL,
project_id integer NOT NULL REFERENCES projects(id),
UNIQUE (name, project_id)
);
