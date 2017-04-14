-- migration to be applied
CREATE TABLE user_group_relations(
user_id integer NOT NULL REFERENCES users(id) ON DELETE CASCADE,
group_id integer NOT NULL REFERENCES user_groups(id) ON DELETE CASCADE,
UNIQUE (user_id, group_id)
);
