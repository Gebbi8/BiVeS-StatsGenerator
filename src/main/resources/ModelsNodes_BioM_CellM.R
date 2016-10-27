
# /!\Change location of repo-evolution (careful: without revision column)
files=read.table ("./data/repo-evolution", header=T)
files[,1] = as.Date(files[,1], "%Y-%m-%d")


# Biomodels data: evolution of the number of models + average number of nodes.
Biomodels=files[files[,2]=="SBML",]
nb_models_bio=Biomodels[,3]
nb_nodes_biom = Biomodels[,4]/nb_models_bio

#CellML data: evolution of number of models + average number of nodes.
CellMl = files[files[,2]=="CellML",]
nb_models_cell = CellMl[,3]
nb_nodes_cell = CellMl[,4]/nb_models_cell
nb_nodes_cell <- replace(nb_nodes_cell, is.na(nb_nodes_cell), 0)


# dates from the Biomodels versions for the x axis in the plot - remove Apr 2010 for clarity of the plot.
dates = unique(Biomodels[,1])
dates = dates[-16]


#Plot:
pdf ("./data/graphs/nbModels&avgNodes.pdf", width=9, height=7)

par(mar=c(5, 4, 4, 6) + 0.1)

# Plot the trend in the number of models in Biomodels and Physiome Model repositories over the time:
plot(Biomodels[,1], nb_models_bio,  cex=2, axes=FALSE, type = "l",ylab="Number of models", col='blue', xlab="", lwd=4)
axis(1, at=dates,format(dates, "%b %y"),las=2,cex.axis=.9)
axis(2, ylim=c(0,1),col="black",las=1) 
lines(CellMl[,1],nb_models_cell, type = "l", col='red', lwd=4, pch=5)

# Plot the mean number of nodes in the Biomodels and Physiome Model repositories models over time
par(new=TRUE)
plot(Biomodels[,1], nb_nodes_biom,ylab="", xlab="", axes=FALSE,type='l', col = 'blue', ylim=c(0,max(nb_nodes_cell, nb_nodes_biom)), lty=3, lwd=2)
mtext("Time", side=1, line=4)
axis(4, ylim=c(0,1),col="black",las=1) 
mtext("Average number of nodes per model",side=4,line=4) 
lines(CellMl[,1], nb_nodes_cell, type='l', col='red',  lty=3, lwd=2)


legend("topleft", col = c(4,2,4,2), lty=c(1,1,3,3),
       legend = c("number of models in BioModels", "number of models in Physiome", "avg number of nodes in BioModels", "avg number of nodes in Physiome"))
title(main = "Evolution of models in the Biomodels and Physiome Model Repositories over time")


dev.off()

