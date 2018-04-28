(ns microblog.frontend.middleware)

(defn remove-trailing-slashes [handler]
  (fn [req]
    (let [uri (:uri req)
          not-root? (not= uri "/")
          ends-with-slash? (.endsWith ^String uri "/")
          fixed-uri (if (and not-root?
                             ends-with-slash?)
                      (subs uri 0 (dec (count uri)))
                      uri)
          fixed-req (assoc req :uri fixed-uri)]
      (handler fixed-req))))
