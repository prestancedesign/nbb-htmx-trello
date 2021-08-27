(ns index
  (:require ["express$default" :as express]
            ["pug" :as pug]
            ["body-parser$default" :as body-parser]
            [data.lists :refer [lists]]
            [routes.lists :refer [lists-router]]))

(let [port 3000]
  (-> (express)
      (.set "view engine" "pug")
      (.use (.static express "assets"))
      (.use (.json body-parser))
      (.use (.urlencoded body-parser #js {:extended true}))
      (.get "/" (fn [_req res]
                  (.render res "index" (clj->js @lists))))
      (.use "/lists" lists-router)
      ;; Cards routes
      (.get "/cards/edit/:list_id/:id" (fn [req res]
                                         (let [list-id (int (.. req -params -list_id))
                                               id (int (.. req -params -id))
                                               list (get-in @lists [:lists list-id])
                                               card (first (filter (comp #{id} :id) (:cards list)))
                                               template (.compileFile pug "views/_edit-card.pug")
                                               markup (template (clj->js {:id id
                                                                          :list list
                                                                          :card card}))]
                                           (.send res markup))))
      (.put "/cards/:list_id/:id" (fn [req res]
                                    (let [label (.. req -body -label)
                                          list-id (int (.. req -params -list_id))
                                          id (int (.. req -params -id))
                                          _ (swap! lists update-in [:lists list-id] assoc-in [:cards (dec id) :label] label)
                                          list (get-in @lists [:lists list-id])
                                          card (first (filter #(= id (:id %)) (:cards list)))
                                          template (.compileFile pug "views/_card.pug")
                                          markup (template (clj->js {:list list
                                                                     :card card}))]
                                      (.send res markup))))
      (.listen port #(println "Listening on port:" port))))
