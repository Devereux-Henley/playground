(ns playground.server.db.users
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "playground/server/db/sql/users.sql")
