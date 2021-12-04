(ns user)

(defn watch!! []
  (let [clerk-serve (requiring-resolve 'nextjournal.clerk/serve!)]
    (clerk-serve {:browse? true
                  :watch-paths ["autodiff"]})))
