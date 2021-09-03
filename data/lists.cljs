(ns data.lists)

(def lists
  (atom {:lists (sorted-map 1 {:id 1
                               :name "To Do"
                               :cards [{:id 1 :label "First Card" :list 1}
                                       {:id 2 :label "Second Card" :list 1}]}
                            2 {:id 2
                               :name "Doing"
                               :cards [{:id 3 :label "First Card" :list 2}
                                       {:id 4 :label "Second Card" :list 2}
                                       {:id 5 :label "Third Card" :list 2}]})}))
