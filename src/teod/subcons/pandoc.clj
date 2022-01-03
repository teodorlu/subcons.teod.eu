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
;;
;; I'm thinking that a clojure.walk/prewalk / postwalk with a multimethod
;; dispatching on :t might work.


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


(comment
  ;; big example

  ;; org
  ;;
  ;; * okay
  ;;
  ;; - are you okay?
  ;; - anything else?

  ;; from markdown
  (-parse {:source "# okay

- are you okay?
- anything else?"
           :format "markdown"})

  ;; from org
  (-parse {:source "* okay

- are you okay?
- anything else?"
         :format "org"})

  ;; into pandoc
  [{:t "Header", :c [1 ["okay" [] []] [{:t "Str", :c "okay"}]]}
   {:t "BulletList",
    :c
    [[{:t "Plain",
       :c
       [{:t "Str", :c "are"}
        {:t "Space"}
        {:t "Str", :c "you"}
        {:t "Space"}
        {:t "Str", :c "okay?"}]}]
     [{:t "Plain",
       :c [{:t "Str", :c "anything"} {:t "Space"} {:t "Str", :c "else?"}]}]]}]

  ;; into hiccup
  [:div
   [:h1 "okay"]
   [:ul
    [:ol "are you okay?"]
    [:ol "anything else?"]]]

  )
