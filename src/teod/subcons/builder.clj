(ns teod.subcons.builder
  "A builder takes EDN and produces an output file."
  (:require [hiccup.core]
            [clojure.string :as string]))

(defmulti builder (fn [edn]
                    (-> edn meta :teod.subcons/builder)))

(defmethod builder :teod.subcons.builder/hiccup-html [edn]
  (let [edn-path (-> edn meta :teod.subcons/source-path)
        html (hiccup.core/html edn)
        html-path (string/replace edn-path #"\.edn$" ".html")]
    (assert (not= edn-path html-path) "Conflict: same input and output path.")
    (spit html-path
          (str "<!doctype html>"
               "\n"
               html))))

(defmethod builder :default [edn]
  (println "no builder registered for" (-> edn meta :teod.subcons/builder)))

(comment
  ;; multimethods can be a hassle in the REPL - just onload the whole ns.
  (remove-ns (-> *ns* str symbol))

  (string/replace "index.edn" #"\.edn$" ".html")
  )
