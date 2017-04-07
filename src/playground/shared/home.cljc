(ns playground.shared.home
  (:require
   #?@(:cljs [[goog.dom :as gdom]
              [pushy.core :as pushy]])
   [bidi.bidi :as bidi]
   [compassus.core :as compassus]
   [om.next :as om]
   [playground.shared.home.cards :as cards]
   [playground.shared.home.index :as index]
   [playground.shared.home.information :as information]
   [playground.shared.logging :as log]
   [playground.shared.ui :as ui]
   [playground.shared.util :as util]))

#?(:cljs (log/log-to-console!))

(defmulti read-home om/dispatch)

(defmethod read-home :route/index
  [{:keys [state query target ast logger] :as env} _ _]
  (let [st @state]
    (if (some st query)
      {:value (select-keys st query)}
      {:remote true})))

(defmethod read-home :route/cards
  [{:keys [state query target ast logger] :as env} _ _]
  (let [st @state]
    (if (some st query)
      {:value (select-keys st query)}
      {:remote true})))

(defmethod read-home :route/information
  [{:keys [state query target ast logger] :as env} _ _]
  (let [st @state]
    (if (some st query)
      {:value (select-keys st query)}
      {:remote true})))

(defmethod read-home :user/session
  [{:keys [state query target ast logger] :as env} key _]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:remote true})))

(declare app)

(defonce routes
  ["/" {"home"         :route/index
        "cards"        :route/cards
        "information"  :route/information}])

#?(:cljs
   (defn update-route!
     [{:keys [handler] :as route}]
     (let [current-route (compassus/current-route app)]
       (when (not= handler current-route)
         (compassus/set-route! app handler)))))

#?(:cljs
   (defonce history
     (pushy/pushy update-route!
       (partial bidi/match-route routes))))

(defonce route-map
  {:route/index       index/IndexPage
   :route/cards       cards/CardsPage
   :route/information information/InformationPage})

(defn get-route
  [route-key]
  (bidi/path-for routes route-key))

(defonce home-parser
  (compassus/parser {:read read-home}))

#?(:clj
   (defn make-app
     [server-send]
     (compassus/application
       {:routes route-map
        :reconciler (om/reconciler
                      {:state (atom {})
                       :parser home-parser
                       :shared {:get-route get-route}
                       :send server-send})
        :mixins [(compassus/wrap-render ui/NavigationWrapper)]})))

#?(:cljs
   (defonce app
     (compassus/application
       {:routes route-map
        :reconciler (om/reconciler
                      {:state (atom {})
                       :parser home-parser
                       :send (util/transit-post "/api/home")
                       :shared {:get-route get-route}
                       :logger log/logger})
        :mixins [(compassus/wrap-render ui/NavigationWrapper)
                 (compassus/will-mount (fn [_] (pushy/start! history)))
                 (compassus/will-unmount (fn [_] (pushy/stop! history)))]})))
