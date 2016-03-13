files=read.table ("FILESTATS", header=T)
files[,14] = as.Date(files[,14], "%Y-%m-%d")

#1 nodes
#2 species
#3 reactions
#4 compartments
#5 functions
#6 parameters
#7 rules
#8 events
#9 units
#0 variables
#1 components
#2 curated
#3 modeltype
#4 date
#5 version
#6 model

print (paste ("num models:", length(unique(files[,16]))))
print (paste ("num versions:", length(files[,15])))

dates=sort(unique(files[,14]))

pdf ("OUTPUT.pdf", width=9, height=7)

oldpar=par(mar=c(5,4,0.2,4)+.1,mfrow=c(1,1))

meanNodes=sapply(dates, function (d) { mean (files[files[,14]==d,1])})
meanSpecies=sapply(dates, function (d) { mean (files[files[,14]==d,2])})
meanReactions=sapply(dates, function (d) { mean (files[files[,14]==d,3])})
meanParameters=sapply(dates, function (d) { mean (files[files[,14]==d,6])})
meanRules=sapply(dates, function (d) { mean (files[files[,14]==d,7])})
numModels=sapply(dates, function (d) { length (files[files[,14]==d,1])})

plot(dates, meanNodes, xaxt = "n", type = "b", xlab="",ylab="Average number of nodes in the XML document" , col=4,pch=1,cex=.5,ylim=c(0,max(meanNodes)))
axis(1, at=dates,format(dates, "%b %y"),las=2,cex.axis=.9)

par(new=TRUE)
plot(dates, numModels, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=2,pch=3,cex=.5,ylim=c(0,max(numModels,meanSpecies,meanReactions,meanParameters,meanRules)))
axis(4)
mtext("Number of models; Avg number of species/reactions/parameters/rules per model",side=4,line=3)

par(new=TRUE)
plot(dates, meanSpecies, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=3,pch=5,cex=.5,ylim=c(0,max(numModels,meanSpecies,meanReactions,meanParameters,meanRules)))

par(new=TRUE)
plot(dates, meanReactions, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=5,pch=2,cex=.5,ylim=c(0,max(numModels,meanSpecies,meanReactions,meanParameters,meanRules)))

par(new=TRUE)
plot(dates, meanParameters, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=6,pch=6,cex=.5,ylim=c(0,max(numModels,meanSpecies,meanReactions,meanParameters,meanRules)))

par(new=TRUE)
plot(dates, meanRules, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=8,pch=8,cex=.5,ylim=c(0,max(numModels,meanSpecies,meanReactions,meanParameters,meanRules)))

legend2 <- legend
body(legend2)[[49]] <- quote(
	invisible(list(rect = list(w = w, h = h, left = left, top = top),
								 text = list(x = xt, y = yt), points = list(x = x1, y = y1)))
)
myLegend <- legend2("topleft",lty=1,  pt.bg = 'white',  pt.cex = 2, pt.lwd = 0, cex = 1, pch = 1, #pch = c(1,3,5,2,6,8), 
			 col = c(4,2,3,5,6,8), 
			 legend = c("avg number of nodes per model", "number of models per release", "avg number of species per model", "avg number of reactions per model", "avg number of parameters per model", "avg number of rules per model"))

points(myLegend$points$x, myLegend$points$y, pch = 21, col = "white", bg="white", pt.bg = 'white', cex = 2)

points(myLegend$points$x, myLegend$points$y, pch = c(1,3,5,2,6,8), col = c(4,2,3,5,6,8), cex = .5)

par(oldpar)

dev.off()









pdf ("OUTPUT-nversions.pdf",width=20,height=5)
plot(table(files[,16]))
dev.off()







