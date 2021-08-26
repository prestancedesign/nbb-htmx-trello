(ns index
  (:require ["express$default" :as express]
            ["pug" :as pug]
            ["body-parser$default" :as body-parser]
            [data.lists :refer [lists]]))

(let [port 3000]
  (-> (express)
      (.set "view engine" "pug")
      (.use (.static express "assets"))
      (.use (.json body-parser))
      (.use (.urlencoded body-parser #js {:extended true}))
      (.get "/" (fn [_req res]
                  (.render res "index" (clj->js @lists))))
      (.get "/lists/add" (fn [_req res]
                           (let [template (.compileFile pug "views/_add-list.pug")
                                 markup (template)]
                             (.send res markup))))
      (.post "/lists" (fn [req res]
                        (let [name (.. req -body -name)
                              new-list (swap! lists update :lists conj {:id (inc (count (:lists lists)))
                                                                        :name name
                                                                        :cards []})
                              template (.compileFile pug "views/_board.pug")
                              markup (template (clj->js new-list))]
                          (.send res markup))))
      (.get "/lists/cancel" (fn [_req res]
                              (let [template (.compileFile pug "views/_new-list.pug")
                                    markup (template)]
                                (.send res markup))))
      (.listen port #(println "Listening on port:" port))))
