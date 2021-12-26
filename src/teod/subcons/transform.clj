(ns teod.subcons.transform
  (:require [clojure.walk]))

;; edn -> edn rewriting - Avoid tedious HTML, if required.
;;
;; 2021-12-26 evaluation - this is probably not useful, and surprising. Opt-in
;; functionality is better than magic.

(defn dl
  "Abstraction for HTML definition lists - <dl></dl>"
  [_ & args]
  (into [:dl]
        (mapcat (fn [[dt dd]]
                  (list
                   [:dt dt]
                   [:dd dd]))
                (partition 2 args))))

;;
(defn transform [hiccup]
  (clojure.walk/postwalk (fn [form]
                           (cond (and (seqable? form)
                                      (= 'dl (first form)))
                                 (apply dl form)
                                 :else form))
                         hiccup))
