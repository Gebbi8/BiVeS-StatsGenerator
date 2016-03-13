files=read.table ("FILESTATS", header=T)
files[,14] = as.Date(files[,14], "%Y-%m-%d")

#1 nodes
#2 species
#3 reactions
#4 compartments
#5 functions
#6 parameters
#7 rules
#8 events
#9 units
#0 variables
#1 components
#2 curated
#3 modeltype
#4 date
#5 version
#6 model


print (paste ("num models:", length(unique(files[,16]))))
print (paste ("num versions:", length(files[,15])))

dates=sort(unique(files[,14]))


numModels=sapply(dates,function (date) 
{
	length(unique(files[files[,14]<=date,16]))
}
)
# 
# nodesPerModel=sapply(dates,function (date) 
# {
# 	models=unique(files[files[,6]<=date,7])
# 	
# 	
# 	all=
# 	#mean(
# 	sapply(models, function (model)
# 	{
# 		# all entries for this model
# 		#current=files[files[,7]==model,]
# 		
# 		# get latest model
# 		#latest=which.max (files[files[,7]==model&files[,6]<=date,6])
# 		
# 		# that's the number of nodes
# 		#current[latest,1]
# 		#if(length(files[files[,7]==model&files[,6]<=date,6]))
# 		#{
# 		current=files[files[,7]==model,]
# 		current[current[,8]==max(current[current[,6]<=date,8]),1]
# 			#files[files[,7]==model&files[,8]==max(files[files[,7]==model&files[,6]<=date,8]),1]
# 		#}
# 		#else
# 		#{
# 		#	-1
# 		#}
# 	})
# #)
# 	#print(paste (date, models))
# 	#print(all)
# 	mean(all[all>0])
# })

hack=read.table ("CELLMLPERDATE")
hack[,1] = as.Date(hack[,1], "%Y-%m-%d")



pdf ("OUTPUT.pdf", width=9, height=6)

oldpar=par(mar=c(2,2,0.2,0)+.1,mfrow=c(1,1))
plot(dates, numModels, type="l",col="#0040ff",lwd=2,xlab="",ylab="",ylim=c(0,300+max(numModels,hack[,2]/hack[,3])))
lines(hack[,1], hack[,2]/hack[,3],col=colors()[53],lwd=2,lty=6)
legend("topleft",lwd=2,lty=c(1,6), col = c("#0040ff",colors()[53]), 
				legend = c("Number of models in the repository", "Avg number of nodes in an XML tree"))
par(oldpar)

dev.off()





###############################################################
# cellml talk


#bw.write (date + "\t" + numNodes + "\t" + numFiles + "\t" + numUnits + "\t" + numVariables + "\t" + numImports + "\t" + numComponents + LOGGER.NEWLINE);

# 
# hack=read.table ("zzzzFullStatsFiles-cellml-nodes-per-date2")
# hack[,1] = as.Date(hack[,1], "%Y-%m-%d")


pdf ("OUTPUT-talk1.pdf", width=9, height=6)

oldpar=par(mar=c(2,2,0.2,0)+.1,mfrow=c(1,1))
plot(dates, numModels, type="l",col="#0040ff",lwd=2,xlab="",ylab="")
#lines(hack[,1], hack[,2]/hack[,3],col=colors()[53],lwd=2)
legend("topleft",lwd=2,lty=c(1,1), col = c("#0040ff",colors()[53]), 
			 legend = c("Number of models in the repository"
			 #, "Avg number of nodes in an XML tree"
			))
par(oldpar)

dev.off()



pdf ("OUTPUT-talk2.pdf", width=9, height=6)

oldpar=par(mar=c(2,2,0.2,0)+.1,mfrow=c(1,1))
plot(dates, numModels, type="l",col="#0040ff",lwd=2,xlab="",ylab="")
lines(hack[,1], hack[,2]/hack[,3],col=colors()[53],lwd=2)
legend("topleft",lwd=2,lty=c(1,1), col = c("#0040ff",colors()[53]), 
			 legend = c("Number of models in the repository", "Avg number of nodes in an XML tree"))
par(oldpar)

dev.off()



pdf ("OUTPUT-entities.pdf", width=9, height=6)
# 
oldpar=par(mar=c(2,4,0.2,4)+.1,mfrow=c(1,1))
# numUnits
plot(dates, hack[,4]/hack[,3], type="l",col="#0040ff",lwd=2,xlab="",ylab="units,imports,components",ylim=(c(0,
max(
	hack[,4]/hack[,3],
	#hack[,5]/hack[,3],
	hack[,6]/hack[,3],
	hack[,7]/hack[,3]))))
# numVariables
#lines(hack[,1], hack[,5]/hack[,3],col=colors()[84],lwd=2)
# numImports
# lines(hack[,1], hack[,6]/hack[,3],col=colors()[84],lwd=2)
# numComponents
lines(hack[,1], hack[,7]/hack[,3],col=colors()[53],lwd=2)
legend("topright",lwd=2,lty=1, col = c("#0040ff",colors()[84],colors()[53],colors()[33]), 
			 legend = 
			 c("mean num units",
# 				 "mean num imports",
				 "mean num components",
				 "mean num variables"))

# numVariables
par(new=TRUE)
plot(hack[,1], hack[,5]/hack[,3], type="l", col=colors()[33],lwd=2,xaxt="n",yaxt="n",xlab="",ylab="",ylim=c(0,max(hack[,5]/hack[,3])))
axis(4)
mtext("variables",side=4,line=3)

par(oldpar)

dev.off()


# 
# par(new=TRUE)
# plot(hack[,1], hack[,2]/hack[,3],xaxt="n",yaxt="n",xlab="",ylab="", type="l",col=2)
# axis(4)
# mtext("number of nodes",side=4,line=3)
# 
# 

# 2006-08-21 /aHR0cDovL21vZGVscy5jZWxsbWwub3JnL3dvcmtzcGFjZS9ub2JsZV8xOTYyCg==/noble_1962.cellml