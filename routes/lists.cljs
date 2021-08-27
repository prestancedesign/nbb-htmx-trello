(ns routes.lists
  (:require ["express$Router" :as router]
            ["pug" :as pug]
            [data.lists :refer [lists]]))

(def lists-router
  (-> (router)
      (.get "/add" (fn [_req res]
                     (let [template (.compileFile pug "views/_add-list.pug")
                           markup (template)]
                       (.send res markup))))
      (.post "/" (fn [req res]
                   (let [name (.. req -body -name)
                         id (inc (count (:lists @lists)))
                         new-list (swap! lists update :lists conj {id {:id id
                                                                       :name name
                                                                       :cards []}})
                         template (.compileFile pug "views/_board.pug")
                         markup (template (clj->js new-list))]
                     (.send res markup))))
      (.get "/cancel" (fn [_req res]
                        (let [template (.compileFile pug "views/_new-list.pug")
                              markup (template)]
                          (.send res markup))))))
