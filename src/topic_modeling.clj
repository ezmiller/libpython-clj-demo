(ns topic-modeling
  (:require [libpython-clj.python :as py]
            [panthera.panthera :as pt]
            [panthera.numpy :as np]
            [panthera.pandas.utils :as u]
            [clojure.pprint :refer [pprint print-table]]))

(py/initialize!)

;; Let's get some test data
(def categories ["comp.graphics",
                 "rec.autos",
                 "rec.motorcycles",
                 "rec.sport.baseball",
                 "rec.sport.hockey",
                 "sci.electronics",
                 "talk.politics.mideast",
                 "talk.politics.misc",
                 "talk.religion.misc",
                 "alt.atheism"])

(def sklearn-datasets (py/import-module "sklearn.datasets"))
(def dataset (py/call-attr-kw sklearn-datasets "fetch_20newsgroups"
                              nil
                              {:shuffle true
                               :random_state 1
                               :remove ["headers" "footers" "quotes"]
                               :categories categories}))

;; This dataset is a python object
(type dataset)

(count (py/get-attr dataset "data"))
(def samples (py/get-attr dataset "data"))
(type samples)
(count samples)

;; Sample article
(print (take 1 samples))

;; build tfidf matrix
(def text-feature-extraction (py/import-module "sklearn.feature_extraction.text"))

(def tf-vectorizer (py/call-attr-kw
                    text-feature-extraction "CountVectorizer"
                    nil {:max_features 1000, :stop_words "english"}))

(def tf (py/call-attr tf-vectorizer "fit_transform" samples))
(py/python-type tf) ;; sparse matrix "csr_matrix"
(def features (py/call-attr tf-vectorizer "get_feature_names"))

;; Now we can fit the LDA model
(def sklearn-decomposition (py/import-module "sklearn.decomposition"))
(py/att-type-map sklearn-decomposition)

(def lda (py/call-attr-kw sklearn-decomposition "LatentDirichletAllocation"
                          nil
                          {:n_components 10, :random_state 11}))

(py/call-attr lda "fit" tf)
(pprint (py/att-type-map lda))
(pprint (py/get-attr lda "components_"))

(defn print-top-words [model features n-top-words]
  (map-indexed
   (fn [idx topic]
     (let [top-idxs (->> (py/call-attr topic "argsort")
                                (take-last n-top-words)
                                (reverse))]
       (println (format "Topic #%d:\n%s\n"
                        idx
                        (apply str (for [i top-idxs] (apply str (features i) " ")))))))
   (py/get-attr model "components_")))

;; let's see the top words in each topic grouping that lda produced
(print-top-words lda features 20)
