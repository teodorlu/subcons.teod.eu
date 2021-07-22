(ns teod.subcons.transform
  (:require [clojure.walk]))

;; edn -> edn rewriting - Avoid tedious HTML, if required.

;; A transform is a walk - regardless of what we believe.


;; Suggestions on idiomatic solutions to "splice" into a vector when it's not
;; macro-time?

;; I'm trying to write a function that behaves like this:

(defn dl [_ & args]
  (into [:dl]
        (mapcat (fn [[dt dd]]
                  (list
                   [:dt dt]
                   [:dd dd]))
                (partition 2 args))))

(defn transform [hiccup]
  (clojure.walk/postwalk (fn [form]
                           (cond (and (seqable? form)
                                      (= 'dl (first form)))
                                 (apply dl form)
                                 :else form))
                         hiccup))

(transform [:html ['dl "relative" "relative stuff"
                   "absolute" "absolute stuff"]])
