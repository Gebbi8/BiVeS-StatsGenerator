
diffstats=read.table("DIFFSTATSFILE", header=T)




#1 (unixDelete + unixInsert) + "\t"
#2 + unixInsert + "\t"
#3 + unixDelete + "\t"
#4 + (ins + del + mov + up) + "\t"
#5 + ins + "\t"
#6 + del + "\t"
#7 + mov + "\t"
#8 + up + "\t"
#9 + trIns + "\t"
#0 + trDel + "\t"
#1 + trMov + "\t"
#2 + trUp + "\t"
#3 + nodes + "\t"
#4 + attr + "\t"
#5 + texts + "\t"
#6 + (sbml ? "SBML\t" : "CellML\t")
#7 + "\"" + modelName + "\"\t"
#8 + aV + "\t"
#9 + bV);


# matrix=as.matrix(diffstats)
# matrix[,18] = as.Date(matrix[,18], "%s")
# matrix[,19] = as.Date(matrix[,19], "%s")


biomodels_curated=diffstats[grepl("^B", diffstats[,17]),]
biomodels_noncurated=diffstats[grepl("^M", diffstats[,17]),]
biomodels_all=diffstats[!grepl("http", diffstats[,17]),]
cellml=diffstats[grepl("http", diffstats[,17]),]

length (diffstats[,1])
length(biomodels_curated[,1]) + length(cellml[,1])
stopifnot(length (diffstats[,1]) == length(biomodels_curated[,1]) + length(cellml[,1]) + length(biomodels_noncurated[,1]))
stopifnot(length (biomodels_all[,1]) == length(biomodels_curated[,1]) + length(biomodels_noncurated[,1]))

processDiffs<-function (curmatrix, type, file)
{
file=paste (file, type, sep="")

print (type)
print (paste ("num diffs:", length(curmatrix[,1])))
print (paste ("num empty unix:", length(curmatrix[,1])-length(curmatrix[curmatrix[,1]>0,1])))
print (paste ("num empty bives:",length(curmatrix[,1])-length(curmatrix[curmatrix[,4]>0,1]) ))
print (paste ("mean num ops unix:", mean (curmatrix[,1])))
print (paste ("mean num ops bives:", mean (curmatrix[,4])))

print (paste ("num relevant diffs unix:", length(curmatrix[curmatrix[,1]>0,1])))
print (paste ("num relevant diffs bives:",length(curmatrix[curmatrix[,4]>0,1]) ))
print (paste ("mean num ops unix:", mean (curmatrix[curmatrix[,4]>0,1])))
print (paste ("mean num ops bives:", mean (curmatrix[curmatrix[,4]>0,4])))


data<-data.frame(bives=curmatrix[,4],unix=curmatrix[,1])
# remove null values
data=data[data[,1]>0,]
data=data[data[,2]>0,]




pdf(paste (file, "-scatter-bives-unix.pdf", sep=""), width=6, height=6)
plot(data,xlim=c(0,26500),ylim=c(0,26500), ylab="#operations needed by Unix' diff", xlab="#operations needed by BiVeS",cex.lab=1.3, cex.axis=1.2)
lines(c(0,28000),c(0,28000),col="gray")
legend("topleft", "#operations Unix' diff = #operations BiVeS",col="gray",lty=1)
dev.off()

pdf(paste (file, "-scatter-bives-unix-log.pdf", sep=""), width=6, height=6)
plot(data,xlim=c(1,265000),ylim=c(1,26500),log="xy", ylab="#operations needed by Unix' diff", xlab="#operations needed by BiVeS",cex.lab=1.3, cex.axis=1.2)
lines(c(1,28000),c(1,28000),col="gray")
legend("topleft", "#operations Unix' diff = #operations BiVeS",col="gray",lty=1)
dev.off()

maxBives=max(data[,1])
maxUnix=max(data[,2])
maxBoth=max(maxBives,maxUnix)*2

# other limits for unix
pdf(paste (file, "-scatter-bives-unix-wider.pdf", sep=""), width=6, height=6)
plot(data,xlim=c(0,maxBives),ylim=c(0,maxUnix), ylab="#operations needed by Unix' diff", xlab="#operations needed by BiVeS",cex.lab=1.3, cex.axis=1.2)
lines(c(0,maxBoth),c(0,maxBoth),col="gray")
legend("topleft", "#operations Unix' diff = #operations BiVeS",col="gray",lty=1)
dev.off()

pdf(paste (file, "-scatter-bives-unix-wider-log.pdf", sep=""), width=6, height=6)
plot(data,xlim=c(1,maxBives),ylim=c(1,maxUnix),log="xy", ylab="#operations needed by Unix' diff", xlab="#operations needed by BiVeS",cex.lab=1.3, cex.axis=1.2)
lines(c(1,maxBoth),c(1,maxBoth),col="gray")
legend("topleft", "#operations Unix' diff = #operations BiVeS",col="gray",lty=1)
dev.off()




#all

data4<-data.frame(triggered=apply(curmatrix[,9:12],1,sum),nontrigged=curmatrix[,1]*0,bives=curmatrix[,4],unix=curmatrix[,1])
data4[,2]=data4[,3]-data4[,1]
stopifnot(data4[,1] + data4[,2] == data4[,3])
# data4[data4[,1]>0,]
data4=data4[data4[,1]>0,]
data4=data4[data4[,2]>0,]
data4=data4[data4[,3]>0,]
data4=data4[data4[,4]>0,]


pdf (paste (file, "-auswertung-numops-bives-vs-unix-triggered-nontriggered.pdf", sep=""),width=10,height=3.5)
manyopts=data4#[data4[,1]>1&data4[,2]>1&data4[,3]>15&data4[,4]>15,]
boxplot(manyopts,horizontal=T,ylim=c(0,3500),width=c(1,1,5,5),col=c("gray","gray","white","white"), xlab="Number of operations")
dev.off()

# ####
# higher version:

pdf (paste (file, "-auswertung-numops-bives-vs-unix-triggered-nontriggered-higher.pdf", sep=""),width=10,height=6)
manyopts=data4#[data4[,1]>1&data4[,2]>1&data4[,3]>15&data4[,4]>15,]
colnames(manyopts)<-c("implicated","direct","BiVeS","Unix' diff")
boxplot(manyopts,horizontal=T,ylim=c(0,3500),width=c(1,1,5,5),col=c("gray","gray","white","white"), xlab="Number of operations",cex.lab=1.3, cex.axis=1.2)
dev.off()

# log
pdf (paste (file, "-auswertung-numops-bives-vs-unix-triggered-nontriggered-log.pdf", sep=""),width=10,height=3.5)
boxplot(manyopts,horizontal=T,log="x",width=c(1,1,5,5),col=c("gray","gray","white","white"), xlab="Number of operations")
dev.off()





updates=curmatrix[,8]+curmatrix[,12]
deletes=curmatrix[,6]+curmatrix[,10]
inserts=curmatrix[,5]+curmatrix[,9]
moves=curmatrix[,7]+curmatrix[,11]

updates=updates[updates!=0]
deletes=deletes[deletes!=0]
inserts=inserts[inserts!=0]
moves=moves[moves!=0]


pdf(paste (file, "-changetypes.pdf", sep=""),width=10,height=6)
boxplot(updates,deletes,inserts,moves,horizontal=T,ylim=c(1,1300),names=c("updates","deletes","inserts","moves"),col=c(7,2:4))
dev.off()
pdf(paste (file, "-changetypes-log.pdf", sep=""),width=10,height=6)
boxplot(updates,deletes,inserts,moves,horizontal=T,ylim=c(1,1300),log='x',names=c("updates","deletes","inserts","moves"),col=c(7,2:4))
dev.off()





updates=curmatrix[,8]
deletes=curmatrix[,6]
inserts=curmatrix[,5]
moves=curmatrix[,7]

updates=updates[updates!=0]
deletes=deletes[deletes!=0]
inserts=inserts[inserts!=0]
moves=moves[moves!=0]


pdf(paste (file, "-changetypes-no-triggered.pdf", sep=""),width=10,height=6)
boxplot(updates,deletes,inserts,moves,horizontal=T,ylim=c(1,1300),names=c("updates","deletes","inserts","moves"),col=c(7,2:4))
dev.off()
pdf(paste (file, "-changetypes-no-triggered-log.pdf", sep=""),width=10,height=6)
boxplot(updates,deletes,inserts,moves,horizontal=T,ylim=c(1,1300),log='x',names=c("updates","deletes","inserts","moves"),col=c(7,2:4))
dev.off()








attr=curmatrix[,14]
nodes=curmatrix[,13]
text=curmatrix[,15]

attr=attr[attr!=0]
nodes=nodes[nodes!=0]
text=text[text!=0]


pdf(paste (file, "-elementtypes.pdf", sep=""),width=10,height=6)
boxplot(attr,nodes,text,horizontal=T,ylim=c(1,800),names=c("attr","node","text"),col=c(4,6,3))
dev.off()



pdf(paste (file, "-elementtypes-log.pdf", sep=""),width=10,height=6)
boxplot(attr,nodes,text,horizontal=T,ylim=c(1,800),log='x',names=c("attr","node","text"),col=c(4,6,3))
dev.off()




















}


processDiffs(diffstats, "all", "DIFFRESULTSDIR/")

processDiffs(biomodels_curated, "biomodels_curated", "DIFFRESULTSDIR/")
processDiffs(biomodels_noncurated, "biomodels_noncurated", "DIFFRESULTSDIR/")
processDiffs(biomodels_all, "biomodels_all", "DIFFRESULTSDIR/")


processDiffs(cellml, "cellml", "DIFFRESULTSDIR/")





