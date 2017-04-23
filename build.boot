(set-env!
  :source-paths #{"sass" "src"}
  :resource-paths #{"resources"}
  :asset-paths #{"assets"}
  :dependencies
  '[[org.clojure/clojure "1.9.0-alpha14"]
    [org.clojure/clojurescript "1.9.494"]
    [adzerk/boot-cljs "1.7.228-1" :scope "test"]
    [adzerk/boot-reload "0.5.0" :scope "test"]
    [deraen/boot-sass "0.3.0" :scope "test"]
    [mbuczko/boot-ragtime "0.2.0" :scope "test"]
    [ragtime "0.7.1" :scope "test"]
    [reloaded.repl "0.2.3" :scope "test"]

    [org.clojure/tools.nrepl "0.2.12" :scope "test"]

    [com.stuartsierra/component "0.3.2"]

    ;; Server deps
    [aero "1.1.2"]
    [aleph "0.4.3"]
    [bidi "2.0.16"]
    [binaryage/dirac "1.2.3" :scope "test"]
    [binaryage/devtools "0.9.2" :scope "test"]
    [buddy "1.3.0"]
    [clj-time "0.13.0"]
    [compassus "1.0.0-alpha2"]
    [com.layerware/hugsql "0.4.7"]
    [hiccup "1.0.5"]
    [kibu/pushy "0.3.7"]
    [metosin/ring-swagger "0.23.0"]
    [org.postgresql/postgresql "42.0.0"]
    [org.danielsz/system "0.4.0"]
    [org.omcljs/om "1.0.0-alpha47"]
    [org.clojure/tools.namespace "0.3.0-alpha3"]
    [powerlaces/boot-cljs-devtools "0.2.0" :scope "test"]
    [prismatic/schema "1.1.3"]
    [yada "1.2.1"]

    ;; App deps
    [cljs-ajax "0.5.8"]
    [com.cognitect/transit-clj "0.8.297"]

    ;; Logging
    [adzerk/boot-logservice "1.2.0"]
    [org.clojure/tools.logging "0.3.1"]
    [org.slf4j/jcl-over-slf4j "1.7.21"]
    [org.slf4j/jul-to-slf4j "1.7.21"]
    [org.slf4j/log4j-over-slf4j "1.7.21"]
    [ch.qos.logback/logback-classic "1.1.5" :exclusions [org.slf4j/slf4j-api]]])

(load-data-readers!)

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-logservice :as log-service]
  '[adzerk.boot-reload :refer [reload]]
  '[aero.core :refer [read-config]]
  '[deraen.boot-sass :refer [sass]]
  '[com.stuartsierra.component :as component]
  '[clojure.string :refer [join]]
  '[clojure.tools.namespace.repl]
  '[clojure.java.io :as io]
  '[clojure.tools.logging :as log]
  '[mbuczko.boot-ragtime :refer [ragtime]]
  '[playground.server.system :refer [new-system dev-system]]
  '[powerlaces.boot-cljs-devtools :refer [cljs-devtools dirac]]
  '[ragtime.jdbc]
  '[ragtime.repl]
  '[system.boot :refer [system run]])

;; Logging configuration
(defonce log-config
  [:configuration {:scan true, :scanPeriod "10 seconds"}
   [:appender {:name "FILE" :class "ch.qos.logback.core.rolling.RollingFileAppender"}
    [:encoder [:pattern "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"]]
    [:rollingPolicy {:class "ch.qos.logback.core.rolling.TimeBasedRollingPolicy"}
     [:fileNamePattern "logs/%d{yyyy-MM-dd}.%i.log"]
     [:timeBasedFileNamingAndTriggeringPolicy {:class "ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP"}
      [:maxFileSize "64 MB"]]]
    [:prudent true]]
   [:appender {:name "STDOUT" :class "ch.qos.logback.core.ConsoleAppender"}
    [:encoder [:pattern "%-5level %logger{36} - %msg%n"]]
    [:filter {:class "ch.qos.logback.classic.filter.ThresholdFilter"}
     [:level "INFO"]]]
   [:root {:level "INFO"}
    [:appender-ref {:ref "FILE"}]
    [:appender-ref {:ref "STDOUT"}]]
   [:logger {:name "user" :level "ALL"}]
   [:logger {:name "boot.user" :level "ALL"}]])

(alter-var-root #'log/*logger-factory* (constantly (log-service/make-factory log-config)))

;; Task defaults

(def repl-port 5600)
(def cljs-build-ids #{"home" "administration" "requirements"})
(def migration-directory "migrations")
(def database-string (let [{:keys [db]}
                           (read-config (io/file "configuration/config.edn") {:profile :dev})
                           {:keys [subprotocol subname serverTimezone user password]} db]
                       (str
                         (join ":"
                           ["jdbc" subprotocol subname])
                         "?"
                         (join "&"
                           (map (fn [[name value]]
                                  (str name "=" value))
                             [["serverTimezone" serverTimezone]
                              ["user" user]
                              ["password" password]])))))

(task-options!
  repl {:client false
        :port repl-port}
  ragtime {:database database-string})

(deftask deps [])

(deftask remigrate
  "Resets state of development database"
  []
  (let [migrations (ragtime.jdbc/load-directory migration-directory)
        config {:datastore (ragtime.jdbc/sql-database database-string)
                :migrations migrations}]
    (do
      (ragtime.repl/rollback
        config
        (count migrations))
      (ragtime.repl/migrate
        config))))

(deftask dev
  "This is the main development entry point."
  []
  (clojure.tools.namespace.repl/set-refresh-dirs "src/playground/server" "src/playground/shared")

  (comp
    (watch)
    (sass :output-style :expanded)
    (system :sys #'dev-system :auto true :files ["server.clj"])
    (cljs-devtools :ids cljs-build-ids)
    (dirac :ids cljs-build-ids :nrepl-opts {:client false :port repl-port})
    (reload :ids cljs-build-ids)
    (cljs
      :ids cljs-build-ids
      :optimizations :none
      :compiler-options {:parallel-build true})
    (target :dir #{"static"})))

(deftask build
  "This is used for creating optimized static resources under static"
  []
  (comp
    (sass :output-style :compressed)
    (cljs :ids cljs-build-ids :optimizations :advanced)
    (target :dir #{"static"})))

(defn- run-system [profile]
  (println "Running system with profile" profile)
  (let [system (new-system profile)]
    (component/start system)
    (intern 'user 'system system)
    (with-pre-wrap fileset
      (assoc fileset :system system))))

(deftask run! [p profile VAL kw "Profile"]
  (comp
    (repl :server true
      :port (case profile :prod 5601 :beta 5602 5600)
      :init-ns 'user)
    (run-system (or profile :prod))
    (wait)))
