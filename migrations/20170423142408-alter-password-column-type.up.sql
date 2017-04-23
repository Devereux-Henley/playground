-- migration to be applied
ALTER TABLE users
ALTER COLUMN password_hash TYPE char(98);
