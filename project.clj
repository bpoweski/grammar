(defproject grammar "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [ring "1.2.2"]
                 [compojure "1.1.8"]
                 [enlive "1.1.5"]]

  :source-paths ["src/cljs" "src/clj"]
  :jvm-opts ["-XX:MaxPermSize=512m" "-Xmx1g"]

  :profiles {:dev {:repl-options {:init-ns grammar.browser-repl}
                   :plugins [[lein-cljsbuild "1.0.3"]
                             [com.cemerick/austin "0.1.4"]]
                   :cljsbuild {:builds [{:source-paths ["src/cljs"]
                                         :compiler {:output-to "target/classes/public/app.js"
                                                    :optimizations :simple
                                                    :pretty-print true}}]}}})
