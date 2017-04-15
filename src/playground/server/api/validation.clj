(ns playground.server.api.validation
  (:require
   [clojure.spec :as spec]))

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
    (catch Exception e (failure (or (ex-data e) (.getMessage e))))))

(defn mutate-call-wrapper
  [unsafe-call]
  (try
    (do
      (unsafe-call)
      success)
    (catch Exception e (failure (or (ex-data e) (.getMessage e))))))

(defn validate-single-id
  [db-call input-id]
  (if (spec/valid? ::valid-id input-id)
    (db-call {:id input-id})
    (throw (ex-info "Invalid input" (spec/explain-data ::valid-id input-id)))))

(defn validate-single-record
  [db-call spec-key record]
  (if (spec/valid? spec-key record)
    (db-call record)
    (throw (ex-info "Invalid input" (spec/explain-data spec-key record)))))
