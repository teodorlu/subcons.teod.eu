(ns teod.subcons.build
  (:require [clojure.edn :as edn]
            [clojure.stacktrace]
            [teod.subcons.filewriter :as writer]
            [hawk.core :as hawk]
            [teod.subcons.info :as info]
            [teod.subcons.transformers :as transformers]))

(defn index-edn? [ctx {:keys [file] :as e}]
  (and (hawk/file? ctx e)
       (#{"index.edn" #_ "simple.edn"}
          (.getName file))))

(defn watch-rebuild-edn-handler
  [_ctx e]
  (let [{:keys [_kind file]} e]
    (try
      (let [edn-path (.getPath file)
            _ (print "building" edn-path "...")
            edn (-> edn-path
                    slurp
                    edn/read-string
                    (info/set-source-path edn-path)
                    transformers/transform)]
        (writer/write edn)
        (println " done."))
      (catch Throwable t
        (println "Faied!")
        (clojure.stacktrace/print-stack-trace t)))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn watch!
  "Look for changes to EDN files; then try to rebuild.

  Called directly from build system."
  [_opts]
  (println "Watching and rebuilding index.edn files")
  (hawk/watch! [{:paths ["."]
                 :filter #'index-edn?
                 :handler #'watch-rebuild-edn-handler}]))
