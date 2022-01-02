(ns user)

(defn dev! []
  (let [watch! (requiring-resolve 'teod.subcons.build/watch!)]
    (watch! {}))
  (let [portal-open (requiring-resolve 'teod.subcons.portal/open)]
    (portal-open)))
