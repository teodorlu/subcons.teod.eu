(ns teod.subcons.pandoc
  (:require [clojure.java.shell :refer [with-sh-dir sh]]
            [clojure.data.json :as json]))

(defn -parse [{:keys [source format]}]
  (assert source)
  (assert format)
  (assert (#{"markdown"} format)) ; whitelist - avoid errors
  (-> (sh "pandoc" "-s" "-f" format "-t" "json" :in source)
      :out
      json/read-str
      (get "blocks")))

;; At some point, the output of -parse needs to become hiccup.

(-parse {:source "# okay

- are you okay?
- sure?"
         :format "markdown"})
;; => [{"t" "Header", "c" [1 ["okay" [] []] [{"t" "Str", "c" "okay"}]]}
;;     {"t" "BulletList",
;;      "c"
;;      [[{"t" "Plain",
;;         "c"
;;         [{"t" "Str", "c" "are"}
;;          {"t" "Space"}
;;          {"t" "Str", "c" "you"}
;;          {"t" "Space"}
;;          {"t" "Str", "c" "okay?"}]}]
;;       [{"t" "Plain", "c" [{"t" "Str", "c" "sure?"}]}]]}]
