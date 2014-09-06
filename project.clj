(defproject grammar "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [com.cemerick/austin "0.1.4"]]

  :source-paths ["src"]
  :jvm-opts ["-XX:MaxPermSize=512m" "-Xmx1g"]

  :cljsbuild {:builds [{:id "grammar"
                        :source-paths ["src"]
                        :compiler {:output-to "grammar.js"
                                   :output-dir "out"
                                   :optimizations :none
                                   :source-map true}}]})
