files=read.table ("zzzzFullStatsDiffs-sbml")
files[,12] = as.Date(files[,12], "%Y-%m-%d")
files[,13] = as.Date(files[,13], "%Y-%m-%d")

# dates=sort(unique(c(files[,12], files[,13])))
# models=sort(unique(files[,14]))
# 
# # delete empty diffs
# print (paste ("num diffs:", length(files[,1])))
# print (paste ("num empty unix:", length(files[,1])-length(files[files[,1]>0,1])))
# print (paste ("num empty bives:",length(files[,1])-length(files[files[,4]>0,1]) ))
# print (paste ("mean num ops unix:", mean (files[,1])))
# print (paste ("mean num ops bives:", mean (files[,4])))
# 
# print (paste ("num relevant diffs unix:", length(files[files[,1]>0,1])))
# print (paste ("num relevant diffs bives:",length(files[files[,4]>0,1]) ))
# print (paste ("mean num ops unix:", mean (files[files[,4]>0,1])))
# print (paste ("mean num ops bives:", mean (files[files[,4]>0,4])))
# 
# modelsversions=
# 
# models[]

subfiles=files[files[,4]>0,c(4,12,14)]

a=sapply(sort(unique(subfiles[,3])),
function (name)
{
	subsub=subfiles[subfiles[,3]==name,1]
	c(sum(subfiles[subfiles[,3]==name,1]),length(subfiles[subfiles[,3]==name,1]),format(min(subfiles[subfiles[,3]==name,2]), "%Y-%m-%d"), paste("", name, sep=""))
})


numchanges=as.integer(a[1,])
numrevs=as.integer(a[2,])

names(numchanges)=a[4,]
names(numrevs)=a[4,]

#plot (1:length(a[4,]),numchanges)#,1:length(a[4,]))


pdf ("biomodels-numchanges-per-model.pdf",width=20,height=5)

barplot(numchanges,names.arg=a[4,],las=2,cex.names=.25)

dev.off()


pdf ("biomodels-numversions-per-model.pdf",width=20,height=5)

barplot(numrevs,names.arg=a[4,],las=2,cex.names=.25)

dev.off()


#axis(1, at=1:length(a[4,]),a[4,],las=2,cex.axis=.9)
#axis(1, at=(1:(length(a[4,]))),labels=a[4,],las=2,cex.axis=.9)
