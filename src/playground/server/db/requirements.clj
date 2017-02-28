(ns playground.server.db.requirements
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "playground/server/db/sql/requirements.sql")
