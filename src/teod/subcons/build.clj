(ns teod.subcons.build
  (:require [hiccup.core]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.stacktrace]))

(defn edn-paths []
  ["theory-meaning/index.edn"])

(defn hiccup-html
  "Write HTML from EDN with Hiccup"
  [edn]
  (let [edn-path (-> edn meta :eu.teod.subcons/source-path)
        html (hiccup.core/html edn)
        html-path (string/replace edn-path #".edn$" ".html")]
    (prn "got meta" (meta edn))
    (spit html-path
          (str "<!doctype html>"
               "\n"
               html))))

(defn build-all [_opts]
  (doseq [edn-path (edn-paths)]
    (print "building" edn-path "...")
    (try
      (let [edn (-> edn-path slurp edn/read-string
                    (vary-meta assoc :eu.teod.subcons/source-path edn-path))]
        (hiccup-html edn)
        (println " done."))
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
