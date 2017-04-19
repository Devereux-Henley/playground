(ns playground.server.db.standard
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "playground/server/db/sql/standard.sql")
