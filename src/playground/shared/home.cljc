(ns playground.shared.home
  (:require
   #?@(:cljs [[goog.dom :as gdom]
              [pushy.core :as pushy]])
   [bidi.bidi :as bidi]
   [compassus.core :as compassus]
   [om.dom :as dom]
   [om.next :as om :refer [IQuery IQueryParams defui]]
   [playground.shared.home.index :as index]
   [playground.shared.ui :as ui]
   [playground.shared.util :as util]))

(def routes
  ["/" {""         :route/index
        "home"     :route/index}])

(defmulti read-home om/dispatch)

(defmethod read-home :route/index
  [{:keys [state query target ast] :as env} _ _]
  (let [st @state]
    (if (some st query)
      {:value (select-keys st query)}
      {:backend-remote ast})))

(defmethod read-home :user/session
  [{:keys [state query target ast] :as env} key _]
  (let [st @state]
    (println env)
    (if-let [[_ value] (find st key)]
      {:value value :backend-remote ast}
      {:backend-remote ast})))

(declare app)

#?(:cljs
   (defn update-route!
     [{:keys [handler] :as route}]
     (let [current-route (compassus/current-route app)]
      (when (not= handler current-route)
        (compassus/set-route! app handler)))))

#?(:cljs
   (def history
     (pushy/pushy update-route!
       (partial bidi/match-route routes))))

(def route-map
  {:route/index index/IndexPage})

(defn make-home-parser
  []
  (compassus/parser {:read read-home}))

#?(:clj
   (defn make-app
     [server-send]
     (compassus/application
       {:routes route-map
        :index-route :route/index
        :reconciler (om/reconciler
                      {:state (atom {})
                       :parser (make-home-parser)
                       :remotes [:backend-remote]
                       :send server-send})
        :mixins [(compassus/wrap-render ui/NavigationWrapper)]})))

#?(:cljs
   (defonce app
     (compassus/application
       {:routes route-map
        :index-route :route/index
        :reconciler (om/reconciler
                      {:state (atom {})
                       :parser (make-home-parser)
                       :remotes [:backend-remote]
                       :send (util/transit-post "/api/home")})
        :mixins [(compassus/wrap-render ui/NavigationWrapper)
                 (compassus/did-mount (fn [_] (pushy/start! history)))
                 (compassus/will-unmount (fn [_] (pushy/stop! history)))]})))
