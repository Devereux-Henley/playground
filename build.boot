(set-env!
 :source-paths #{"sass" "src"}
 :resource-paths #{"resources"}
 :asset-paths #{"assets"}
 :dependencies
 '[[org.clojure/clojure "1.9.0-alpha14"]
   [org.clojure/clojurescript "1.9.494"]
   [adzerk/boot-cljs "1.7.228-1" :scope "test"]
   [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
   [adzerk/boot-reload "0.5.0" :scope "test"]
   [deraen/boot-sass "0.3.0" :scope "test"]
   [weasel "0.7.0" :scope "test"] ;; Websocket Server
   [reloaded.repl "0.2.3" :scope "test"]

   [org.clojure/tools.nrepl "0.2.12" :scope "test"]
   ;; Needed for start-repl in cljs repl
   [com.cemerick/piggieback "0.2.1" :scope "test"]

   [com.stuartsierra/component "0.3.2"]

   ;; Server deps
   [aero "1.1.2"]
   [aleph "0.4.3"]
   [bidi "2.0.16"]
   [buddy "1.3.0"]
   [clj-time "0.13.0"]
   [compassus "1.0.0-alpha2"]
   [com.layerware/hugsql "0.4.7"]
   [hiccup "1.0.5"]
   [kibu/pushy "0.3.7"]
   [metosin/ring-swagger "0.23.0"]
   [mysql/mysql-connector-java "6.0.6"]
   [org.danielsz/system "0.4.0"]
   [org.omcljs/om "1.0.0-alpha47"]
   [org.clojure/tools.namespace "0.3.0-alpha3"]
   [prismatic/schema "1.1.3"]
   [yada "1.2.1"]

   ;; App deps
   [cljs-ajax "0.5.8"]
   [com.cognitect/transit-clj "0.8.297"]

   ;; Logging
   [org.clojure/tools.logging "0.3.1"]
   [org.slf4j/jcl-over-slf4j "1.7.21"]
   [org.slf4j/jul-to-slf4j "1.7.21"]
   [org.slf4j/log4j-over-slf4j "1.7.21"]
   [ch.qos.logback/logback-classic "1.1.5" :exclusions [org.slf4j/slf4j-api]]])

(load-data-readers!)

(require
 '[adzerk.boot-cljs :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload :refer [reload]]
 '[deraen.boot-sass :refer [sass]]
 '[com.stuartsierra.component :as component]
 '[clojure.tools.namespace.repl]
 '[clojure.java.io :as io]
 '[playground.server.system :refer [new-system dev-system]]
 '[system.boot :refer [system run]])

(def repl-port 5600)
(def cljs-build-ids #{"home" "administration" "requirements"})

(task-options!
 repl {:client true
       :port repl-port})

(deftask deps [])

(deftask dev
  "This is the main development entry point."
  []
  (comp
   (watch)
   (sass :output-style :expanded)
   (reload :ids cljs-build-ids)
   (cljs-repl :nrepl-opts {:client false
                           :port repl-port}) ; this is also the server repl!
   (cljs
     :ids cljs-build-ids
     :optimizations :none
     :compiler-options {:parallel-build true})
   (system :sys #'dev-system :auto true :files ["server.clj"])
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
