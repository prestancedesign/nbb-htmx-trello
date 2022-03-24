(ns index
  (:require ["express$default" :as express]
            [data.db :refer [db]]
            [routes.lists :refer [lists-router]]
            [routes.cards :refer [cards-router]]))

(let [port 3000]
  (-> (express)
      (.set "view engine" "pug")
      (.use (express/static "assets"))
      (.use (express/json))
      (.use (express/urlencoded #js {:extended true}))
      (.get "/" (fn [_req res]
                  (.render res "index" (clj->js @db))))
      (.use "/lists" lists-router)
      (.use "/cards" cards-router)
      (.listen port #(println "Listening on port:" port))))
