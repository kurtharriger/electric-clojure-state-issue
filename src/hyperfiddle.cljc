(ns hyperfiddle
  (:require [clojure.spec.alpha :as s]))

; See discussion of single-segment namespaces here
; https://github.com/bbatsov/clojure-style-guide/pull/100
; Clojure is fine if no gen-class
; ClojureScript compiler warns
; Just use hyperfiddle.api :as hf, for specs as well as fns,
; it's a little ugly but with the require alias it works out.

(s/def ::domain some?)
(s/def ::user-version string?)

; public api here, one API ns
; internal specs anywhere