(ns teod.subcons.build
  (:require [clojure.edn :as edn]
            [clojure.stacktrace]
            [teod.subcons.builder :as builder]
            [teod.subcons.transform :as transform]
            [hawk.core :as hawk]))


(defonce watcher (atom nil))

(defn watch-rebuild-edn-stop!
  "Stop all watchers."
  []
  (swap! watcher
         (fn [w]
           (when w
             (hawk/stop! w))
           nil)))

(defn index-edn? [ctx {:keys [file] :as e}]
  (and (hawk/file? ctx e)
       (= "index.edn"
          (.getName file))))

(defn watch-rebuild-edn-handler
  "Provides information on file source @ :teod.subcons/source-path"
  [_ctx e]
  (let [{:keys [_kind file]} e]
    (try
      (let [edn-path (.getPath file)
            _ (print "building" edn-path "...")
            edn (-> edn-path slurp edn/read-string
                    (vary-meta assoc :teod.subcons/source-path edn-path)
                    transform/transform)]
        (builder/builder edn)
        (println " done."))
      (catch Throwable t
        (println "Faied!")
        (clojure.stacktrace/print-stack-trace t)))))

(defn watch-rebuild-edn!
  "Look for changes to EDN files; then try to rebuild."
  [_opts]
  (println "Watching and rebuilding index.edn files")
  (watch-rebuild-edn-stop!)
  (hawk/watch! [{:paths ["."]
                 :filter #'index-edn?
                 :handler #'watch-rebuild-edn-handler}]))

(comment
  (watch-rebuild-edn! {})

  (-> ["teodor"]
      (with-meta  {:teod/id "teod"})
      (vary-meta assoc :teod/count 99)
      meta)
  )
