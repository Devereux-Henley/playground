(ns playground.server.db.authorization
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "playground/server/db/sql/authorization.sql")
