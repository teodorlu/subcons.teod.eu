(ns teod.subcons.build
  (:require [hiccup.core]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.stacktrace]))

(defn edn-paths []
  ["theory-meaning/index.edn"])

(defn hiccup-html
  "Write HTML from EDN with Hiccup"
  [edn edn-path]
  (let [html (hiccup.core/html edn)
        html-path (string/replace edn-path #".edn$" ".html")]
    (spit html-path
          (str "<!doctype html>"
               "\n"
               html))))

(defn build-all [_opts]
  (doseq [edn-path (edn-paths)]
    (print "building" edn-path "...")
    (try
      (let [edn (-> edn-path slurp edn/read-string)]
        (hiccup-html edn edn-path)
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

  )
