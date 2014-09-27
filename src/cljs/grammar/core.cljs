(ns grammar.core)

(comment
  (data-frame [1 2 3])
  (data-frame :x [1 2 3])
  (data-frame (data-table [1 2 3]))

  (data-frame (data-table [1 2 3]))

  (plot [1 2 3] :aes (aes :x (exp mpg 2) :y (/ wt cyl)))

  (-> (data-frame :date [1 2 3])
      (plot :aes {:x :date})
      (geom-bar :width .5)
      (facet-wrap :cut)
      (coord-flip)))

(defn data-frame
  "Creates a data frame"
  [col v & more]
  (apply hash-map col v more))

(comment
  (-> (data-frame :year [2006 2007 2008 2009 2010] :books [54 43 41 44 35])
      (ggplot "svg")))

(comment
  ;; ggplot(data.frame(year=c(2006, 2007, 2008, 2009, 2010), books=c(54, 43, 41, 44, 35)), aes(x=year, y=books)) + geom_bar(stat="identity")

  (-> (data-frame :year [2006 2007 2008 2009 2010] :books [54 43 41 44 35])
      (ggplot "svg")
      (geom-bar :stat "identity")))

(comment
  ;; ggplot(data.frame(x=c(1, 2, 3), y=c(1, 2, 3)), aes(x=x, y=y)) + geom_point()
  ;; ggplot(data.frame(year=c(2006, 2007, 2008, 2009, 2010), books=c(54, 43, 41, 44, 35)), aes(x=year, y=books)) + geom_bar(stat="identity")

  (-> (data-frame :year [2006 2007 2008 2009 2010] :books [54 43 41 44 35])
      (ggplot "svg")
      (geom-bar :stat "identity")))

(comment
  (-> (.select x "body")
      (.append "svg")
      (size 400 300)
      (.append "rect")
      (.attr "x" 100)
      (.attr "y" 100)
      (size 100 200)))

(defn rel [x]
  {:type :rel :value x})

(defn theme-gray [& {:keys [base-size base-family]
                     :or {base-size 12
                          base-family ""}}]

  ;; ported https://github.com/hadley/ggplot2/blob/master/R/theme-defaults.r
  {:line                 {:colour :black
                          :size 0.5
                          :linetype 1
                          :lineend :butt}
   :rect                 {:fill :white
                          :colour :black
                          :size 0.5
                          :linetype 1}
   :text                 {:family base-family
                          :face "plain"
                          :colour :black
                          :size base-size
                          :hjust 0.5
                          :vjust 0.5
                          :angle 0
                          :lineheight 0.9}
   :axis.text            {:size (rel 0.8) :colour :grey50} ;; rgb(127, 127, 127)
   :strip.text           {:size (rel 0.8)}

   :axis.line            {}
   :axis.text.x          {}
   :axis.text.y          {}
   :axis.ticks           {}
   :axis.length          {}
   :axis.ticks.margin    {}

   :legend.background    {}
   :legend.margin        {}
   :legend.key           {}
   :legend.key.size      {}
   :legend.key.height    {}
   :legend.key.width     nil
   :legend.text          {}
   :legend.text.align    nil
   :legend.title         {}
   :legend.title.align   {}
   :legend.position      :right
   :legend.direction     nil
   :legend.justification :center
   :legend.box           nil

   :panel.background     {}  ;; element_rect(fill = "grey90", colour = NA),
   :panel.border         {}  ;; element_blank(),
   :panel.grid.major     {}  ;; element_line(colour = "white"),
   :panel.grid.minor     {}  ;; element_line(colour = "grey95", size = 0.25),
   :panel.margin         {}  ;; unit(0.25, "lines"),
   :panel.margin.x       nil
   :panel.margin.y       nil

   :strip.background     {} ;; element_rect(fill = "grey80", colour = NA),
   :strip.text.x         {} ;; element_text(),
   :strip.text.y         {} ;; element_text(angle = -90),

   :plot.background      {} ;; element_rect(colour = "white"),
   :plot.title           {} ;; element_text(size = rel(1.2)),
   :plot.margin          {} ;; unit(c(1, 1, 0.5, 0.5), "lines"),
   })

(defn ggplot [data & {:keys [aes]}]
  {:data    data
   :aes     aes
   :x-scale (d3.scale.linear.)
   :y-scale (d3.scale.linear.)
   :layers  []
   :theme   (theme-gray)})

(defn geom-point
  "The point geom is used to create scatterplots."
  [{:keys [aes data] :as plot} & rest]
  (update-in plot [:layers] conj (assoc plot :type :point :aes aes :data data :id (gensym))))

(defn size [x width height]
  (-> x
      (.attr "width" width)
      (.attr "height" height)))

(defn draw-grid [canvas {:keys [data x-scale y-scale layers] :as plot}]
  (let [margin  {:top 20 :right 20 :bottom 30 :left 40}
        width   (- 960 (:left margin) (:right margin))
        height  (- 500 (:top margin) (:bottom margin))
        x-scale (-> x-scale
                    (.domain (clj->js [0.5 1.5]))
                    (.range (clj->js [0 width])))
        y-scale (-> y-scale
                    (.domain (clj->js [1.5 2.5]))
                    (.range (clj->js [height 0])))
        svg     (-> (size canvas (+ width (:left margin) (:right margin)) (+ height (:top margin) (:bottom margin)))
                    (.append "g")
                    (.attr "transform" (str "translate(" (:left margin) "," (:top margin) ")")))

        x-axis  (-> (d3.svg.axis.)
                    (.scale x-scale)
                    (.orient "bottom")
                    (.tickValues (clj->js [0.5 0.75 1.00 1.25 1.5]))
                    (.tickFormat (d3.format. ".2f")))
        y-axis  (-> (d3.svg.axis.)
                    (.scale y-scale)
                    (.orient "left")
                    (.tickValues (clj->js [1.5 1.75 2.0 2.25 2.5]))
                    (.tickFormat (d3.format. ".2f")))]
    (-> svg
        (.append "rect")
        (.attr "fill" "rgb(229, 229, 229)")
        (size width height))

    ;; major grid lines
    (-> svg
        (.append "g")
        (.attr "class" "x grid major")
        (.attr "transform" (str "translate(0," height ")"))
        (.call (-> (d3.svg.axis.)
                   (.scale x-scale)
                   (.orient "bottom")
                   (.tickValues (clj->js [0.5 0.75 1.00 1.25 1.5]))
                   (.tickFormat "")
                   (.tickSize (- height) 0 0))))

    (-> svg
        (.append "g")
        (.attr "class" "x grid minor")
        (.attr "transform" (str "translate(0," height ")"))
        (.call (-> (d3.svg.axis.)
                   (.scale x-scale)
                   (.orient "bottom")
                   (.tickValues (clj->js (take 4 (iterate (partial + 0.25) 0.625))))
                   (.tickFormat "")
                   (.tickSize (- height) 0 0))))

    (-> svg
        (.append "g")
        (.attr "class" "y grid major")
        (.call (-> (d3.svg.axis.)
                   (.scale y-scale)
                   (.orient "left")
                   (.tickValues (clj->js [1.5 1.75 2.0 2.25 2.5]))
                   (.tickFormat "")
                   (.tickSize (- width) 0 0))))

    (-> svg
        (.append "g")
        (.attr "class" "y grid minor")
        (.call (-> (d3.svg.axis.)
                   (.scale y-scale)
                   (.orient "left")
                   (.tickValues (clj->js (take 4 (iterate (partial + 0.25) 1.625))))
                   (.tickFormat "")
                   (.tickSize (- width) 0 0))))

    ;; axis
    (-> svg
        (.append "g")
        (.attr "class" "x axis")
        (.attr "transform" (str "translate(0," height ")"))
        (.call x-axis))

    (-> svg
        (.append "g")
        (.attr "class" "y axis")
        (.call y-axis))

    (doseq [layer layers]
      (draw-layer svg layer))))

(defn draw-layer [canvas {:keys [id data x-scale y-scale aes]}]
  (-> canvas
      (.selectAll (name id))
      (.data (clj->js data))
      (.enter)
      (.append "circle")
      (.attr "r" 3.5)
      (.attr "cx" (comp x-scale (:x aes) js->clj))
      (.attr "cy" (comp y-scale (:y aes) js->clj))
      (.style "fill" "black")))

(defn render! [{:keys [location layers x-scale y-scale] :as plot}]
  (let [canvas (-> js/d3
                   (.select "body")
                   (.append "svg"))]
    (draw-grid canvas plot)))

(-> (ggplot [[1 2]] :aes {:x first :y second})
    (geom-point)
    (render!))
