(defproject frontend "0.1.0-SNAPSHOT"
  :description "frontend for microblogging web app"
  :url ""
  :license {:name "MIT" :url "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [com.taoensso/timbre "4.10.0"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-anti-forgery "1.2.0"]
                 [environ "1.1.0"]
                 [mount "0.1.12"]
                 [http-kit "2.3.0"]
                 [cljs-ajax "0.7.3"]
                 [compojure "1.6.1"]
                 [reagent "0.8.0"]
                 [re-frame "0.10.5"]
                 [day8.re-frame/http-fx "0.1.6"]
                 ]

  :plugins [[lein-figwheel "0.5.15"]
            [lein-cljsbuild "1.1.7" :exclusions [org.clojure/clojure]]]

  :min-lein-version "2.8.1"

  :jvm-opts ["--add-modules" "java.xml.bind"]

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :uberjar-name "frontend-standalone.jar"
  :main ^:skip-aot frontend.core

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev
             {:dependencies
              [;; [cider/piggieback "0.3.1"]
               [com.cemerick/piggieback "0.2.2"]
               [org.clojure/tools.nrepl "0.2.13"]
               [figwheel-sidecar "0.5.15"]
               [binaryage/devtools "0.9.10"]
               ]
              :plugins []
              :repl-options
              {:nrepl-middleware
               ;; [cider.piggieback/wrap-cljs-repl]
               [cemerick.piggieback/wrap-cljs-repl]
               }}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "frontend.core/mount-root"}
     :compiler     {:main                 frontend.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :optimizations        :none
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            frontend.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :asset-path      "js/compiled/out"
                    :optimizations   :none
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}
    ]}
  )
