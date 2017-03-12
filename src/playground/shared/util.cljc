(ns playground.shared.util
  (:require #?(:cljs [ajax.core :refer [GET POST]])
            [cognitect.transit :as t]
            [om.next :as om]))

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
