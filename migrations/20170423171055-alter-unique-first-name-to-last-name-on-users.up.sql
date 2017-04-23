-- migration to be applied
ALTER TABLE users
      DROP CONSTRAINT users_first_name_key,
      ADD CONSTRAINT users_user_name_key UNIQUE(user_name);
