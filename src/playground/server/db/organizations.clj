(ns playground.server.db.organizations
  (:require
   [hugsql.core :as hugsql]))

;; Loads function definitions from sql file.
(hugsql/def-db-fns "playground/server/db/sql/organizations.sql")
