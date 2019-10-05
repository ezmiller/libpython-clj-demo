(ns python-clj-demo
  (:require [libpython-clj.python :as py]
            [clojure.pprint :refer [pprint]]))

(py/initialize!)

;; Execute some python directly
(py/run-simple-string "msg = 'hello world'")
(py/run-simple-string "print(\"hello\")")

;; More common use. Using a module. Let's say numpy.
(def numpy (py/import-module "numpy"))
(py/att-type-map numpy)

;; So now we can use numpy functions
(def test-array (py/call-attr numpy "ones" [2,3]))
(py/call-attr numpy "shape" test-array)

;; We can use clojure's pprint on these python object!
(type test-array)
(pprint test-array)

;; Works the other way too! We can run python's pprint on a clojure data structure
(def pprint-module (py/import-module "pprint"))
(py/call-attr pprint-module "pprint" [[1 1 1]
                                      [1 1 1]])
