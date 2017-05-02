-- migration to be applied
CREATE TYPE requirement_edit AS ENUM ('create', 'edit', 'restore', 'delete');
CREATE TABLE requirement_edits(
id SERIAL PRIMARY KEY,
requirement_id integer NOT NULL REFERENCES requirements(id),
edit_type requirement_edit NOT NULL,
name varchar(100) NOT NULL default '',
description varchar(1000) NOT NULL default '',
date_created timestamp default current_timestamp
);
