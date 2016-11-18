files=read.table ("zzzzFullStatsFiles")
files[,6] = as.Date(files[,6], "%Y-%m-%d")

dates=sort(unique(files[,6]))

pdf ("filestats.pdf", width=9, height=7)
oldpar=par(mar=c(5,4,0.2,4)+.1,mfrow=c(1,1))

meanNodes=sapply(dates, function (d) { mean (files[files[,6]==d,1])})
meanSpecies=sapply(dates, function (d) { mean (files[files[,6]==d,2])})
meanReactions=sapply(dates, function (d) { mean (files[files[,6]==d,3])})
meanParameters=sapply(dates, function (d) { mean (files[files[,6]==d,4])})
meanRules=sapply(dates, function (d) { mean (files[files[,6]==d,5])})
numModels=sapply(dates, function (d) { length (files[files[,6]==d,1])})

plot(dates, meanNodes, xaxt = "n", type = "b", xlab="",ylab="mean number of nodes in the XML document" , col=4,pch=1,cex=.5,ylim=c(0,max(meanNodes)))
axis(1, at=dates,format(dates, "%b %y"),las=2,cex.axis=.9)

par(new=TRUE)
plot(dates, numModels, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=2,pch=3,cex=.5,ylim=c(0,max(numModels)))
axis(4)
mtext("number of !nodes",side=4,line=3)

par(new=TRUE)
plot(dates, meanSpecies, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=3,pch=5,cex=.5,ylim=c(0,max(numModels)))

par(new=TRUE)
plot(dates, meanReactions, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=5,pch=2,cex=.5,ylim=c(0,max(numModels)))

par(new=TRUE)
plot(dates, meanParameters, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=6,pch=6,cex=.5,ylim=c(0,max(numModels)))

par(new=TRUE)
plot(dates, meanRules, type = "b",xaxt="n",yaxt="n",xlab="",ylab="",col=8,pch=8,cex=.5,ylim=c(0,max(numModels)))

legend2 <- legend
body(legend2)[[49]] <- quote(
	invisible(list(rect = list(w = w, h = h, left = left, top = top),
								 text = list(x = xt, y = yt), points = list(x = x1, y = y1)))
)
myLegend <- legend2("topleft",lty=1,  pt.bg = 'white', pt.lwd = 0, cex = 1.2, pch = 21, #pch = c(1,3,5,2,6,8), 
			 col = c(4,2,3,5,6,8), 
			 legend = c("mean number of nodes per model", "number of models per release", "mean num of species", "mean num reactions", "mean num parameters", "mean num rules"))

points(myLegend$points$x, myLegend$points$y, pch = c(1,3,5,2,6,8), col = c(4,2,3,5,6,8), cex = .5)

par(oldpar)
dev.off()
