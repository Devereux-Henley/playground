-- migration to be applied
INSERT INTO requirements_paths (ancestor, descendant, depth)
VALUES (1, 1, 0),
       (2, 2, 0),
       (1, 2, 1),
       (3, 3, 0),
       (4, 4, 0),
       (4, 5, 1),
       (5, 5, 0),
       (5, 6, 1),
       (4, 6, 2),
       (6, 6, 0),
       (7, 7, 0),
       (8, 8, 0);
