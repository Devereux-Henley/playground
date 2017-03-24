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
  ["/" {""         :index
        "home"     :index}])

(defmulti read-home om/dispatch)

(defmethod read-home :default
  [{:keys [state query]} _ _]
  {:value (select-keys @state query)})

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

#?(:clj
   (defn make-app
     [server-send]
     (compassus/application
       {:routes route-map
        :index-route :route/index
        :reconciler (om/reconciler
                      {:state (atom {})
                       :parser (compassus/parser {:read read-home})
                       :normalize true
                       :send server-send})
        :mixins [(compassus/wrap-render ui/NavigationWrapper)]})))

#?(:cljs
   (defonce app
     (compassus/application
       {:routes route-map
        :index-route :route/index
        :reconciler (om/reconciler
                      {:state (atom {})
                       :parser (compassus/parser {:read read-home})
                       :normalize true
                       :send (util/transit-post "/api/navigation")})
        :mixins [(compassus/wrap-render ui/NavigationWrapper)
                 (compassus/did-mount (fn [_] (pushy/start! history)))
                 (compassus/will-unmount (fn [_] (pushy/stop! history)))]})))
