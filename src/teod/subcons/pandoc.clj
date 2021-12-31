(ns teod.subcons.pandoc
  (:require
   [clojure.data.json :as json]
   [clojure.java.shell :refer [sh]]))

(defn -parse [{:keys [source format]}]
  (assert source)
  (assert format)
  (assert (#{"markdown" "org"} format)) ; whitelist - avoid errors
  (-> (sh "pandoc" "-f" format "-t" "json" :in source)
      :out
      (json/read-str :key-fn keyword)
      :blocks))

;; At some point, the output of -parse needs to become hiccup.
;;
;; Any advice on how to approach this? Just do it by hand with clojure.walk? Not
;; sure if it's a plain mapping (dispatch on :t) or if I'm going to have to
;; "join" elements. Like how pandoc splits on space.

(-parse {:source "# okay

- are you okay?
- sure?"
         :format "markdown"})
;; => [{:t "Header", :c [1 ["okay" [] []] [{:t "Str", :c "okay"}]]}
;;     {:t "BulletList",
;;      :c
;;      [[{:t "Plain",
;;         :c
;;         [{:t "Str", :c "are"}
;;          {:t "Space"}
;;          {:t "Str", :c "you"}
;;          {:t "Space"}
;;          {:t "Str", :c "okay?"}]}]
;;       [{:t "Plain", :c [{:t "Str", :c "sure?"}]}]]}]

(-parse {:source "* okay

- are you okay?
- sure?"
         :format "org"})
;; => [{:t "Header", :c [1 ["okay" [] []] [{:t "Str", :c "okay"}]]}
;;     {:t "BulletList",
;;      :c
;;      [[{:t "Plain",
;;         :c
;;         [{:t "Str", :c "are"}
;;          {:t "Space"}
;;          {:t "Str", :c "you"}
;;          {:t "Space"}
;;          {:t "Str", :c "okay?"}]}]
;;       [{:t "Plain", :c [{:t "Str", :c "sure?"}]}]]}]

(defn org-> [source]
  (-parse {:source source
           :format "org"}))

(defn markdown-> [source]
  (-parse {:source source
           :format "markdown"}))

(comment
  (assert (= (org-> "* head
** subhead
Hello!")
  (markdown-> "# head
## subhead
Hello!")))

  )

;; how do I want to fail? Could perhaps tap> to portal?
