(ns playground.shared.home.login
  (:require
   #?(:cljs [ajax.core :refer [POST]])
   [compassus.core :as compassus]
   [cognitect.transit :as t]
   [om.dom :as dom]
   [om.next :as om :refer [defui]]))

#?(:cljs
   (defn login-post
     [this get-route username password]
     (POST "/api/login"
       {:handler (fn [response]
                   (do
                     (.pushState (.-history js/window) "Organization" "" (get-route :route/index))
                     (om/transact! this `[(session/refresh-session) ~(om/force :user/session :remote)])
                     (compassus/set-route! this :route/index)))
        :body (t/write (t/writer :json) {:user username
                                         :password password})
        :format :transit
        :params :transit
        :response-format :text
        :with-credentials true
        :headers {:content-type "application/transit+json"}})))

(defui ^:once LoginPage
  static om/IQueryParams
  (params [_]
    {:user ""
     :password ""})
  static om/IQuery
  (query [_]
    '[(:login-information {:user ?user
                           :password ?password})])
  Object
  (render
    [this]
    (let [{:keys [login-information]} (om/props this)
          {:keys [get-route]}         (om/shared this)]
      (dom/div #js {:className "login"}
        (dom/h1 nil "Login Page")
        (dom/div #js {:className "login-form"}
          (dom/label nil
            "Username"
            (dom/input
              #js {:type "text"
                   :value (:user (om/get-params this))
                   :onChange (fn [event]
                               (om/set-query! this {:params {:user
                                                             (.. event -target -value)
                                                             :password
                                                             (:password (om/get-params this))}}))}))
          (dom/label nil
            "Password"
            (dom/input
              #js {:type "password"
                   :value (:password (om/get-params this))
                   :onChange (fn [event]
                               (om/set-query! this {:params {:user
                                                             (:user (om/get-params this))
                                                             :password
                                                             (.. event -target -value)}}))}))
          (dom/button #js {:className "submission-button"
                           :onClick (fn [event]
                                      #?(:cljs (login-post
                                                 this
                                                 get-route
                                                 (:user (om/get-params this))
                                                 (:password (om/get-params this)))))}
            "Log in!"))))))
