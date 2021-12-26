(ns teod.subcons.builder
  "A builder takes EDN and produces an output file."
  (:require [hiccup2.core]
            [clojure.string :as str]))

;; 2021-12-26 - Hunch that this does not deserve its own namespace.
;;
;; Right now - there's a multimethod with a single implementation.

(defmulti builder (fn [edn]
                    (-> edn meta :teod.subcons/builder)))

(defmethod builder :teod.subcons.builder/hiccup-html [edn]
  (let [edn-path (-> edn meta :teod.subcons/source-path)
        html (hiccup2.core/html edn)
        html-path (str/replace edn-path #"\.edn$" ".html")]
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

  (str/replace "index.edn" #"\.edn$" ".html")
  )
