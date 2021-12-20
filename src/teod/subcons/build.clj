(ns teod.subcons.build
  (:require [clojure.edn :as edn]
            [clojure.stacktrace]
            [teod.subcons.builder :as builder]
            [teod.subcons.transform :as transform]
            [hawk.core :as hawk]))

(defn edn-paths []
  ["index.edn"
   "theory-meaning/index.edn"
   "doomemacs-journey/index.edn"
   "sp/index.edn"
   "ast/index.edn"
   "crazy-todo/index.edn"])

(defn build-all
  "One-off - build EDN files."
  [_opts]
  (doseq [edn-path (edn-paths)]
    (println "building" edn-path "...")
    (try
      (let [edn (-> edn-path slurp edn/read-string
                    (vary-meta assoc :eu.teod.subcons/source-path edn-path))]
        (builder/builder edn)
        (println "Done."))
      (catch Throwable t
        (println "Faied!")
        (clojure.stacktrace/print-stack-trace t)))
    (println "All done.")))



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

(defn watch-rebuild-edn-handler [_ctx e]
  (let [{:keys [_kind file]} e]
    (try
      (let [edn-path (.getPath file)
            _ (println "building" edn-path "...")
            edn (-> edn-path slurp edn/read-string
                    (vary-meta assoc :eu.teod.subcons/source-path edn-path)
                    transform/transform)]
        (builder/builder edn)
        (println "Done."))
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

  (build-all {})

  (meta
   (-> (edn-paths)
       first slurp edn/read-string))

  (-> ["teodor"]
      (with-meta  {:teod/id "teod"})
      (vary-meta assoc :teod/count 99)
      meta)
  )
