(ns routes.lists
  (:require ["express$Router" :as router]
            ["pug" :as pug]
            [data.db :refer [db]]))

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
                     new-list {:id (str (random-uuid)) :name name}
                     _ (swap! db update :lists conj new-list)
                     _ (swap! db update :cards conj {id []})
                     template (.compileFile pug "views/_board.pug")]
                 (.send res (template (clj->js @db))))))
      (.get "/cancel"
            (fn [_req res]
              (let [template (.compileFile pug "views/_new-list.pug")]
                (.send res (template)))))))
