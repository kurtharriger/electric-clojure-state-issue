 (ns electric-starter-app.main
   (:require [hyperfiddle.electric :as e]
             [hyperfiddle.electric-dom2 :as dom]
             [hyperfiddle.electric-ui4 :as ui4]))

(defn empty->nil [x]
  (if (empty? x) nil x))

#?(:clj (defonce !msgs (atom (list))))
(e/def msgs (e/server (reverse (e/watch !msgs))))

#?(:clj (defonce !present (atom {}))) ; session-id -> user
(e/def present (e/server (e/watch !present)))

; the above atoms are defonce and shared.
; I want an atom that is per-uer not shared
(e/def !user (e/server (atom "anon")))
(e/def user (e/server (e/watch !user)))

(e/defn Chat-UI [username]
  (e/client
    (dom/div (dom/text "Present: "))
    (dom/ul
      (e/server
        (e/for-by first [[session-id username] present]
          (e/client
            (dom/li (dom/text username (str " (session-id: " session-id ")")))))))

    (dom/hr)
    (dom/ul
      (e/server
        (e/for [{:keys [::username ::msg]} msgs]
          (e/client
            (dom/li (dom/strong (dom/text username))
              (dom/text " " msg))))))

    (dom/input
      (dom/props {:placeholder "Type a message" :maxlength 100})
      (dom/on "keydown" (e/fn [e]
                          (when (= "Enter" (.-key e))
                            (when-some [v (empty->nil (.substr (.. e -target -value) 0 100))]
                              (dom/style {:background-color "yellow"}) ; loading
                              (e/server
                                (swap! !msgs #(cons {::username @!user ::msg v}
                                                (take 9 %))))
                              (set! (.-value dom/node) ""))))))))

(e/defn ChatExtended []
  (e/client
    (let [session-id
          (e/server (get-in e/http-request [:headers "sec-websocket-key"]))
          ;username (e/server (get-in e/http-request [:cookies "username" :value]))]
          username (e/server (e/watch !user))]
      (do ; if-not (some? username)
        (dom/div
          ;(dom/text "Set login cookie here: ")
          ;(dom/a (dom/props {::dom/href "/auth"}) (dom/text "/auth"))
          ;(dom/text " (blank password)"))
          (ui4/button (e/fn []
                       (e/server
                         (reset! !user (str "user-" (rand-int 1000)))
                         (println "user is now " @!user)))
            (dom/text "Login as random user")))
        (do
          (e/server
            (swap! !present assoc session-id username)
            (e/on-unmount #(swap! !present dissoc session-id)))
          (dom/div (dom/text "Authenticated as: " username))
          (Chat-UI. username))))))
(e/defn Main [http-request]
  (e/server
    (binding [e/http-request (assoc http-request ::user (atom "anon"))]
      (e/client
        (binding [dom/node js/document.body]
        (ChatExtended.))))))
