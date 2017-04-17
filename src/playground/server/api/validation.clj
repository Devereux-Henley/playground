(ns playground.server.api.validation
  (:require
   [clojure.spec :as spec]
   [playground.shared.logging :as log]))

;; Generic specs for validation.

(spec/def ::valid-id (spec/and integer?
                       #(> % 0)))

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
      (unsafe-call)
      success)
    (catch Exception e (let [msg (failure (or (ex-data e) (.getMessage e)))]
                         (log/error e)
                         (log/error msg)
                         msg))))

(defn validate-single-id
  [db-call input-id]
  (if (spec/valid? ::valid-id input-id)
    (db-call {:id input-id})
    (let [specific-issue (spec/explain-data ::valid-id input-id)]
      (log/debug specific-issue)
      (throw (ex-info "Invalid input" specific-issue)))))

(defn validate-single-record
  [db-call spec-key record]
  (if (spec/valid? spec-key record)
    (db-call record)
    (let [specific-issue (spec/explain-data spec-key record)]
      (log/debug specific-issue)
      (throw (ex-info "Invalid input" specific-issue)))))
