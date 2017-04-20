(ns playground.server.db.team-members
  (:require
   [hugsql.core :as hugsql]))

;; Loads function definitions from sql file.
(hugsql/def-db-fns "playground/server/db/sql/team_members.sql")
