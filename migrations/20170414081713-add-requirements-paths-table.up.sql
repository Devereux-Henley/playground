-- migration to be applied
CREATE TABLE requirements_paths(
ancestor integer NOT NULL REFERENCES requirements(id),
descendant integer NOT NULL REFERENCES requirements(id),
depth integer NOT NULL,
PRIMARY KEY (ancestor, descendant, depth)
);
