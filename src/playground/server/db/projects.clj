(ns playground.server.db.projects
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "playground/server/db/sql/projects.sql")
