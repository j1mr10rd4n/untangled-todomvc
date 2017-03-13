(ns untangled-todomvc.core
  (:require
    [goog.events :as events]
    [goog.history.EventType :as EventType]
    [om.next :as om]
    [secretary.core :as secretary :refer-macros [defroute]]
    [untangled-todomvc.i18n.default-locale]
    [untangled-todomvc.i18n.locales]
    [untangled-todomvc.routing :refer [configure-routing!]]
    [untangled-todomvc.ui :as ui]
    [untangled.client.core :as uc]
    [untangled.client.data-fetch :as df]
    [untangled.client.logging :as log]
    [untangled.client.mutations :as m])
  (:import goog.History))

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

(m/defmutation merge-todos [_]
  (action [{:keys [state]}]
    (swap! state
      #(let [todo-list (get @state :todo-list)]
         (-> %
           (update :todos merge todo-list)
           (dissoc :todo-list))))))

(defn on-app-started [app]
  (let [reconciler (:reconciler app)
        state (om/app-state reconciler)
        list (:list @state)]
    (df/load reconciler :todo-list ui/TodoList
      {:without #{:list/filter} :params {:list list}
       :refresh [:todos] :post-mutation `merge-todos})
    (configure-routing! reconciler))
  (let [h (History.)]
    (events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true))))

(defonce app (atom (uc/new-untangled-client
                     :initial-state {:list  (or (get-url-param "list") "main")
                                     :todos {:list/title  ""
                                             :list/items  []
                                             :list/filter :none}}
                     :started-callback on-app-started)))
