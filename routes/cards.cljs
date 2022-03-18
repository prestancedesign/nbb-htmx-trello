(ns routes.cards
  (:require ["express$Router" :as router]
            ["pug" :as pug]
            [applied-science.js-interop :as j]
            [data.lists :refer [lists]]))

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
                       id (str (random-uuid))
                       list (find-by-id list_id @lists)
                       card {:label label
                             :id id
                             :list (:id list)}
                       _ (swap! lists update-in [(dec list_id) :cards] conj card)]
                 (.send res (render "_new-card" id list_id)))))
      (.get "/edit/:list_id/:id"
            (fn [req res]
              (j/let [^:js {:keys [id list_id]} (j/get req :params)]
                (.send res (render "_edit-card" id list_id)))))
      (.put "/:list_id/:id"
            (fn [req res]
              (let [label (.. req -body -label)
                    ;TODO: Find a solution for coerce id + list-id
                    list-id (.. req -params -list_id)
                    id (.. req -params -id)
                    _ (swap! lists update-in [(dec list-id) :cards]
                             (fn [l] (mapv #(if (= (:id %) id) (assoc % :label label) %) l)))]
                (.send res (render "_card" id list-id)))))
      (.get "/cancel-edit/:list_id/:id"
            (fn [req res]
              (j/let [^:js {:keys [id list_id]} (j/get req :params)]
                (.send res (render "_card" id list_id)))))
      (.delete "/:list_id/:id"
               (fn [req res]
                 (let [list-id (.. req -params -list_id)
                       id (.. req -params -id)
                       _ (swap! lists update-in [(dec list-id) :cards]
                                #(remove (fn [i] (= (:id i) id)) %))]
                   (.send res ""))))))
