-- migration to be applied
CREATE TABLE organizations(
id SERIAL PRIMARY KEY,
name varchar(100) UNIQUE NOT NULL,
description varchar(1000) NOT NULL
);
