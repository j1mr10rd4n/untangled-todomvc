(ns untangled-todomvc.routing
  (:require
    [secretary.core :as secretary :refer-macros [defroute]]
    [om.next :as om]
    [untangled-todomvc.mutations :as mut]))

(defn get-url
  [] (-> js/window .-location .-href))

(defn uri-params
  ([] (uri-params (get-url)))
  ([url]
   (let [query-data (.getQueryData (goog.Uri. url))]
     (into {}
           (for [k (.getKeys query-data)]
             [k (.get query-data k)])))))

(defn get-url-param
  ([param-name] (get-url-param (get-url) param-name))
  ([url param-name]
   (get (uri-params url) param-name)))

(defn configure-routing! [reconciler]
  (secretary/set-config! :prefix "#")

  (defroute active-items "/active" []
    (om/transact! reconciler `[(mut/set-todo-filter ~{:todo-filter :list.filter/active})]))

  (defroute completed-items "/completed" []
    (om/transact! reconciler `[(mut/set-todo-filter ~{:todo-filter :list.filter/completed})]))

  (defroute all-items "*" []
    (om/transact! reconciler `[(mut/set-todo-filter ~{:todo-filter :list.filter/none})])))

