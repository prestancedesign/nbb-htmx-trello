(ns routes.router
  (:require ["express$Router" :as router]
            ["pug" :as pug]
            [data.lists :refer [lists]]))

(def lists-router
  (-> (router)
      (.get "/add"
            (fn [_req res]
              (let [template (.compileFile pug "views/_add-list.pug")
                    markup (template)]
                (.send res markup))))
      (.post "/"
             (fn [req res]
               (let [name (.. req -body -name)
                     id (inc (count (:lists @lists)))
                     new-list (swap! lists update :lists conj {id {:id id :name name :cards []}})
                     template (.compileFile pug "views/_board.pug")
                     markup (template (clj->js new-list))]
                 (.send res markup))))
      (.get "/cancel"
            (fn [_req res]
              (let [template (.compileFile pug "views/_new-list.pug")
                    markup (template)]
                (.send res markup))))))

(def cards-router
  (-> (router)
      (.get "/edit/:list_id/:id"
            (fn [req res]
              (let [list-id (int (.. req -params -list_id))
                    id (int (.. req -params -id))
                    list (get-in @lists [:lists list-id])
                    card (first (filter (comp #{id} :id) (:cards list)))
                    template (.compileFile pug "views/_edit-card.pug")
                    markup (template (clj->js {:id id :list list :card card}))]
                (.send res markup))))
      (.put "/:list_id/:id"
            (fn [req res]
              (let [label (.. req -body -label)
                    list-id (int (.. req -params -list_id))
                    id (int (.. req -params -id))
                    idx (first (keep-indexed #(when (= (:id %2) id) %1) (get-in @lists [:lists list-id :cards])))
                    _ (swap! lists update-in [:lists list-id] assoc-in [:cards idx :label] label)
                    list (get-in @lists [:lists list-id])
                    card (first (filter #(= id (:id %)) (:cards list)))
                    template (.compileFile pug "views/_card.pug")
                    markup (template (clj->js {:list list :card card}))]
                (.send res markup))))
      (.get "/cancel-edit/:list_id/:id"
            (fn [req res]
              (let [list-id (int (.. req -params -list_id))
                    id (int (.. req -params -id))
                    list (get-in @lists [:lists list-id])
                    card (first (filter #(= id (:id %)) (:cards list)))
                    template (.compileFile pug "views/_card.pug")
                    markup (template (clj->js {:id id :list list :card card}))]
                (.send res markup))))))
