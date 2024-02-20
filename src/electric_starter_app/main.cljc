(ns electric-starter-app.main
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui4]))

;; Saving this file will automatically recompile and update in your browser

(e/defn say-hello [user]
  (e/client
    (.alert js/window (str "Hello, " user) )))

(e/defn Main [ring-request]
  (e/client
    (binding [dom/node js/document.body]
      (dom/h1 (dom/text "Hello from Electric Clojure"))
      ;; (ui4/button (partial say-hello "Bob") (dom/text "Bob")  )
      ;; (ui4/button (partial say-hello "Alice") (dom/text "Alice"))
      ;; inline implementation works
      (dom/h1 (dom/text "Inline "))
      (ui4/button
        (e/fn [] (.alert js/window "Hello, Bob"))
        (dom/text "Bob"))
      (ui4/button
        (e/fn [] (.alert js/window "Hello, Alice"))
        (dom/text "Alice"))

      (dom/h1 (dom/text "Trivial function"))
      (ui4/button
        (e/fn [] (say-hello "Bob"))
        (dom/text "Bob"))
      (ui4/button
        (e/fn [] (say-hello "Alice"))
        (dom/text "Alice"))

      )))
