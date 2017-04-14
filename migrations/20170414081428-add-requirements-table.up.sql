-- migration to be applied
CREATE TABLE requirements(
id SERIAL PRIMARY KEY,
name varchar(100) NOT NULL,
description varchar(1000) NOT NULL,
project_id integer NOT NULL REFERENCES projects(id)
);
