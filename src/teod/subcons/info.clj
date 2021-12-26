(ns teod.subcons.info)

;; information protocol for EDN object metadata on teod.subcons

(comment
  ;; Example of metadata-enriched EDN
  ^{:teod.subcons/source-path "teod.edn"
    :teod.subcons/transformers ['teod.subcons.sci/eval]
    :teod.subcons/filewriter :teod.subcons.filewriter/hiccup-html}
  [:html
   [:name "Teodor"]
   [:age (+ 1 2 3 25)]]

  ;; The site generator can process that EDN to this HTML, written to the file
  ;; "teod.html":
  ;;
  ;; <html>
  ;;   <name>Teodor</html>
  ;;   <age>31</age>
  ;; </html>

  )

(defn source-path [edn]
  (:teod.subcons/source-path (meta edn)))

(defn set-source-path [edn source-path]
  (vary-meta edn assoc :teod.subcons/source-path source-path))

(defn transformers [edn]
  (:teod.subcons/transformers (meta edn)
                              []))

(defn set-transformers [edn transformers]
  (vary-meta edn assoc :teod.subcons/transformers transformers))

(defn filewriter [edn]
  (:teod.subcons/filewriter (meta edn)))

(defn set-filewriter [edn filewriter]
  (vary-meta edn assoc :teod.subcons/filewriter filewriter))
