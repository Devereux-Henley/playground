(ns playground.server.specs.standard
  (:require
   [clojure.spec :as spec]))

;; Generic specs for validation.

(spec/def ::valid-id (spec/and integer?
                       #(> % 0)))

(spec/def ::standard-description (spec/and
                                   string?
                                   #(not (empty? %))))
