(ns define
  (:require
   [clojure.string :as str]
   [clojure.tools.macro :as macro]
   [malli.core :as m]
   [malli.transform :as mt]))

(def exports (atom []))

(defn ts-type [arg]
  (println "Type?" arg (meta arg))
  (-> arg (meta) :tag (or "any")))

(defn ts-name:type [arg]
  (str arg ": " (ts-type arg)))

(defn ts-fn-args [name args]
  ; (println "ts-fn-args" name args)
  (let [[singles [_&_ vararg]] (split-with #(not= '& %1) args)]
    (str "export function " (munge name) " (" (str/join ", " (map ts-name:type singles)) "): " (ts-type name) ";\n")))

(defn typescript [[type name body]]
  ; (println "typescript" type name body)
  (case type
    :fn  body
    :def (str "export const " (munge name) ": " (ts-type name) ";\n")))

;; API

(defmacro export []
  (binding [*print-meta* true] (prn &env))
  (let [form `(let [~'object (js/Object)]
               ~@(map (fn [[t k v]] `(set! (.. ~'object ~(symbol (str "-" k)))
                                           (cljs.core/clj->js ~k)))
                      @exports)
               ~'object)]
    (println "       exporting " @exports)
    ; (println "        form:    " form)
    (spit "./lib/index.d.ts" (apply str (map typescript @exports)))
    (swap! exports (fn [_] []))
    form))

(defmacro defx [name & body]
  (swap! exports conj [:def name])
  `(def ^:export ~name ~@body))

(defmacro defnx [name & body]
  (let [[name bodies] (macro/name-with-attributes name body)
        bodies (if (vector? (first bodies)) (list bodies) #_else bodies) ; single to 1 multi arity
        typed-args (->> bodies (map #(ts-fn-args name (first %))) (str/join ""))]
    (swap! exports conj [:fn name (str "\n" typed-args)])
    `(defn ^:export ~name ~@body)))
