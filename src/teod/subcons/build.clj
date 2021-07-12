(ns teod.subcons.build
  (:require [hiccup.core]
            [clojure.edn :as edn]
            [clojure.string :as string]))

;; Idea: hit a SINGLE use case in a good way first. That means finding
;; theory-meaning/index.edn, and transforming it to index.html.

(defn edn-paths []
  ["theory-meaning/index.edn"])

(defn build-edn [path]
  (let [edn (-> path slurp edn/read-string)
        html (hiccup.core/html edn)
        html-path (string/replace path #".edn$" ".html")]
    (spit html-path
          (str "<!doctype html>"
               "\n"
               html))))

(comment
  (let [path "theory-meaning/index.edn"]
    (string/replace path #".edn$" ".html")))

(defn build-all [_opts]
  (doseq [path (edn-paths)]
    (print "building" path "...")
    (build-edn path)
    (println " Done.")))


(comment
  (build-all {})
  )
