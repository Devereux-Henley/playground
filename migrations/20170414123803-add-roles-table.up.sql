-- migration to be applied
CREATE TABLE roles(
id SERIAL PRIMARY KEY,
token varchar(100) UNIQUE NOT NULL,
description varchar(100) NOT NULL,
default_value boolean NOT NULL
);
