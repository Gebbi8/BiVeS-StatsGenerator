
# /!\Change location of repo-evolution (careful: without revision column)
files=read.table ("/home/vasundra/SEMS_work/StatsHackathon/repo-evolution", header=T)
files[,1] = as.Date(files[,1], "%Y-%m-%d")

# list of dates
dates=unique(files[,1])

# select random dates for the x axis:
condensedDates = c(dates[1],dates[15], dates[50], dates[100], dates[200],dates[300], dates[400], dates[500], dates[550], dates[600], dates[650],dates[700])

# Biomodels data: evolution of number of models + average number of nodes.
Biomodels=files[files[,2]=="SBML",]
nb_models_bio=Biomodels[,3]
nb_nodes_biom = Biomodels[,4]/nb_models_bio

#CellML data: evolution of number of models + average number of nodes.
CellMl = files[files[,2]=="CellML",]
nb_models_cell = CellMl[,3]
nb_nodes_cell = CellMl[,4]/nb_models_cell
nb_nodes_cell <- replace(nb_nodes_cell, is.na(nb_nodes_cell), 0)


#Plot
# /!\ Change location 
pdf ("/home/vasundra/SEMS_work/sems-docs/papers/2015-statsPaper/nbModels&avgNodes.pdf", width=9, height=7)

par(mar=c(5, 4, 4, 6) + 0.1)

plot(dates, nb_models_cell,  cex=2, axes=FALSE, type = "l",ylab="Number of models", col='red', xlab="", lwd=4)
axis(1, at=condensedDates,format(condensedDates, "%b %y"),las=2,cex.axis=.9)
axis(2, ylim=c(0,1),col="black",las=1) 
lines(dates, nb_models_bio, type = "l", col='blue', lwd = 4, pch=5)

par(new=TRUE)
plot(dates, nb_nodes_biom,ylab="", xlab="", axes=FALSE,type='l', col = 'blue', ylim=c(0,max(nb_nodes_cell, nb_nodes_biom)), lty=3, lwd=2)
mtext("Time", side=1, line=4)
axis(4, ylim=c(0,1),col="black",las=1) 
mtext("Average number of nodes per model",side=4,line=4) 
lines(dates, nb_nodes_cell, type='l', col='red',  lty=3,lwd=2)


legend("topleft", col = c(4,2,4,2), lty=c(1,1,3,3),
       legend = c("number of models in BioModels", "number of models in CellML", "avg number of nodes in BioModels", "avg number of nodes in CellML"))


dev.off()
