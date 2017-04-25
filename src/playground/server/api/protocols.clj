(ns playground.server.api.protocols)

(defprotocol Create (create-record [this record]))

(defprotocol Read (read-record [this record-id]))

(defprotocol Update (update-record [this record-id record]))

(defprotocol Delete (delete-record [this record-id] "nephew"))

(defprotocol List (list-record [this]))
