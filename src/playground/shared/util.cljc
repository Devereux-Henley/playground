(ns playground.shared.util
  (:require #?(:cljs [ajax.core :refer [GET POST]])
            [cognitect.transit :as t]
            [om.next :as om]
            [om.dom :as dom]))
#?(:clj
   (defn create-om-string
     [reconciler ui-component]
     (let [root (om/add-root! reconciler ui-component nil)]
       (dom/render-to-str root))))

#?(:clj
   (defn server-send
     [parser-partial]
     (fn [{:keys [remote] :as env} callback]
       (let [response (parser-partial remote)]
         (callback response)))))

#?(:cljs
   (defn transit-post
     [url]
     (fn [{:keys [remote]} post-callback]
       (POST url
         {:handler (fn [response]
                     (post-callback response))
          :body (t/write (t/writer :json) remote)
          :format :transit
          :params :transit
          :response-format :transit
          :headers {:content-type "application/transit+json"}}))))


(defn default-parser
  [{:keys [state] :as env} key _]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value :remote (:ast env)}
      {:remote true})))
