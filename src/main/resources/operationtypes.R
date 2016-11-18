sbmlFiles=read.table ("zzzzFullStatsDiffs-sbml")
cellmlFiles=read.table ("zzzzFullStatsDiffs-cellml")

# setup biomodels db
# diffStats.write (
# (unixInsert + unixDelete)
# + "\t" + unixInsert
# + "\t" + unixDelete
# + "\t" + (patch.getNumInserts () + patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ())
# + "\t" + patch.getNumInserts ()
# + "\t" + patch.getNumDeletes ()
# + "\t" + patch.getNumMoves ()
# + "\t" + patch.getNumUpdates ()
# + "\t" + documents.get (i - 1).file.getName ()
# + "\t" + documents.get (i).file.getName ()
# + "\t" + model.getName () + LOGGER.NEWLINE);

inserts=sbmlFiles[,5]
deletes=sbmlFiles[,6]
moves=sbmlFiles[,7]
updates=sbmlFiles[,8]

updates=updates[updates!=0]
deletes=deletes[deletes!=0]
inserts=inserts[inserts!=0]
moves=moves[moves!=0]

nodes=sbmlFiles[,9]
attributes=sbmlFiles[,10]
texts=sbmlFiles[,11]

nodes=nodes[nodes!=0]
attributes=attributes[attributes!=0]
texts=texts[texts!=0]


#pdf("operation-types.pdf",width=10,height=6)

#boxplot(updates,deletes,inserts,moves,horizontal=T,log='x',ylim=c(1,1000),names=c("updates","deletes","inserts","moves"),col=c(7,2:4))

#boxplot(nodes,attributes,texts,horizontal=T,log='x',ylim=c(1,1000),names=c("nodes","attributes","texts"),col=c(7,2:4))

pdf ("operations-biomodels.pdf",width=8,height=2)


oldpar=par(mar=c(2,4.3,0.2,0)+.1,mfrow=c(1,1))
boxplot(updates,deletes,inserts,moves,horizontal=T,log='x',ylim=c(1,1000),
				#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
				col=c("#FFFA5F","#f47e7d","#b5d045","#83C5D1"))
axis(2, at=1:4,c("updates","deletes","inserts","moves"),las=2,cex.axis=.9)
par(oldpar)

dev.off()

pdf ("operations-types-biomodels.pdf",width=8,height=1.5)


oldpar=par(mar=c(2,6.5,0.2,0)+.1,mfrow=c(1,1))
boxplot(nodes,attributes,texts,horizontal=T,log='x',ylim=c(1,1000),
				#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
				col=c("#81c0c5","#fb8335","#f9c8ea"))
axis(2, at=1:3,c("document nodes","attributes","text nodes"),las=2,cex.axis=.9)
par(oldpar)

dev.off()




#dev.off()

# setup cellml model repository
# diffStats.write (
# 	(unixInsert + unixDelete)
# 	+ "\t" + unixInsert
# 	+ "\t" + unixDelete
# 	+ "\t" + (patch.getNumInserts () + patch.getNumDeletes () + patch.getNumMoves () + patch.getNumUpdates ())
# 	+ "\t" + patch.getNumInserts ()
# 	+ "\t" + patch.getNumDeletes ()
# 	+ "\t" + patch.getNumMoves ()
# 	+ "\t" + patch.getNumUpdates ()
# 	+ "\t" + rev1
# 	+ "\t" + rev2
# 	+ "\t\"" + repo.getName () + File.separatorChar + file
# 	+ "\"\t" + diffType + LOGGER.NEWLINE);

inserts=cellmlFiles[,5]
deletes=cellmlFiles[,6]
moves=cellmlFiles[,7]
updates=cellmlFiles[,8]

updates=updates[updates!=0]
deletes=deletes[deletes!=0]
inserts=inserts[inserts!=0]
moves=moves[moves!=0]

nodes=cellmlFiles[,9]
attributes=cellmlFiles[,10]
texts=cellmlFiles[,11]

nodes=nodes[nodes!=0]
attributes=attributes[attributes!=0]
texts=texts[texts!=0]



#boxplot(updates,deletes,inserts,moves,horizontal=T,log='x',ylim=c(1,1000),names=c("updates","deletes","inserts","moves"),col=c(7,2:4))

#boxplot(nodes,attributes,texts,horizontal=T,log='x',ylim=c(1,1000),names=c("nodes","attributes","texts"),col=c(7,2:4))


pdf ("operations-cellml.pdf",width=8,height=2)


oldpar=par(mar=c(2,4.3,0.2,0)+.1,mfrow=c(1,1))
boxplot(updates,deletes,inserts,moves,horizontal=T,log='x',ylim=c(1,1000),
				#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
				col=c("#FFFA5F","#f47e7d","#b5d045","#83C5D1"))
#"#83C5D1","#b5a6d3","#b5d045","#FFFA5F"))#"#b5a6d3","#b5d045","#83C5D1","#FFFA5F"))
axis(2, at=1:4,c("updates","deletes","inserts","moves"),las=2,cex.axis=.9)
par(oldpar)

dev.off()


pdf ("operations-types-cellml.pdf",width=8,height=1.5)


#oldpar=par(mar=c(2,4.3,0.2,0)+.1,mfrow=c(1,1))
oldpar=par(mar=c(2,6.7,0.2,0)+.1,mfrow=c(1,1))
boxplot(nodes,attributes,texts,horizontal=T,log='x',ylim=c(1,1000),
				#names=c("updates","deletes","inserts","moves","nodes","attributes","texts"),
				col=c("#81c0c5","#fb8335","#f9c8ea"))
axis(2, at=1:3,c("document nodes","attributes","text nodes"),las=2,cex.axis=.9)
par(oldpar)

dev.off()













# 
# pdf("operation-types-log.pdf",width=10,height=6)
# 
# boxplot(updates,deletes,inserts,moves,horizontal=T,ylim=c(1,1300),log='x',names=c("updates","deletes","inserts","moves"),col=c(7,2:4))
# 
# dev.off()
# 
# 
# attr=apply(matrix[,c(seq(2,25,6),seq(3,25,6))],1,sum)
# nodes=apply(matrix[,c(seq(4,25,6),seq(5,25,6))],1,sum)
# text=apply(matrix[,c(seq(6,25,6),seq(7,25,6))],1,sum)
# 
# attr=attr[attr!=0]
# nodes=nodes[nodes!=0]
# text=text[text!=0]
# 
# 
# pdf("operation-node-types.pdf",width=10,height=6)
# 
# boxplot(attr,nodes,text,horizontal=T,ylim=c(1,800),names=c("attr","node","text"),col=c(4,6,3))
# 
# dev.off()
# 
# 

