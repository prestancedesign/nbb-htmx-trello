(ns routes.lists
  (:require ["express$Router" :as router]
            ["pug" :as pug]
            [data.lists :refer [lists]]))

(def lists-router
  (-> (router)
      (.get "/add"
            (fn [_req res]
              (let [template (.compileFile pug "views/_add-list.pug")]
                (.send res (template)))))
      (.post "/"
             (fn [req res]
               (let [name (.. req -body -name)
                     id (str (random-uuid))
                     new-list {:id id :name name :cards []}
                     _ (swap! lists conj new-list)
                     template (.compileFile pug "views/_board.pug")]
                 (.send res (template (clj->js {:lists @lists}))))))
      (.get "/cancel"
            (fn [_req res]
              (let [template (.compileFile pug "views/_new-list.pug")]
                (.send res (template)))))))
