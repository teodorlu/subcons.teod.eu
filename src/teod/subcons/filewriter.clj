(ns teod.subcons.filewriter
  "A builder takes EDN and produces an output file.

  EDN source files pick their builder via metadata."
  (:require [hiccup2.core]
            [clojure.string :as str]))

;;; builder is a bad word. To wide. Can we do better?
;;
;; There are different kinds of things:
;;
;;  - File producers (like this one)
;;  - Data transformers (like the EDN thing)
;;
;; That a file producer is a builder is perhaps obvious.

(defmulti write (fn [edn]
                    (-> edn meta :teod.subcons/builder)))

;; Currently - the only builder we have takes Hiccup (EDN file) and produces an
;; HTML file.

(defmethod write :teod.subcons.builder/hiccup-html [edn]
  (let [edn-path (-> edn meta :teod.subcons/source-path)
        html (hiccup2.core/html edn)
        html-path (str/replace edn-path #"\.edn$" ".html")]
    (assert (not= edn-path html-path) "Conflict: same input and output path.")
    (spit html-path
          (str "<!doctype html>"
               "\n"
               html))))

(defmethod write :default [edn]
  (println "no builder registered for" (-> edn meta :teod.subcons/builder))
  (println " => no action taken."))

(comment
  ;; multimethods can be a hassle in the REPL - just onload the whole ns.
  (remove-ns (-> *ns* str symbol))

  (str/replace "index.edn" #"\.edn$" ".html")
  )
