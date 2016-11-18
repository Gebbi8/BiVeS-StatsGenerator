files=read.table ("zzzzFullStatsDiffs-cellml")
#files[,9] = as.Date(files[,9], "%Y-%m-%d")
#files[,10] = as.Date(files[,10], "%Y-%m-%d")

#dates=sort(unique(c(files[,9], files[,10])))
#models=sort(unique(files[,11]))

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



m=matrix(0, nrow=length(models), ncol=length(dates))
#rownames(m)=models
#colnames(m)=dates

dimnames(m) = list(models, dates)

sapply (models,
				function (model)
				{
					sapply (1:length(dates),
									function (datenum)
									{
										#print (paste (model, date))
										if (sum(files[,10]==dates[datenum]&files[,11]==model) > 0)
											m[model,datenum]<<-files[files[,10]==dates[datenum]&files[,11]==model,1]
									}
					)
				}
)

pdf ("biomodels-diffs.pdf", width=9, height=7)
image(m)
dev.off()

m2=log(m)
m2[is.infinite(m2)]=0
pdf ("biomodels-diffs-log.pdf", width=9, height=6)

oldpar=par(mar=c(8,4,0.2,0.2)+.1,mfrow=c(1,1))
image(m2, xaxt = "n", yaxt = "n", xlab="")
modelTiks=models[seq(1, length(models), 20)]
axis(1, at=(0:(length(modelTiks)-1))/(length(modelTiks)-1),labels=modelTiks,las=2,cex.axis=.9)
axis(2, at=(0:(length(dates)-1))/(length(dates)-1),labels=format(dates, "%b %y"),las=2,cex.axis=.9)
par(oldpar)

dev.off()
