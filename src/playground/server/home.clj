(ns playground.server.home
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [om.next :as om]))

(defn home-page
  [send-func]  
  (let [nav-string "foo"]
    (html
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:http-equiv "X-UA-Compatible"}]
       [:title "Home"]]
      [:body
       [:section#nav nav-string]
       [:section#home
        [:div
         [:p "Welcome to the home page!"]]]
       (include-js "/home.js")])))
