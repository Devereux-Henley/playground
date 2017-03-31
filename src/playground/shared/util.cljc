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
     (fn [{:keys [backend-remote]} callback]
       (println backend-remote)
       (let [response (parser-partial backend-remote)]
         (callback response)))))

#?(:cljs
   (defn transit-post
     [url]
     (fn [{:keys [backend-remote]} post-callback]
       (POST url
         {:handler (fn [response]
                     (post-callback response))
          :body (t/write (t/writer :json) backend-remote)
          :format :transit
          :params :transit
          :response-format :transit
          :headers {:content-type "application/transit+json"}}))))

(defn default-parser
  [{:keys [state query target ast] :as env} _ _]
  (let [st @state]
    (if (some st query)
      {:value st}
      {target true})))
