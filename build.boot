(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :asset-paths #{"assets"}
 :dependencies
 '[[org.clojure/clojure "1.9.0-alpha14"]
   [org.clojure/clojurescript "1.9.494"]
   [adzerk/boot-cljs "1.7.228-1" :scope "test"]
   [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
   [adzerk/boot-reload "0.5.0" :scope "test"]
   [weasel "0.7.0" :scope "test"] ;; Websocket Server
   [reloaded.repl "0.2.3" :scope "test"]

   [org.clojure/tools.nrepl "0.2.12" :scope "test"]
   ;; Needed for start-repl in cljs repl
   [com.cemerick/piggieback "0.2.1" :scope "test"]

   [com.stuartsierra/component "0.3.2"]

   ;; Server deps
   [aero "1.0.3"]
   [aleph "0.4.2-alpha10"]
   [bidi "2.0.16"]
   [com.layerware/hugsql "0.4.7"]
   [hiccup "1.0.5"]
   [metosin/ring-swagger "0.22.12"]
   [mysql/mysql-connector-java "6.0.6"]
   [org.omcljs/om "1.0.0-alpha48"]
   [prismatic/schema "1.1.3"]
   [yada "1.2.1"]

   ;; App deps
   [cljs-ajax "0.5.8"]
   [com.cognitect/transit-clj "0.8.297"]

   ;; Logging
   [org.clojure/tools.logging "0.3.1"]])

(load-data-readers!)

(require
 '[adzerk.boot-cljs :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload :refer [reload]]
 '[com.stuartsierra.component :as component]
 '[clojure.tools.namespace.repl]
 '[playground.server.system :refer [new-system]])

(def repl-port 5600)
(def cljs-build-ids #{"home" "administration" "requirements"})

(task-options!
 repl {:client true
       :port repl-port})

(deftask dev-system
  "Develop the server backend. The system is automatically started in
  the dev profile."
  []
  (with-pass-thru _
    (require 'reloaded.repl)
    (let [go (resolve 'reloaded.repl/go)]
      (try
        (require 'user)
        (go)
        (catch Exception e
          (boot.util/fail "Exception while starting the system\n")
          (boot.util/print-ex e))))))

(deftask dev
  "This is the main development entry point."
  []
  (set-env! :dependencies #(vec (concat % '[[reloaded.repl "0.2.1"]])))
  (set-env! :source-paths #(conj % "dev"))

  ;; Needed by tools.namespace to know where the source files are
  (apply clojure.tools.namespace.repl/set-refresh-dirs (get-env :directories))

  (comp
   (watch)
   (reload :ids cljs-build-ids :cljs-asset-path "/static/")
   (cljs-repl :nrepl-opts {:client false
                           :port repl-port
                           :init-ns 'user}) ; this is also the server repl!
   (cljs :ids cljs-build-ids :optimizations :none)
   (dev-system)
   (target :dir #{"static"})))

(deftask build
  "This is used for creating optimized static resources under static"
  []
  (comp
   (cljs :ids cljs-build-ids :optimizations :advanced)
   (target :dir #{"static"})))

(defn- run-system [profile]
  (println "Running system with profile" profile)
  (let [system (new-system profile)]
    (component/start system)
    (intern 'user 'system system)
    (with-pre-wrap fileset
      (assoc fileset :system system))))

(deftask run [p profile VAL kw "Profile"]
  (comp
   (repl :server true
         :port (case profile :prod 5601 :beta 5602 5600)
         :init-ns 'user)
   (run-system (or profile :prod))
   (wait)))
