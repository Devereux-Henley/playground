(ns playground.server.authorization
  (:require
   [buddy.sign.jwt :as jwt]
   [om.next :as om]
   [playground.server.db.requirements :as db]
   [schema.core :as s]
   [yada.swagger :as swagger]
   [yada.yada :as yada]))

(defn new-authorization-post-resource
  [db-spec]
  (yada/resource
   {:id :playground.resources/authorization-token-post
    :description "Post route for authorizing users and returning JSON tokens."
    :produces  [{:media-type #{"application/json;q=0.9" "application/edn;q=0.9" "application/transit+json;q=0.9"}
                 :charset "UTF-8"}]
    :methods
    {:post {:consumes "application/x-www-form-urlencoded"
            :parameters {:form
                         {:user s/Str :password s/Str}}
            :response
            (fn [ctx]
              (let [{:keys [user password]} (get-in ctx [:parameters :form])]
                (if false
                  (assoc (:response ctx)
                         :cookies {:session
                                   {:value
                                    (jwt/sign {:user user} "secretvaluegoeshere")}})
                  "ooops")))}}}))

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
