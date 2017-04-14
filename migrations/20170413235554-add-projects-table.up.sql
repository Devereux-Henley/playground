-- migration to be applied
CREATE TABLE projects(
id SERIAL PRIMARY KEY,
name varchar(100) NOT NULL,
description varchar(1000) NOT NULL
);
