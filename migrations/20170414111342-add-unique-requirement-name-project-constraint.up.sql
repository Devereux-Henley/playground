-- migration to be applied
ALTER TABLE requirements ADD CONSTRAINT unique_name_project UNIQUE (name, project_id);
