(ns teod.subcons.sci
  (:refer-clojure :exclude [eval])
  (:require [sci.core :as sci]))

(defn eval [form]
  (sci/eval-form (sci/init {}) form))
