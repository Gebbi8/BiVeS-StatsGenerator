library("fields")

files=read.table ("zzzzFullStatsDiffs-sbml")
files[,12] = as.Date(files[,12], "%Y-%m-%d")
files[,13] = as.Date(files[,13], "%Y-%m-%d")

dates=sort(unique(c(files[,12], files[,13])))
models=sort(unique(files[,14]))

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
				if (sum(files[,13]==dates[datenum]&files[,14]==model) > 0)
					m[model,datenum]<<-files[files[,13]==dates[datenum]&files[,14]==model,4]
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

oldpar=par(mar=c(8,4,0.2,5.2)+.1,mfrow=c(1,1))
image(m2, xaxt = "n", yaxt = "n", xlab="",col=colorRampPalette(c("#ffffff", "#0040ff"))( 12 ))#,legend.only = T)
modelTiks=models[seq(1, length(models), 20)]
axis(1, at=(0:(length(modelTiks)-1))/(length(modelTiks)-1),labels=modelTiks,las=2,cex.axis=.9)
axis(2, at=(0:(length(dates)-1))/(length(dates)-1),labels=format(dates, "%b %y"),las=2,cex.axis=.9)

ticks<- c(5, 10, 50, 100, 500, 1000, 5000, max(m))
image.plot(m2, xaxt = "n", yaxt = "n", xlab="",col=colorRampPalette(c("#ffffff", "#0040ff"))( 12 ),legend.only = T,legend.width = 1, axis.args=list( at=c(0,log(ticks)), labels=c(0,ticks)))
#list( at=0:log(max(m)), labels=c(0,exp(1:8))))
par(oldpar)

dev.off()
