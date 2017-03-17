(ns playground.shared.ui
  (:require
   #?(:cljs [goog.dom :as gdom])
   [om.dom :as dom]
   [om.next :as om :refer [IQuery IQueryParams defui]]
   [playground.shared.util :as util]))

(defmulti read-navigation om/dispatch)

(defmethod read-navigation :default
  [env key _]
  (util/default-parser env key))

(defui ^:once SessionMenu
  static IQuery
  (query
   [this]
   [:organization/organization-name :user/username :user/first-name :user/last-name])
  Object
  (render
   [this]
   (let [{:keys [:organization/organization-name :user/username :user/first-name :user/last-name]} (om/props this)]
     (dom/ul nil
             (dom/li nil organization-name)
             (dom/li nil username)
             (dom/li nil (str first-name " " last-name))))))

(def session-menu-factory (om/factory SessionMenu))

(defui ^:once NavigationBar
  static IQuery
  (query
   [this]
   [:user/session])
  Object
  (render
   [this]
   (let [{:keys [:user/session]} (om/props this)]
     (dom/nav nil
              (session-menu-factory session)))))

(def navigation-bar-factory (om/factory NavigationBar))

#?(:clj
   (defn make-reconciler
     [server-send]
     (om/reconciler
      {:state (atom {})
       :normalize true
       :parser (om/parser {:read read-navigation})
       :send server-send})))

#?(:cljs
   (defonce navigation-reconciler
     (om/reconciler
      {:state (atom {})
       :normalize true
       :parser (om/parser {:read read-navigation})
       :send (util/transit-post "/api/navigation")})))

#?(:cljs
   (defn navigation-init
     [reconciler]
     (om/add-root! reconciler NavigationBar (gdom/getElement "navigation"))))
