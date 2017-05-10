(ns playground.shared.home
  (:require
   #?@(:cljs [[goog.dom :as gdom]
              [pushy.core :as pushy]])
   [bidi.bidi :as bidi]
   [compassus.core :as compassus]
   [om.next :as om]
   [playground.shared.home.cards :as cards]
   [playground.shared.home.index :as index]
   [playground.shared.home.login :as login]
   [playground.shared.home.organizations :as organizations]
   [playground.shared.home.information :as information]
   [playground.shared.logging :as log]
   [playground.shared.ui :as ui]
   [playground.shared.util :as util]))

#?(:cljs (log/log-to-console!))

(defmulti read-home om/dispatch)

(defmethod read-home :default
  [{:keys [state query target ast logger] :as env} key _]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value nil})))

;; :projects/by-id [{:project-id 1} {:project-id 2}]
(defmethod read-home :project/by-id
  [{:keys [state query target ast logger] :as env} key _]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:remote true})))

;; :organizations/current-organization {:organization-id 1}
(defmethod read-home :organizations/current-organization
  [{:keys [state query-root target ast logger] :as env} key _]
  (let [st @state]
    {:value (get
              (om/db->tree '[[:organizations/current-organization _]] st st)
              :organizations/current-organization)}))

;; :organizations/by-id [{:organization-id 1} {:organization-id 2}]
(defmethod read-home :organizations/organizations-by-id
  [{:keys [state query]} key params]
  (let [st @state
        value (get st key)]
    (if value
      {:value value}
      {:remote true})))

;; :current/user {:user/name "devo"}
(defmethod read-home :current/user
  [{:keys [state]} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:remote true})))

(defmulti mutate-home om/dispatch)

(defmethod mutate-home 'any/refresh
  [{:keys [state]} _ _]
  {:action
   (fn []
     state)})

(defmethod mutate-home 'organization/set-organization
  [{:keys [state]} _ {:keys [organizations/organization-id]}]
  {:action
   (fn []
     (swap! state assoc
       :organizations/current-organization
       [[:organizations/organizations-by-id organization-id]]))})

(declare app)

(defonce routes
  ["/" [
        ["home"         :route/index]
        ["cards"        :route/cards]
        ["information"  :route/information]
        ["login"        :route/login]
        ["organizations" [
                          ["" :route/organizations]
                          ["/" :route/organizations]
                          [["/" [#"\d+" :org-id]] :route/organization-targets]
                          ]]]])

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
  {:route/index                index/IndexPage
   :route/cards                cards/CardsPage
   :route/information          information/InformationPage
   :route/login                login/LoginPage
   :route/organizations        organizations/OrganizationPage
   :route/organization-targets organizations/OrganizationPage})

(defn get-route
  [route-key]
  (bidi/path-for routes route-key))

(defonce home-parser
  (compassus/parser {:read read-home
                     :mutate mutate-home
                     :route-dispatch false}))

#?(:clj
   (defn make-app
     [server-send]
     (compassus/application
       {:state (atom {})
        :routes route-map
        :reconciler (om/reconciler
                      {:state (atom {})
                       :parser home-parser
                       :shared {:get-route get-route}
                       :send server-send})
        :mixins [(compassus/wrap-render ui/NavigationWrapper)]})))

#?(:cljs
   (defonce app
     (compassus/application
       {:state (atom {})
        :routes route-map
        :reconciler (om/reconciler
                      {:state (atom {})
                       :parser home-parser
                       :send (util/transit-post "/api/home")
                       :shared {:get-route get-route}
                       :logger log/logger})
        :mixins [(compassus/wrap-render ui/NavigationWrapper)
                 (compassus/will-mount (fn [_] (pushy/start! history)))
                 (compassus/will-unmount (fn [_] (pushy/stop! history)))]})))
