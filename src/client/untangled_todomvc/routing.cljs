(ns untangled-todomvc.routing
  (:require
    [secretary.core :as secretary :refer-macros [defroute]]
    [om.next :as om]
    [untangled-todomvc.mutations :as mut]))

(defn configure-routing! [reconciler]
  (secretary/set-config! :prefix "#")

  (defroute active-items "/active" []
    (om/transact! reconciler `[(mut/set-todo-filter ~{:todo-filter :list.filter/active})]))

  (defroute completed-items "/completed" []
    (om/transact! reconciler `[(mut/set-todo-filter ~{:todo-filter :list.filter/completed})]))

  (defroute all-items "*" []
    (om/transact! reconciler `[(mut/set-todo-filter ~{:todo-filter :list.filter/none})])))

