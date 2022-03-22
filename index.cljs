(ns index
  (:require ["express$default" :as express]
            ["body-parser$default" :as body-parser]
            [data.db :refer [db]]
            [routes.lists :refer [lists-router]]
            [routes.cards :refer [cards-router]]))

(let [port 3000]
  (-> (express)
      (.set "view engine" "pug")
      (.use (express/static "assets"))
      (.use (body-parser/json))
      (.use (body-parser/urlencoded #js {:extended true}))
      (.get "/" (fn [_req res]
                  (.render res "index" (clj->js @db))))
      (.use "/lists" lists-router)
      (.use "/cards" cards-router)
      (.listen port #(println "Listening on port:" port))))
