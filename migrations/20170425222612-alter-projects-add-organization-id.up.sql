-- migration to be applied
ALTER TABLE projects
      ADD COLUMN organization_id INTEGER NOT NULL,
      ADD CONSTRAINT projects_organization_id_fkey FOREIGN KEY (organization_id) REFERENCES organizations(id);
