(ns teod.subcons.transformers
  (:refer-clojure :exclude [eval])
  (:require [teod.subcons.sci]
            [teod.subcons.info :as info]))

(defn transform [edn]
    (let [transformers (info/transformers edn)
          edn-nometa (with-meta edn {})]
      (reduce (fn [val trans]
                (if-let [f (requiring-resolve trans)]
                  (f val)
                  (do
                    (println "Error transforming EDN ")

                    val)))
              edn-nometa
              transformers)))

(comment
  ;; mechanism for hooking into arbitrary functions via metadata
  (transform ^{:teod.subcons/transformers ['teod.subcons.sci/eval]}
             {:name "Teodor"
              :level '(+ 2 9000)})
  ;; => {:name "Teodor", :level 9002}
  )
