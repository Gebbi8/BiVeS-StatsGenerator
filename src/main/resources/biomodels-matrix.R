library("fields")

files=read.table ("./data/diffstats", header=T)
files[,18] = as.Date(files[,18], "%Y-%m-%d")
files[,19] = as.Date(files[,19], "%Y-%m-%d")
files = files[grep("^/BIO", files[,17]),] # get curated models from Biomodels
files=files[complete.cases(files[,18]),] # remove lines with missing values

dates=sort(unique(c(files[,18], files[,19])))
models=sort(unique(files[,17]))

# delete empty diffs
print (paste ("num diffs:", length(files[,1])))
print (paste ("num empty unix:", length(files[,1])-length(files[files[,1]>0,1])))
print (paste ("num empty bives:",length(files[,1])-length(files[files[,4]>0,1]) ))
print (paste ("mean num ops unix:", mean (files[,1])))
print (paste ("mean num ops bives:", mean (files[,4])))

print (paste ("num relevant diffs unix:", length(files[files[,1]>0,1])))
print (paste ("num relevant diffs bives:",length(files[files[,4]>0,1]) ))
print (paste ("mean num ops unix:", mean (files[files[,4]>0,1])))
print (paste ("mean num ops bives:", mean (files[files[,4]>0,4])))

# Matrix of unique models (row) and the different Biomodels release dates (column)
m=matrix(0, nrow=length(models), ncol=length(dates))
dimnames(m) = list(models, dates)

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


# sapply (models,
# 	function (model)
# 	{
# 		sapply (1:length(dates),
# 			function (datenum)
# 			{
#  			#	print (paste (model, datenum))
# 				if (sum(files[,19]==dates[datenum]&files[,17]==model) > 0){
# 				  print (paste (model, datenum))
# 					m[model,datenum]<<-files[files[,19]==dates[datenum]&files[,17]==model,4]
# 		  	}
# 			}
# 		)
# 	}
# )


# Matrix m to get the BIVES number of changes for a model that occured at a certain date (between two versions)
for(model in models){
  for(datenum in 1:length(dates)){
    if(sum(files[,19]==dates[datenum]&files[,17]==model) > 0){
#       print(files[,19]==dates[datenum] && files[,17] == model)
      m[model,datenum] = files[files[,19]==dates[datenum]&files[,17]==model,4]
    }
  }
}


dir.create("./data/graphs/Biomodels/", showWarnings=F, recursive=T)


#plots
pdf ("./data/graphs/Biomodels/MATRIXFILE.pdf", width=9, height=7)
image(m)
dev.off()

m2=log(m)
m2[is.infinite(m2)]=0
pdf ("./data/graphs/Biomodels/MATRIXFILE-log.pdf", width=9, height=6)

oldpar=par(mar=c(8,4,0.2,5.2)+.1,mfrow=c(1,1))
image(m2, xaxt = "n", yaxt = "n", xlab="",col=colorRampPalette(c("#ffffff", "#0040ff"))( 12 ))#,legend.only = T)
# modelTiks=models[seq(1, length(models), 20)]
modelTiks=sapply(models[seq(1, length(models), 20)], substr, 2, 16)
axis(1, at=(0:(length(modelTiks)-1))/(length(modelTiks)-1),labels=modelTiks,las=2,cex.axis=.9)
axis(2, at=(0:(length(dates)-1))/(length(dates)-1),labels=format(dates, "%b %y"),las=2,cex.axis=.9)

ticks<- c(5, 10, 50, 100, 500, 1000, 5000, max(m))
image.plot(m2, xaxt = "n", yaxt = "n", xlab="",col=colorRampPalette(c("#ffffff", "#0040ff"))( 12 ),legend.only = T,legend.width = 1, axis.args=list( at=c(0,log(ticks)), labels=c(0,ticks)))
#list( at=0:log(max(m)), labels=c(0,exp(1:8))))
par(oldpar)

dev.off()
