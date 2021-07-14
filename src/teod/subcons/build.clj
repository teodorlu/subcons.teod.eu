(ns teod.subcons.build
  (:require [clojure.edn :as edn]
            [clojure.stacktrace]
            [teod.subcons.builder :as builder]))

(defn edn-paths []
  ["index.edn"
   "theory-meaning/index.edn"
   "doomemacs-journey/index.edn"
   "sp/index.edn"])

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

;; TODO
(defn watch-rebuild
  "Look for changes to EDN files; then try to rebuild."
  [_opts])

;; TODO
(defn watch-stop!
  "Stop all watchers."
  [])

(comment
  (build-all {})

  (meta
   (-> (edn-paths)
       first slurp edn/read-string))

  (-> ["teodor"]
      (with-meta  {:teod/id "teod"})
      (vary-meta assoc :teod/count 99)
      meta)

  )
