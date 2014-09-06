library(ggplot2)

p <- ggplot(mtcars, aes(wt, mpg))
p + geom_point()


p + geom_point(aes(colour = qsec))
