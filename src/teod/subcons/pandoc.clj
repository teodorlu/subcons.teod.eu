(ns teod.subcons.pandoc
  (:require [clojure.java.shell :refer [with-sh-dir sh]]))

(with-sh-dir "doc"
  (sh "ls"))

(with-sh-dir "doc"
  (sh "pandoc" "-h"))

(defn -parse [{:keys [source format]}]
  (assert source)
  (assert format)
  (assert (#{"markdown"} format)) ; whitelist - avoid errors
  (sh "pandoc" "-s" "-f" format "-t" "json" :in source))

(-parse {:source "# okay\n\n- are you okay?\n- sure?"
         :format "markdown"})

(sh "pandoc" "-s" "-f" "markdown" "-t" "json"
    :in "# hello

- Are you okay?")

(defn parse-org
  ""
  [])

(defn parse-md [])

(comment
  ;; draft

  ;; something that matches Pandoc's CLI
  ;;
  ;; perhaps I should start out exploring it myself ...
  )
