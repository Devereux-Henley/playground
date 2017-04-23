-- rolling back recipe
ALTER TABLE users
      DROP CONSTRAINT users_user_name_key;
