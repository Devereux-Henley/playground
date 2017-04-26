-- src/playground/server/db/sql/standard.sql
-- Standard CRUD

-- :name get-all :? :*
-- :doc Get all fields from a specified table.
SELECT * FROM :i:table

-- :name get-by-id :? :1
-- :doc Get a single record by its id.
SELECT * FROM :i:table
WHERE id = :id

-- :name insert! :! :1
-- :doc Insert a single record
/* :require [clojure.string :as string]
[hugsql.parameters :refer [identifier-param-quote]] */
INSERT INTO :i:table
/*~
(str " ("
(string/join ","
(for [[field _] (:inserts params)]
(str (identifier-param-quote (string/replace (name field) #"-" "_") options))))
") "
"VALUES "
"("
(string/join ","
(for [[field _] (:inserts params)]
(str ":v:inserts." (name field))))
")")
~*/

-- :name update-by-id! :! :1
-- :doc Update a single record by its id.
/* :require [clojure.string :as string]
[hugsql.parameters :refer [identifier-param-quote]] */
UPDATE :i:table SET
/*~
(string/join ","
(for [[field _] (:updates params)]
(str (identifier-param-quote (string/replace (name field) #"-" "_") options)
" = :v:updates." (name field))))
~*/
WHERE id = :id

-- :name delete-by-id! :! :1
-- :doc Delete a single record by its id.
DELETE FROM :i:table
WHERE id = :id

-- :name delete-by-pivot! :! :1
-- :doc Delete a single record from a pivot table.
/* :require [clojure.string :as string]
[hugsql.parameters :refer [identifier-param-quote]] */
DELETE FROM :i:table WHERE
/*~
(string/join " AND "
(for [[field _] (:deletes params)]
(str (identifier-param-quote (string/replace (name field) #"-" "_") options)
" = :v:deletes." (name field))))
~*/
