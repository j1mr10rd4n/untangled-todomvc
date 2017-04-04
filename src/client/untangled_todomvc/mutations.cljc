(ns untangled-todomvc.mutations
  (:require
    [untangled.client.mutations :as m]
    [untangled.dom :refer [unique-key]]
    [untangled.client.core :as uc]))

(m/defmutation toggle-support-request [_]
  (action [{:keys [state]}]
    (swap! state update :ui/support-visible not)))

(m/defmutation add-todo [{:keys [id text]}]
  (action [{:keys [state]}]
    (swap! state #(-> %
                    (update-in [:todo-list/by-id (:list %) :list/items] (fn [todos] ((fnil conj []) todos [:todo/by-id id])))
                    (assoc-in [:todo/by-id id] {:db/id id :item/label text}))))
  (remote [{:keys [state ast]}]
    (assoc ast :params {:id id :text text :list (:list @state)})))

(m/defmutation check-todo [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:todo/by-id id :item/complete] true))
  (remote [_] true))

(m/defmutation uncheck-todo [{:keys [id]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:todo/by-id id :item/complete] false))
  (remote [_] true))

(m/defmutation edit-todo [{:keys [id text]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:todo/by-id id :item/label] text))
  (remote [_] true))

(m/defmutation delete-todo [{:keys [id]}]
  (action [{:keys [state]}]
    (letfn [(remove-item [todos] (vec (remove #(= id (second %)) todos)))]
      (swap! state #(-> %
                      (update-in [:todo-list/by-id (:list %) :list/items] remove-item)
                      (update :todo/by-id dissoc id)))))
  (remote [_] true))

(defn- set-completed [val todos]
  (into {} (map (fn [[k v]] [k (assoc v :item/complete val)]) todos)))

(m/defmutation check-all-todos [_]
  (action [{:keys [state]}]
    (swap! state update :todo/by-id (partial set-completed true)))
  (remote [{:keys [ast state]}]
    (assoc ast :params {:id (:list @state)})))

(m/defmutation uncheck-all-todos [_]
  (action [{:keys [state]}]
    (swap! state update :todo/by-id (partial set-completed false)))
  (remote [{:keys [ast state]}]
    (assoc ast :params {:id (:list @state)})))

(m/defmutation clear-complete-todos [_]
  (action [{:keys [state]}]
    (swap! state
      (fn [st]
        (-> st
          (update-in [:todo-list/by-id (:list st) :list/items]
            (fn [todos]
              (vec (remove
                     (fn [[_ id]]
                       (get-in @state [:todo/by-id id :item/complete] false))
                     todos))))))))
  (remote [{:keys [ast state]}]
    (assoc ast :params {:id (:list @state)})))

(m/defmutation set-todo-filter [{:keys [todo-filter]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:todo-list/by-id (:list @state) :list/filter] todo-filter)))
