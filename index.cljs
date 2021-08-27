(ns index
  (:require ["express$default" :as express]
            ["body-parser$default" :as body-parser]
            [data.lists :refer [lists]]
            [routes.router :refer [lists-router cards-router]]))

(let [port 3000]
  (-> (express)
      (.set "view engine" "pug")
      (.use (.static express "assets"))
      (.use (.json body-parser))
      (.use (.urlencoded body-parser #js {:extended true}))
      (.get "/" (fn [_req res]
                  (.render res "index" (clj->js @lists))))
      (.use "/lists" lists-router)
      (.use "/cards" cards-router)
      (.listen port #(println "Listening on port:" port))))
