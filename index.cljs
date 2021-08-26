(ns index
  (:require ["express$default" :as express]
            [data.lists :refer [lists]]))

(def port 3000)

(-> (express)
    (.set "view engine" "pug")
    (.get "/" (fn [_req res]
                (.render res "index" (clj->js lists))))
    (.listen port #(println "Listening on port:" port)))
