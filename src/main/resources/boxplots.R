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

dir.create("./data/graphs/boxes/", showWarnings=F, recursive=T)

boxit (diffs, "all")

SBML=diffs[diffs[,16]=="SBML",]
CellML=diffs[diffs[,16]=="CellML",]
stopifnot (length(diffs[,1]) == length(SBML[,1]) + length (CellML[,1]))

boxit (SBML, "SBML")
boxit (CellML, "CellML")

