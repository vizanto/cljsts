(ns data
  #?(:cljs (:require-macros [define]))
  (:require
   [define :refer [defx defnx]]
   [malli.core :as m]))

(def IBAN (m/schema string?))
(def myUUID (m/schema string?))

;; (def Event )

(defx schema {:a :b})

(defnx check-something
  "@param {!string} x"
  ([x] x)
  ([x y] (+ x y)))

(defnx check-something2
  "@param {!string} x"
  [x] x)

(def valid-IBAN (m/validator IBAN))


;(def exports #js {:check_something check-something :schema schema :foo 123})

; (def exports
;   {:check_something check-something
;    :schema schema})

;(defschema 'data)

(println (check-something "checked!"))
;;






(def flow-registry)

(def registry
  (merge m/comparator-registry m/base-registry
         {:int    (m/fn-schema "int" int?)
          :number (m/fn-schema "number" number?)
          :string (m/fn-schema "string" string?)
          :UUID   (m/fn-schema "UUID" uuid?)
          :URI    (m/fn-schema "URI" uri?)}))



(def EDN-DataModel "Flow's data model"
  {:Amount [:map
            [:currency [:enum "EUR"]]
            [:value [:number]]]})

(m/schema (-> EDN-DataModel :Amount) {:registry registry})

(defn validate [type input]
  (m/validate
   (get EDN-DataModel type)
   #?(:cljs (js->clj input :keywordize-keys true)
      :clj  input)
   {:registry registry}))

;(validate :Amount #js {:currency "EUR" :value 1.02})

(defn to-Type [type input]
  (m/validate (get EDN-DataModel type) input {:registry registry}))


#?(:cljs (defnx ^boolean validate-Amount [input]
           (validate :Amount input)))

#?(:cljs (defnx ^Amount to-Amount [input] (validate :Amount input)))

(defrecord testrec [a ^string bxxx c])

(defnx testfn [] (->testrec 1 2 3))


;; (specify! )

;;;;;;;;

;;;;;;;;

#?(:cljs (def exports (define/export)))
