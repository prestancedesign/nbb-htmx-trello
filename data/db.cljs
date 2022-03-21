(ns data.db)

(def db (atom {:lists [{:id "1" :name "To Do"}
                       {:id "2" :name "Doing"}]
               :cards {"1" [{:id "1" :label "First Card" :list 1}
                            {:id "2" :label "Second Card" :list 1}]
                       "2" [{:id "3" :label "First Card" :list 2}
                            {:id "4" :label "Second Card" :list 2}
                            {:id "5" :label "Third Card" :list 2}]}}))
