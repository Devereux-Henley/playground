-- migration to be applied
CREATE TABLE teams(
id SERIAL PRIMARY KEY,
organization_id integer NOT NULL REFERENCES organizations(id),
name varchar(100) NOT NULL
);
