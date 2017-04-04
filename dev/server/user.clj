(ns user
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :refer (pprint)]
    [clojure.stacktrace :refer (print-stack-trace)]
    [clojure.tools.namespace.repl :refer [disable-reload! refresh clear set-refresh-dirs]]
    [com.stuartsierra.component :as cp]
    [figwheel-sidecar.system :as fsys]
    [taoensso.timbre :refer [info set-level!] :as timbre]
    [todomvc.system :as system]
    [watch :refer [start-watching stop-watching reset-fn]]))

;;FIGWHEEL
(defn start-figwheel
  "Start Figwheel on the given builds, or defaults to build-ids in `figwheel-config`."
  ([] (->> (fsys/fetch-config) :data :all-builds
        (mapv :id) (select-keys (System/getProperties)) keys
        start-figwheel))
  ([build-ids]
   (println build-ids)
   (-> (fsys/fetch-config)
     (assoc-in [:data :build-ids] build-ids)
     fsys/figwheel-system cp/start fsys/cljs-repl)))

;;SERVER

(set-refresh-dirs "src/server" "specs/server" "dev/server")

(def system (atom nil))

(set-level! :info)

(defn init
  "Create a web server from configurations. Use `start` to start it."
  []
  (reset! system (system/make-system)))

(defn start "Start (an already initialized) web server." [] (swap! system cp/start))

(defn stop "Stop the running web server. Is a no-op if the server is already stopped" []
  (when @system
    (swap! system cp/stop)
    (reset! system nil)))

(defn go "Load the overall web server system and start it." []
  (init)
  (start))

(defn reset
  "Stop the web server, refresh all namespace source code from disk, then restart the web server."
  []
  (stop)
  (refresh :after 'user/go))

(reset! watch/reset-fn reset)

(comment
  (System/getProperty "boo")
  (keys (select-keys (System/getProperties) ["boo"])))
