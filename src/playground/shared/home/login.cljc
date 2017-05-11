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
       {:handler (fn [{:keys [status status-text response]}]
                   (do
                     (.pushState (.-history js/window) "Organization" "" (get-route :route/index))
                     (om/transact! this `[~(om/force :current/user :remote)
                                          ~(om/force :organizations/organizations-by-id :remote)])
                     (compassus/set-route! this :route/index)))
        :error-handler (fn [{:keys [status status-text response]}]
                         (om/set-query! this {:params
                                              {:user ""
                                               :password ""
                                               :error true
                                               :error-message "Incorrect login information provided"}})
                         (.forceUpdate this))
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
     :password ""
     :error false
     :error-message ""})
  static om/IQuery
  (query [_]
    '[(:login-information {:user ?user
                           :password ?password})
      (:error-information {:error ?error
                           :error-message ?error-message})])
  Object
  (render
    [this]
    (let [
          {:keys [get-route]}         (om/shared this)]
      (dom/div #js {:className "login-page"}
        (dom/div #js {:className "login-body"}
          (dom/h1 nil "Login Page")
          (dom/div #js {:className "login-form"}
            (dom/label nil
              "Username: "
              (dom/input
                #js {:type "text"
                     :value ""
                     :onChange (fn [event]
                                 (let [{:keys [password]} (om/get-params this)]
                                   (om/set-query! this {:params {:user
                                                                 (.. event -target -value)
                                                                 :password
                                                                 password}})))}))
            (dom/label nil
              "Password: "
              (dom/input
                #js {:type "password"
                     :value ""
                     :onChange (fn [event]
                                 (let [{:keys [user]} (om/get-params this)]
                                   (om/set-query! this {:params {:user
                                                                 user
                                                                 :password
                                                                 (.. event -target -value)}})))}))
            (dom/button #js {:className "submission-button"
                             :onClick (fn [event]
                                        #?(:cljs
                                           (let [{:keys [user password]}
                                                 (om/get-params this)]
                                             (login-post
                                               this
                                               get-route
                                               user
                                               password))))}
              "Log in!"))
          (let [{:keys [error error-message]} (om/get-params this)]
               (when error
                 (dom/div #js {:className "error"}
                   (str "Error: " error-message)))))))))
