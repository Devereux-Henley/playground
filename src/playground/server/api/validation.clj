(ns playground.server.api.validation
  (:require
   [clojure.spec :as spec]
   [playground.server.specs.standard :as standard]
   [playground.shared.logging :as log]))

;; Wrap reads and mutates

(defonce success
  {:success "true"})

(defn failure
  [failure-message]
  {:success "false"
   :error failure-message})

(defn read-call-wrapper
  [unsafe-call]
  (try
    (merge
      {:results (unsafe-call)}
      success)
    (catch Exception e (let [msg (failure (or (ex-data e) (.getMessage e)))]
                         (log/error e)
                         (log/error msg)
                         msg))))

(defn mutate-call-wrapper
  [unsafe-call]
  (try
    (do
      {:results (unsafe-call)}
      success)
    (catch Exception e (let [msg (failure (or (ex-data e) (.getMessage e)))]
                         (log/error e)
                         (log/error msg)
                         msg))))

(defn validate-single-id
  [db-call input-id]
  (if (spec/valid? ::standard/valid-id input-id)
    (db-call {:id input-id})
    (let [specific-issue (spec/explain-data ::standard/valid-id input-id)]
      (log/debug specific-issue)
      (throw (ex-info "Invalid input" specific-issue)))))

(defn validate-single-record
  [db-call spec-key record]
  (if (spec/valid? spec-key record)
    (db-call record)
    (let [specific-issue (spec/explain-data spec-key record)]
      (log/debug specific-issue)
      (throw (ex-info "Invalid input" specific-issue)))))

(defn assoc-table
  [table input-map]
  (assoc input-map :table table))

(defn assoc-inserts
  [input-map]
  {:inserts input-map})

(defn assoc-updates
  [input-map]
  (let [id (:id input-map)]
    {:id id
     :updates (dissoc input-map :id)}))
