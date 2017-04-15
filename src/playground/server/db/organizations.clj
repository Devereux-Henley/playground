(ns playground.server.db.organizations
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "playground/server/db/sql/organizations.sql")
