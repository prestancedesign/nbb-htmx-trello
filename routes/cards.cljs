(ns routes.cards
  (:require ["express$Router" :as router]
            ["pug" :as pug]
            [applied-science.js-interop :as j]
            [data.db :refer [db]]))

(defn find-by-id [id coll]
  (some #(when (= (:id %) id) %) coll))

(defn render [template-path id list-id]
  (let [template (.compileFile pug (str "views/" template-path ".pug"))
        list (find-by-id list-id (:lists @db))
        card (find-by-id id (get-in @db [:cards list-id]))]
    (template (clj->js {:list list :card card}))))

(def cards-router
  (-> (router)
      (.post "/new/:list_id"
             (fn [req res]
               (j/let [^:js {:keys [list_id]} (j/get req :params)
                       label (j/get-in req [:body (str "label-" list_id)])
                       id (str (random-uuid))
                       list (find-by-id list_id (:lists @db))
                       card {:label label
                             :id id
                             :list (:id list)}
                       _ (swap! db update-in [:cards list_id] conj card)]
                 (.send res (render "_new-card" id list_id)))))
      (.get "/edit/:list_id/:id"
            (fn [req res]
              (j/let [^:js {:keys [id list_id]} (j/get req :params)]
                (.send res (render "_edit-card" id list_id)))))
      (.put "/:list_id/:id"
            (fn [req res]
              (let [label (.. req -body -label)
                    list-id (.. req -params -list_id)
                    id (.. req -params -id)
                    _ (swap! db update-in [:cards list-id]
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
                       _ (swap! db update-in [:cards list-id]
                                #(remove (fn [i] (= (:id i) id)) %))]
                   (.send res ""))))))
