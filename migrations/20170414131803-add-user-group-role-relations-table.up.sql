-- migration to be applied
CREATE TABLE user_group_role_relations(
group_id integer NOT NULL REFERENCES user_groups(id) ON DELETE CASCADE,
role_id integer NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
UNIQUE (group_id, role_id)
);
