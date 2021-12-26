(ns teod.subcons.transformers
  (:refer-clojure :exclude [eval])
  (:require [teod.subcons.sci]))

(do

  (defn transform [edn]
    (let [transformers (:teod.subcons/transformers (meta edn)
                                                    [])
          edn-nometa (with-meta edn {})]
      (reduce (fn [val trans]
                (if-let [f (requiring-resolve trans)]
                  (f val)
                  val))
              edn-nometa
              transformers)))

  (transform ^{:teod.subcons/transformers ['teod.subcons.sci/eval]}
             {:name "Teodor"
              :level '(+ 2 9000)})

  #_
  (sci-eval {:name "Teodor"
             :level '(+ 2 9000)})

  )

(let [f (requiring-resolve 'teod.subcons.sci/eval)]
  (f {:name "Teodor"
      :level '(+ 2 9000)})
  )

(let [obj ^{:teod.subcons/transformers ['teod.subcons.sci/eval]}
      {:name "Teodor"
       :level '(+ 2 9000)}]
  (meta obj))
