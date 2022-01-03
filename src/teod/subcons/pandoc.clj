(ns teod.subcons.pandoc
  (:require
   [clojure.data.json :as json]
   [clojure.java.shell :refer [sh]]
   [clojure.walk :refer [postwalk]]
   [clojure.string :as str]))

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

(org-> "some text")

(do
  (defn ->hiccup [data]
    (into [:div]
          (postwalk (fn [el]
                      (cond
                        (= "Header" (:t el))
                        el

                        (= "Str" (:t el))
                        (:c el)

                        (= "Space" (:t el))
                        " "

                        (= "Para" (:t el))
                        (into [:p
                               (if (every? str (:c el))
                                 (str/join "" (:c el))
                                 (:c el))])

                        (= "Plain" (:t el))
                        (into [:span
                               (if (every? str (:c el))
                                 (str/join "" (:c el))
                                 (:c el))])

                        (= "BulletList" (:t el))
                        (into [:ul]
                              (for [li (:c el)]
                                 (into [:li] li)))

                        ;; todo - return nil (aka ignore) when we go into production
                        :else el
                        )
                      )
                    data)))
  (-> "- item 1\n- item 2"
      org->
      ->hiccup)


  #_
  (-> "some text"
      org->
      ->hiccup)
  )

(comment

  (org-> "* my header")
  ;; => [{:t "Header",
  ;;      :c
  ;;      [1
  ;;       ["my-header" [] []]
  ;;       [{:t "Str", :c "my"} {:t "Space"} {:t "Str", :c "header"}]]}]

  (org-> "** my header")
  ;; => [{:t "Header",
  ;;      :c
  ;;      [2
  ;;       ["my-header" [] []]
  ;;       [{:t "Str", :c "my"} {:t "Space"} {:t "Str", :c "header"}]]}]
  (org-> "*** my header")
  ;; => [{:t "Header",
  ;;      :c
  ;;      [3
  ;;       ["my-header" [] []]
  ;;       [{:t "Str", :c "my"} {:t "Space"} {:t "Str", :c "header"}]]}]
  )

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
