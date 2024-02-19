(ns electric-starter-app.main
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]))

(e/def !user (atom {:name "World"}))
(e/def user (e/server (e/watch !user)))

; must use def e/def doesn't seem to work here
;;(e/def !force-render (atom 0))
(def !force-render (atom 0))

(e/defn Login []
  (e/client
    (ui/button
      (e/fn []
        (e/client
          (let [token (rand-int 100)]
            (println "Login with token " token)
            (e/server
              (prn "Validating token" token)
              (let [username (str "User-" token)]
                (reset! !user  {:name username})
                (println "updated: " @!user))
              (e/client (swap! !force-render inc))))))
      (dom/text "Login as random user"))))

(e/defn SaveButton []
  (e/client
    (ui/button
      (e/fn []
        (println "saved data. client: " user " server: " (e/server user))
        (e/server
          (println "save data as " user)))
      (dom/text "Save or something")))
  )

(e/defn Greeting []
  (e/client
    (dom/div
      (dom/p (dom/text "Hello, " (e/server (:name user))))
      (dom/p (dom/text (e/watch !force-render))))))

(e/defn Main [ring-request]
  (e/client
    (e/server
      (reset! !user {:name "Electric"}))
    (binding [dom/node js/document.body]
      (dom/h1 (dom/text "Hello from Electric Clojure"))
      (Greeting.)
      (Login.)
      (SaveButton.)
      )))
