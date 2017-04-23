(ns playground.server.authorization
  (:require
   [buddy.sign.jwt :as jwt]
   [om.next :as om]
   [playground.server.api.users :as api]
   [playground.server.middleware.authorization :refer [secret]]
   [schema.core :as schema :refer [required-key defschema]]
   [yada.swagger :as swagger]
   [yada.yada :as yada]))

(defschema UserAuth
  {:user String
   :password String})

(defn new-authorization-post-resource
  [db-spec]
  (yada/resource
   {:id :playground.resources/authorization-token-post
    :description "Post route for authorizing users and returning JSON tokens."
    :produces  [{:media-type #{"text/plain"}
                 :charset "UTF-8"}]
    :methods
    {:post {:consumes "application/x-www-form-urlencoded"
            :parameters {:form UserAuth}
            :response
            (fn [ctx]
              (let [{:keys [user password] :as auth-pair} (-> ctx :parameters :form)
                    authenticated? (api/auth-user db-spec auth-pair)]
                (merge
                  (:response ctx)
                  (if-not authenticated?
                    {:body "Login failed"
                     :status 401}
                    {:status 303
                     :headers {"location" (yada/url-for ctx :playground.resources/index)}
                     :cookies
                     {"session"
                      {:value
                       (jwt/sign
                         {:claims (pr-str {:user user :roles #{:user}})}
                         secret)}}}))))}}}))

(defn authorization-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/login"
                    [
                     ["" (new-authorization-post-resource db-spec)]
                     ["/" (new-authorization-post-resource db-spec)]]]]
    [""
     [
      api-routes
      ]]
    ))
