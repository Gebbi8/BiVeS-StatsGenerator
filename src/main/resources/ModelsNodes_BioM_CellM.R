# /!\Change location of repo-evolution (careful: without revision column)
files=read.table ("./data/repo-evolution", header=T)
files[,1] = as.Date(files[,1], "%Y-%m-%d")


# Biomodels data: evolution of the number of models + average number of nodes.
Biomodels=files[files[,2]=="BIOMODELS",]
nb_models_bio=Biomodels[,3]
nb_nodes_biom = Biomodels[,4]/nb_models_bio

#CellML data: evolution of number of models + average number of nodes.
CellMl = files[files[,2]=="PMR2",]
nb_models_cell = CellMl[,3]
nb_nodes_cell = CellMl[,4]/nb_models_cell
nb_nodes_cell <- replace(nb_nodes_cell, is.na(nb_nodes_cell), 0)


dates = sort (c(unique(Biomodels[,1]), unique(CellMl[,1])))
dates <- c(seq(dates[1], dates[length(dates)], by=15*30), dates[length(dates)])



#Plot:
pdf ("./data/graphs/nbModels_avgNodes.pdf", width=9, height=4.5)



#c(bottom, left, top, right) 
oldpar=par(oma=c(0,0,0,0),mar=c(4,4,0,1) + 0.1,mfrow=c(1,2))




# Plot the trend in the number of models in Biomodels and Physiome Model repositories over the time:
plot(nb_models_bio ~ Biomodels[,1],  cex=2, axes=FALSE, type = "l",ylab="#Models in the Repository", col='blue', xlab="", lwd=2, ylim=c(0,max(max(nb_models_bio),max(nb_models_cell))),xlim=c(min(min(Biomodels[,1]),min(CellMl[,1])),max(max(Biomodels[,1]),max(CellMl[,1]))))

mtext("Time", side=1, line=4)
axis(1, at=dates,format(dates, "%b %y"),las=2,cex.axis=.9)
axis(2, ylim=c(0,max(nb_models_bio, nb_models_cell)),col="black",las=1) 
lines(CellMl[,1],nb_models_cell, type = "l", col='red', lwd=2, pch=5)


# Plot the mean number of nodes in the Biomodels and Physiome Model repositories models over time
par(new=TRUE)



par(oma=c(0,0,0,0),mar=c(4,1,0,5) + 0.1,mfrow=c(1,2))



plot(nb_nodes_biom ~ Biomodels[,1] ,ylab="", xlab="", axes=FALSE,type='l', col = 'blue', ylim=c(0,max(nb_nodes_cell, nb_nodes_biom)),xlim=c(min(min(Biomodels[,1]),min(CellMl[,1])),max(max(Biomodels[,1]),max(CellMl[,1]))), lwd=2)
mtext("Time", side=1, line=4)
axis(1, at=dates,format(dates, "%b %y"),las=2,cex.axis=.9)
axis(4, ylim=c(0,max(nb_nodes_biom, nb_nodes_cell)),col="black",las=1) 
mtext("Avgarage #Nodes per Model",side=4,line=4) 
lines(CellMl[,1], nb_nodes_cell, type='l', col='red', lwd=2)



# mtext( "Evolution of models in the Biomodels and Physiome Model Repositories over time", side=3, line=-2, outer = TRUE)
#legend("topleft", col = , lty=c(1,1), )




legend(x = "topleft",inset = 0, legend = c("BioModels", "Physiome"), col=c(4,2), lwd=2, cex=0.7)


par(oldpar)

dev.off()


