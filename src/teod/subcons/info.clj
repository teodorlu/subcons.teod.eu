(ns teod.subcons.info)

;; information protocol for EDN object metadata on teod.subcons

(defn source-path [edn]
  (:teod.subcons/source-path (meta edn)))

(defn set-source-path [edn source-path]
  (vary-meta assoc edn :teod.subcons/source-path source-path))

(defn transformers [edn]
  (:teod.subcons/transformers (meta edn)))

(defn set-transformers [edn transformers]
  (vary-meta assoc edn :teod.subcons/transformers transformers))
