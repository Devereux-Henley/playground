(ns playground.server.authorization
  (:require
   [buddy.sign.jwt :as jwt]
   [clj-time.core :as time]
   [om.next :as om]
   [playground.server.api.authorization :as api]
   [playground.server.constants :refer [standard-inputs]]
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
    {:post {:consumes standard-inputs
            :produces "text/plain"
            :parameters {:body UserAuth}
            :response
            (fn [ctx]
              (let [{:keys [user password] :as auth-pair} (get-in ctx [:parameters :body])
                    {:keys [id authenticated?]} (:results (api/auth-user db-spec auth-pair))
                    roles (if authenticated? (:results (api/get-user-roles db-spec id)) nil)]
                (merge
                  (:response ctx)
                  (if-not authenticated?
                    {:status 401}
                    {:status 200
                     :cookies
                     {"session"
                      {:path "/"
                       :value
                       (jwt/sign
                         {:claims (pr-str {:user user :roles #{:user}})
                          :exp (time/plus (time/now) (time/hours 8))}
                         secret)}}}))))}}}))

(defn authorization-api-routes
  [db-spec {:keys [port]}]
  (let [api-routes ["/login"
                    [
                     ["" (new-authorization-post-resource db-spec)]
                     ["/" (new-authorization-post-resource db-spec)]]]]
    api-routes
    ))
