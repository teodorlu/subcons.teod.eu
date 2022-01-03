(ns user)

(defonce state (atom {}))

(defn dev! []
  (let [watch! (requiring-resolve 'teod.subcons.build/watch!)
        watch-stop! (requiring-resolve 'teod.subcons.build/stop!)
        portal-open (requiring-resolve 'teod.subcons.portal/open)]
    (swap! state (fn [{:keys [watcher portal]}]
                   (when watcher
                     (watch-stop! watcher))
                   {:watcher (watch! {})
                    :portal (if portal
                              portal
                              (portal-open))}))))
