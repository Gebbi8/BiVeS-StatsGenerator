diffs=read.table ("data/diffstats", header=T)


boxit<-function (table, type)
{

	inserts=table[,5]
	deletes=table[,6]
	moves=table[,7]
	updates=table[,8]

	updates=updates[updates!=0]
	deletes=deletes[deletes!=0]
	inserts=inserts[inserts!=0]
	moves=moves[moves!=0]

	nodes=table[,13]
	attributes=table[,14]
	texts=table[,15]

	nodes=nodes[nodes!=0]
	attributes=attributes[attributes!=0]
	texts=texts[texts!=0]


	#pdf("operation-types.pdf",width=10,height=6)

	#boxplot(updates,deletes,inserts,moves,horizontal=T,log='x',ylim=c(1,1000),names=c("updates","deletes","inserts","moves"),col=c(7,2:4))

	#boxplot(nodes,attributes,texts,horizontal=T,log='x',ylim=c(1,1000),names=c("nodes","attributes","texts"),col=c(7,2:4))

	
# 	print (updates)
	
	pdf (paste ("./data/graphs/boxes/operations-", type, ".pdf", sep=""),width=8,height=2)


	oldpar=par(mar=c(2,4.3,0.2,0)+.1,mfrow=c(1,1))
	boxplot(updates,deletes,inserts,moves,horizontal=T,log='x',ylim=c(1,1000),
					#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
					col=c("#FFFA5F","#f47e7d","#b5d045","#83C5D1"))
	axis(2, at=1:4,c("updates","deletes","inserts","moves"),las=2,cex.axis=.9)
	par(oldpar)

	dev.off()

	pdf (paste ("./data/graphs/boxes/operations-types-",type, ".pdf", sep=""),width=8,height=1.5)


	oldpar=par(mar=c(2,6.5,0.2,0)+.1,mfrow=c(1,1))
	boxplot(nodes,attributes,texts,horizontal=T,log='x',ylim=c(1,1000),
					#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
					col=c("#81c0c5","#fb8335","#f9c8ea"))
	axis(2, at=1:3,c("document nodes","attributes","text nodes"),las=2,cex.axis=.9)
	par(oldpar)

	dev.off()

}



allinone<-function (SBML, CellML)
{
	
	insertsSBML=SBML[,5]
	deletesSBML=SBML[,6]
	movesSBML=SBML[,7]
	updatesSBML=SBML[,8]
	
	updatesSBML=updatesSBML[updatesSBML!=0]
	deletesSBML=deletesSBML[deletesSBML!=0]
	insertsSBML=insertsSBML[insertsSBML!=0]
	movesSBML=movesSBML[movesSBML!=0]
	
	nodesSBML=SBML[,13]
	attributesSBML=SBML[,14]
	textsSBML=SBML[,15]
	
	nodesSBML=nodesSBML[nodesSBML!=0]
	attributesSBML=attributesSBML[attributesSBML!=0]
	textsSBML=textsSBML[textsSBML!=0]
	
	
	
	insertsCellML=CellML[,5]
	deletesCellML=CellML[,6]
	movesCellML=CellML[,7]
	updatesCellML=CellML[,8]
	
	updatesCellML=updatesCellML[updatesCellML!=0]
	deletesCellML=deletesCellML[deletesCellML!=0]
	insertsCellML=insertsCellML[insertsCellML!=0]
	movesCellML=movesCellML[movesCellML!=0]
	
	nodesCellML=CellML[,13]
	attributesCellML=CellML[,14]
	textsCellML=CellML[,15]
	
	nodesCellML=nodesCellML[nodesCellML!=0]
	attributesCellML=attributesCellML[attributesCellML!=0]
	textsCellML=textsCellML[textsCellML!=0]
	
	
	#pdf("operation-types.pdf",width=10,height=6)
	
	#boxplot(updates,deletes,inserts,moves,horizontal=T,log='x',ylim=c(1,1000),names=c("updates","deletes","inserts","moves"),col=c(7,2:4))
	
	#boxplot(nodes,attributes,texts,horizontal=T,log='x',ylim=c(1,1000),names=c("nodes","attributes","texts"),col=c(7,2:4))
	
	
	# 	print (updates)
	
	pdf ("./data/graphs/boxes/allinone.pdf",width=8,height=7)
	
	
	oldpar=par(mar=c(10.5,1.3,0.2,0)+.1,mfrow=c(1,4))
	
	boxplot(updatesSBML,deletesSBML,insertsSBML,movesSBML,horizontal=F,log='y',ylim=c(1,1000),
					#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
					col=c("#FFFA5F","#f47e7d","#b5d045","#83C5D1"), yaxt='n')
	axis(1, at=1:4,c("updates","deletes","inserts","moves"),las=2,cex.axis=1.5)
# 	axis(1, at=1:4, labels = FALSE)
# 	text(1:4-.5, par("usr")[1] + 0.2, labels = c("updates","deletes","inserts","moves"), srt = 30, pos = 1, xpd = TRUE)
	
# 	par(oldpar)
	
	
	par(mar=c(10.5,0,0.2,1.3)+.1)
# 	oldpar=par(mar=c(2,6.5,0.2,0)+.1,mfrow=c(1,1))
	boxplot(nodesSBML,attributesSBML,textsSBML,horizontal=F,log='y',ylim=c(1,1000),
					#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
					col=c("#cccccc","#fb8335","#c2a5cf"), yaxt='n')
	axis(1, at=1:3,c("document nodes","attributes","text nodes"),las=2,cex.axis=1.5)
	# 	par(oldpar)
# 	axis(1, at=1:3, labels = FALSE)
# 	text(1:3-.2, par("usr")[1] + 0.2, labels = c("document nodes","           attributes","       text nodes"), srt = 60, pos = 1, xpd = TRUE)
	
	axis(4,labels=F)
	
	
	
	
	par(mar=c(10.5,1.3,0.2,0)+.1)
	boxplot(updatesCellML,deletesCellML,insertsCellML,movesCellML,horizontal=F,log='y',ylim=c(1,1000),
					#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
					col=c("#FFFA5F","#f47e7d","#b5d045","#83C5D1"))
	axis(1, at=1:4,c("updates","deletes","inserts","moves"),las=2,cex.axis=1.5)
	
	
	par(mar=c(10.5,0,0.2,1.3)+.1)
	# 	oldpar=par(mar=c(2,6.5,0.2,0)+.1,mfrow=c(1,1))
	boxplot(nodesCellML,attributesCellML,textsCellML,horizontal=F,log='y',ylim=c(1,1000),
					#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
					col=c("#cccccc","#fb8335","#c2a5cf"), yaxt='n')
	axis(1, at=1:3,c("document nodes","attributes","text nodes"),las=2,cex.axis=1.5)
	
	
	
	
	
	par(oldpar)
	
	dev.off()
	
}






dir.create("./data/graphs/boxes/", showWarnings=F, recursive=T)

boxit (diffs, "all")

SBML=diffs[diffs[,16]=="SBML",]
CellML=diffs[diffs[,16]=="CellML",]
stopifnot (length(diffs[,1]) == length(SBML[,1]) + length (CellML[,1]))

boxit (SBML, "SBML")
boxit (CellML, "CellML")

allinone (SBML, CellML)
