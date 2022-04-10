(ns teod.pandoc-test
  (:require [teod.pandoc :as sut]
            [clojure.test :as t]))

;; First some proper tests

(t/deftest org->hiccup-test
  ;; Are er skikkelig fin -- den lar oss skrive par med input og output, uten å
  ;; styre med is, = funksjonskall og sånn. Anbefalt!
  (t/are [pandoc hiccup] (= hiccup
                            (sut/->hiccup* pandoc {}))
    {:t "Para", :c [{:t "Str", :c "Paragraph"}]}
    [:p "Paragraph"]

    {:t "Space"}
    " "

    {:t "Str", :c "A plain string"}
    "A plain string"

    {:t "Emph", :c [{:t "Str", :c "two"}]}
    [:em "two"]

    {:t "Para",
     :c
     [{:t "Str", :c "Para"}
      {:t "Space"}
      {:t "Emph", :c [{:t "Str", :c "two"}]}
      {:t "Str", :c "."}]}
    [:p "Para" " " [:em "two"] "."]

    {:t "BulletList",
     :c
     [[{:t "Plain", :c [{:t "Str", :c "Item"}]}]
      [{:t "Plain",
        :c [{:t "Str", :c "Another"} {:t "Space"} {:t "Str", :c "item"}]}]]}
    [:ul
     [:li [:span "Item"]]
     [:li [:span "Another" " " "item"]]]
    ))

;; Then some improper tests 😈

(def org-> sut/org->)
(def ->hiccup sut/->hiccup)

(comment
  (-> "* header\nBody goes here"
      org->
      ->hiccup)

  (org-> "* head /more/")
  (-> "- item 1\n- item 2"
      org->
      ->hiccup)
  (-> "some text"
      org->
      ->hiccup)
  )

(defn org->hiccup [data]
  (-> data org-> ->hiccup))

(def markdown-> sut/markdown->)

(defn markdown->hiccup [data]
  (-> data markdown-> ->hiccup))

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

(def -parse sut/-parse)

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

  (org->hiccup "* okay

- are you okay?
- anything else?")

  ;; into hiccup
  [:div
   [:h1 "okay"]
   [:ul
    [:ol "are you okay?"]
    [:ol "anything else?"]]]

  (org->hiccup "
#+TITLE: Balancing outcome orientation and bottom-up play

* Outcome orientation distributes intent to teams
")

  (org-> "#+TITLE: Balancing\n\n* Outcome orientation")

  (let [;; hard-code some deps
        sh (requiring-resolve 'clojure.java.shell/sh)
        json-read-str (requiring-resolve 'clojure.data.json/read-str)

        source "#+TITLE: Balancing outcome and process\n\n* Outcome orientation"
        format "org"]
    (-> (sh "pandoc" "-f" format "-t" "json" :in source)
        :out
        (json-read-str :key-fn keyword)
        :meta))
  ;; => {:title
  ;;     {:t "MetaInlines",
  ;;      :c
  ;;      [{:t "Str", :c "Balancing"}
  ;;       {:t "Space"}
  ;;       {:t "Str", :c "outcome"}
  ;;       {:t "Space"}
  ;;       {:t "Str", :c "and"}
  ;;       {:t "Space"}
  ;;       {:t "Str", :c "process"}]}}

  )
