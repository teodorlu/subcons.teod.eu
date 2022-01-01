(ns user)

(defn dev! []
  (let [watch! (requiring-resolve 'teod.subcons.build/watch!)]
    (watch! {}))
  (let [portal-start (requiring-resolve 'teod.subcons.portal/start)]
    (portal-start)))
