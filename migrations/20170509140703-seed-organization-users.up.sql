-- migration to be applied
INSERT INTO organization_users (user_id, organization_id)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (3, 3);
