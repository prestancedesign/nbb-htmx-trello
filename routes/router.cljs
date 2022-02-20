(ns routes.router
  (:require ["express$Router" :as router]
            ["pug" :as pug]
            [applied-science.js-interop :as j]
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

(defn find-by-id [id coll]
  (some #(when (= (:id %) id) %) coll))

(defn render [template-path id list-id]
  (let [template (.compileFile pug (str "views/" template-path ".pug"))
        list (find-by-id list-id @lists)
        card (find-by-id id (:cards list))]
    (template (clj->js {:list list :card card}))))

(def cards-router
  (-> (router)
      (.post "/new/:list_id"
             (fn [req res]
               (j/let [^:js {:keys [list_id]} (j/get req :params)
                       label (j/get-in req [:body (str "label-" list_id)])
                       list (find-by-id list_id @lists)
                       card {:label label
                             :id (str (random-uuid))
                             :list (:id list)}
                       _ (swap! lists update-in [(dec list_id) :cards] conj card)
                       template (.compileFile pug "views/_new-card.pug")
                       markup (template (clj->js {:card card :list list}))]
                 (.send res markup))))
      (.get "/edit/:list_id/:id"
            (fn [req res]
              (j/let [^:js {:keys [id list_id]} (j/get req :params)
                      markup (render "_edit-card" id list_id)]
                (.send res markup))))
      (.put "/:list_id/:id"
            (fn [req res]
              (let [label (.. req -body -label)
                    ;TODO: Find a solution for coerce id + list-id
                    list-id (.. req -params -list_id)
                    id (.. req -params -id)
                    _ (swap! lists update-in [(dec list-id) :cards]
                             (fn [l] (mapv #(if (= (:id %) id) (assoc % :label label) %) l)))
                    markup (render "_card" id list-id)]
                (.send res markup))))
      (.get "/cancel-edit/:list_id/:id"
            (fn [req res]
              (j/let [^:js {:keys [id list_id]} (j/get req :params)
                      markup (render "_card" id list_id)]
                (.send res markup))))
      (.delete "/:list_id/:id"
               (fn [req res]
                 (let [list-id (.. req -params -list_id)
                       id (.. req -params -id)
                       _ (swap! lists update-in [(dec list-id) :cards]
                                #(remove (fn [i] (= (:id i) id)) %))]
                   (.send res ""))))))
