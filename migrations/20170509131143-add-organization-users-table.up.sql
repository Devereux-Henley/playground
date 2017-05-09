-- migration to be applied
CREATE TABLE organization_users(
user_id integer NOT NULL REFERENCES users(id),
organization_id integer NOT NULL REFERENCES organizations(id),
UNIQUE (user_id, organization_id)
);
