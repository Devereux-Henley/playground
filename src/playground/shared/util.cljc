(ns playground.shared.util
  (:require #?(:cljs [ajax.core :refer [GET POST]]
               :clj [clojure.string :refer [split]])
            [cognitect.transit :as t]
            [om.next :as om]
            [om.dom :as dom]))

#?(:clj
   (defn build-id
     [first-form keyword-to-parse]
     (keyword
       (str first-form
         "/"
         (-> keyword-to-parse
           name
           (split #"\/")
           last)))))

#?(:clj
   (defn create-om-string
     [reconciler ui-component]
     (let [root (om/add-root! reconciler ui-component nil)]
       (dom/render-to-str root))))

#?(:clj
   (defn server-send
     [parser-partial]
     (fn [remotes callback]
       (doseq [remote remotes]
         (let [response (parser-partial (second remote))]
           (callback response))))))

#?(:cljs
   (defn transit-post
     [url]
     (fn [{:keys [remote] :as env} post-callback]
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
