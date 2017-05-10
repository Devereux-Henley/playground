-- migration to be applied
INSERT INTO requirement_edits (requirement_id, edit_type, name, description)
VALUES (1, 'create', 'Req1', 'The application should not be bad.'),
       (1, 'delete', 'Req1', 'The application should not be bad.'),
       (1, 'restore', 'Req1', 'The application should not be bad.'),
       (1, 'edit',   'Req1', 'The application SHALL not be bad!'),
       (1, 'edit',   'Req1', 'The application probably will not be bad.'),
       (2, 'create', 'Req2', 'The application should indeed be good.'),
       (3, 'create', 'Req3', 'The application shall not use Om.Next to preserve sanity.'),
       (4, 'create', 'Req4', 'The application shall use Yada for http / rest resources.'),
       (5, 'create', 'Req5', 'The application shall use Hugsql for database interactions.'),
       (6, 'create', 'Req6', 'The application shall use Hiccup for basic templating.'),
       (7, 'create', 'Req7', 'The application shall be full stack Clojure'),
       (8, 'create', 'Req8', 'The application shall not have deletable requirements.'),
       (8, 'delete', 'Req8', 'The application shall not have deletable requirements.');
