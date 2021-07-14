(ns teod.subcons.build
  (:require [clojure.edn :as edn]
            [clojure.stacktrace]
            [teod.subcons.builder :as builder]))

(defn edn-paths []
  ["theory-meaning/index.edn"])

(defn build-all [_opts]
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
