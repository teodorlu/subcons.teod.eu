(ns teod.subcons.transformers
  (:refer-clojure :exclude [eval])
  (:require [teod.subcons.sci]
            [teod.subcons.info :as info]))

(defn transform [edn]
  (let [transformers (info/transformers edn)
        edn-nometa (with-meta edn {})
        transformed (reduce (fn [val trans]
                              (if-let [f (requiring-resolve trans)]
                                ;; should I provide more info here?
                                ;;
                                ;; hmm, not sure ... the whole thing goes into a
                                ;; SCI env
                                ;;
                                ;; We need something on the outside, I think
                                ;;
                                ;; Something passed to the transformers, then
                                ;; passed further to SCI powered functions.
                                (f val)
                                (do
                                  (println "Error transforming EDN: " (info/source-path edn))
                                  val)))
                            edn-nometa
                            transformers)]
    ;; Save the metadata -- append only information model!
    (with-meta transformed (meta edn))))

(comment
  ;; mechanism for hooking into arbitrary functions via metadata
  (transform ^{:teod.subcons/transformers ['teod.subcons.sci/eval]}
             {:name "Teodor"
              :level '(+ 2 9000)})
  ;; => {:name "Teodor", :level 9002}
  )
