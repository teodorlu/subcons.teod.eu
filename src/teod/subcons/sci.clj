(ns teod.subcons.sci
  (:refer-clojure :exclude [eval])
  (:require [sci.core :as sci]))

(defn ctx []
  (sci/init {:namespaces {'teod {'link (fn [x] [:a {:href x} x] )}}}))

(defn eval [form]
  (sci/eval-form (ctx)
                 form))
