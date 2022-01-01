(ns teod.subcons.portal
  "Interface to https://github.com/djblue/portal"
  (:require
   [portal.api :as p]))

(add-tap #'p/submit)

(defn open
  []
  (p/close)
  (p/open))

(defn clear []
  (p/clear))

(defn close []
  (p/close))
