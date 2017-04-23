-- rolling back recipe
ALTER TABLE users
ALTER COLUMN password_hash TYPE char(64);
