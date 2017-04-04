(ns untangled-todomvc.core
  (:require
    [goog.events :as events
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

(defn on-app-started [app]
  (let [reconciler (:reconciler app)
        state (om/app-state reconciler)
        list (:list @state)]
    (df/load reconciler :todos ui/TodoList
      {:without #{:list/filter} :params {:list list}})
    (configure-routing! reconciler))
  (let [h (History.)]
    (events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (doto h (.setEnabled true))))

(defonce app (atom (uc/new-untangled-client :started-callback on-app-started)))

(m/defmutation send-support-request [{:keys [comment]}]
  (remote [{:as env :keys [ast]}]
    (assoc ast :params {:comment comment :history (uc/history @app)})))
