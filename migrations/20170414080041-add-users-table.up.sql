-- migration to be applied
CREATE TABLE users(
id SERIAL PRIMARY KEY,
first_name varchar(100) NOT NULL UNIQUE,
last_name varchar(100) NOT NULL,
user_name varchar(100) NOT NULL,
password_hash char(64) NOT NULL
);
