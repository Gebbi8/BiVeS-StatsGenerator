/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;



// TODO: Auto-generated Javadoc
/**
 * The Class Figurizer.
 *
 * @author Martin Scharm
 */
public class Figurizer
{
	
	/** The Constant newLine. */
	public final static String newLine = System.getProperty("line.separator");
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public static void main (String[] args) throws IOException, InterruptedException
	{
		File diffStats = new File ("/srv/modelcrawler/statswebsite/copy/workingdir/diffstats");
		File fileStats = new File ("/srv/modelcrawler/statswebsite/copy/workingdir/filestats");
		File targetDir = new File ("/srv/modelcrawler/statswebsite/copy/workingdir/analyses");
		targetDir.mkdirs ();
		
		
		Figurizer f = new Figurizer ();
		String [] types = new String [] {"biomodels", "biomodels-curated", "biomodels-noncurated", "cellml"};
		
		if (true)
		{
			// read files and create R scripts
			f.splitFileStats (fileStats, targetDir, "fileStats");
			f.splitFileStats (diffStats, targetDir, "diffStats");
		}
			

		if (false)
		{
			// create heatmaps
			File heatMapsDir = new File (targetDir.getAbsolutePath () + "/heatmaps-all");
			heatMapsDir.mkdirs ();
			BivesAuswertungImager.run (diffStats, heatMapsDir);
			for (String type : types)
			{
				heatMapsDir = new File (targetDir.getAbsolutePath () + "/heatmaps-" + type);
				heatMapsDir.mkdirs ();
				BivesAuswertungImager.run (new File (targetDir.getAbsolutePath () + "/diffStats-" + type), heatMapsDir);
			}
		}
		
		
		// needs R installed
		if (false)
		{
			File diffStatsDir = new File (targetDir.getAbsolutePath () + "/diffstats-all");
			diffStatsDir.mkdirs ();
			f.doRdiffStats (new File (diffStatsDir.getAbsolutePath () + "/diffStats.R"), diffStats, diffStatsDir);
		}
		/*
		 * DO NOT USE
		 * for (String type : types)
		{
			diffStatsDir = new File (targetDir.getAbsolutePath () + "/diffstats-" + type);
			diffStatsDir.mkdirs ();
			f.doR (new File (diffStatsDir.getAbsolutePath () + "/diffStats.R"), new File (targetDir.getAbsolutePath () + "/diffStats-" + type), diffStatsDir);
		}*/
		
		if (false)
		{
			MeanNumNodesCalculatorCellML.run (targetDir.getAbsolutePath () + "/fileStats-cellml", targetDir.getAbsolutePath () + "/fileStats-cellml-per-date");
		}
		

		if (false)
		{
			File matrixDir = new File (targetDir.getAbsolutePath () + "/biomodels-matrix");
			matrixDir.mkdirs ();
			
			f.doRbiomodelsMatrix (new File (targetDir.getAbsolutePath () + "/diffStats-biomodels-curated"), matrixDir);
		}

		if (true)
		{
			File fileStatsDir = new File (targetDir.getAbsolutePath () + "/filestats-all");
			fileStatsDir.mkdirs ();
			f.doRfileStats (
				new File (targetDir.getAbsolutePath () + "/fileStats-biomodels"),
				new File (targetDir.getAbsolutePath () + "/fileStats-biomodels-curated"),
				new File (targetDir.getAbsolutePath () + "/fileStats-biomodels-noncurated"),
				new File (targetDir.getAbsolutePath () + "/fileStats-cellml"),
				new File (targetDir.getAbsolutePath () + "/fileStats-cellml-per-date"),
				fileStatsDir);
		}
	}
	
	/**
	 * Do rbiomodels matrix.
	 *
	 * @param biomodelsDiffs the biomodels diffs
	 * @param output the output
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private void doRbiomodelsMatrix (File biomodelsDiffs, File output) throws IOException, InterruptedException
	{
		File biomodelsR = new File (output.getAbsolutePath () + "/filescript-biomodels.R");
		BufferedWriter bw = new BufferedWriter (new FileWriter (biomodelsR));
		BufferedReader br = new BufferedReader (new FileReader (getClass().getClassLoader().getResource("biomodels-matrix.R").getFile()));
		while (br.ready ())
			bw.write (br.readLine ().replace ("DIFFSFILE", biomodelsDiffs.getAbsolutePath ())
				.replace ("MATRIXFILE", output.getAbsolutePath () + "/biomodels-matrix") + newLine);
		br.close ();
		bw.close ();
		
    Process p = Runtime.getRuntime().exec(new String [] { "R", "CMD", "BATCH", biomodelsR.getAbsolutePath () }, null, biomodelsR.getParentFile ());
    p.waitFor();
	}
	

	/**
	 * Do rfile stats.
	 *
	 * @param filestatsFileBiomodels the filestats file biomodels
	 * @param filestatsFileBiomodelsCurated the filestats file biomodels curated
	 * @param filestatsFileBiomodelsNonCurated the filestats file biomodels non curated
	 * @param filestatsFileCellml the filestats file cellml
	 * @param filestatsFileCellmlPerDate the filestats file cellml per date
	 * @param fileResultsdir the file resultsdir
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private void doRfileStats (File filestatsFileBiomodels, File filestatsFileBiomodelsCurated, File filestatsFileBiomodelsNonCurated, File filestatsFileCellml, File filestatsFileCellmlPerDate, File fileResultsdir) throws IOException, InterruptedException
	{
		File biomodelsR = new File (fileResultsdir.getAbsolutePath () + "/filescript-biomodels.R");
		BufferedWriter bw = new BufferedWriter (new FileWriter (biomodelsR));
		BufferedReader br = new BufferedReader (new FileReader (getClass().getClassLoader().getResource("filescript-biomodels.R").getFile()));
		while (br.ready ())
			bw.write (br.readLine ().replace ("FILESTATS", filestatsFileBiomodels.getAbsolutePath ())
				.replace ("OUTPUT", fileResultsdir.getAbsolutePath () + "/filestats-biomodels") + newLine);
		br.close ();
		bw.close ();
		
    Process p = Runtime.getRuntime().exec(new String [] { "R", "CMD", "BATCH", biomodelsR.getAbsolutePath () }, null, biomodelsR.getParentFile ());
    p.waitFor();
    
    
		bw = new BufferedWriter (new FileWriter (biomodelsR));
		br = new BufferedReader (new FileReader (getClass().getClassLoader().getResource("filescript-biomodels.R").getFile()));
		while (br.ready ())
			bw.write (br.readLine ().replace ("FILESTATS", filestatsFileBiomodelsCurated.getAbsolutePath ())
				.replace ("OUTPUT", fileResultsdir.getAbsolutePath () + "/filestats-biomodels-curated") + newLine);
		br.close ();
		bw.close ();
		
    p = Runtime.getRuntime().exec(new String [] { "R", "CMD", "BATCH", biomodelsR.getAbsolutePath () }, null, biomodelsR.getParentFile ());
    p.waitFor();
    
    
		bw = new BufferedWriter (new FileWriter (biomodelsR));
		br = new BufferedReader (new FileReader (getClass().getClassLoader().getResource("filescript-biomodels.R").getFile()));
		while (br.ready ())
			bw.write (br.readLine ().replace ("FILESTATS", filestatsFileBiomodelsNonCurated.getAbsolutePath ())
				.replace ("OUTPUT", fileResultsdir.getAbsolutePath () + "/filestats-biomodels-non-curated") + newLine);
		br.close ();
		bw.close ();
		
    p = Runtime.getRuntime().exec(new String [] { "R", "CMD", "BATCH", biomodelsR.getAbsolutePath () }, null, biomodelsR.getParentFile ());
    p.waitFor();
    
    

		File cellmlFilesR = new File (fileResultsdir.getAbsolutePath () + "/filescript-cellml.R");
		bw = new BufferedWriter (new FileWriter (cellmlFilesR));
		br = new BufferedReader (new FileReader (getClass().getClassLoader().getResource("filescript-cellml.R").getFile()));
		while (br.ready ())
			bw.write (br.readLine ()
				.replace ("FILESTATS", filestatsFileCellml.getAbsolutePath ())
				.replace ("CELLMLPERDATE", filestatsFileCellmlPerDate.getAbsolutePath ())
				.replace ("OUTPUT", fileResultsdir.getAbsolutePath () + "/filestats-cellml") + newLine);
		br.close ();
		bw.close ();
		
    p = Runtime.getRuntime().exec(new String [] { "R", "CMD", "BATCH", cellmlFilesR.getAbsolutePath () }, null, cellmlFilesR.getParentFile ());
    p.waitFor();
    
    
	}

	/**
	 * Do rdiff stats.
	 *
	 * @param targetRfile the target rfile
	 * @param diffstatsFile the diffstats file
	 * @param diffResultsdir the diff resultsdir
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private void doRdiffStats (File targetRfile, File diffstatsFile, File diffResultsdir) throws IOException, InterruptedException
	{
		BufferedWriter bw = new BufferedWriter (new FileWriter (targetRfile));
		BufferedReader br = new BufferedReader (new FileReader (getClass().getClassLoader().getResource("diffstats.R").getFile()));
		while (br.ready ())
			bw.write (br.readLine ().replace ("DIFFSTATSFILE", diffstatsFile.getAbsolutePath ()).replace ("DIFFRESULTSDIR", diffResultsdir.getAbsolutePath ()) + newLine);
		br.close ();
		bw.close ();
		
		String[] cmd = { "R", "CMD", "BATCH", targetRfile.getAbsolutePath () };
    Process p = Runtime.getRuntime().exec(cmd, null, targetRfile.getParentFile ());
    p.waitFor();
	}

	/**
	 * Split file stats.
	 *
	 * @param fileStats the file stats
	 * @param targetDir the target dir
	 * @param filePrefix the file prefix
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void splitFileStats (File fileStats, File targetDir, String filePrefix) throws IOException
	{
		BufferedWriter bwB = new BufferedWriter (new FileWriter (targetDir.getAbsolutePath () + "/" + filePrefix + "-biomodels"));
		BufferedWriter bwBc = new BufferedWriter (new FileWriter (targetDir.getAbsolutePath () + "/" + filePrefix + "-biomodels-curated"));
		BufferedWriter bwBnc = new BufferedWriter (new FileWriter (targetDir.getAbsolutePath () + "/" + filePrefix + "-biomodels-noncurated"));
		BufferedWriter bwC = new BufferedWriter (new FileWriter (targetDir.getAbsolutePath () + "/" + filePrefix + "-cellml"));
		
		BufferedReader br = new BufferedReader (new FileReader (fileStats));
		
		String line = "";
		boolean header = true;
		while (br.ready ())
		{
			line = br.readLine () + newLine;
			if (header)
			{
				header = false;
				bwB.write (line);
				bwBc.write (line);
				bwBnc.write (line);
				bwC.write (line);
				continue;
			}
			
			
			int type = getType (line);
			if (type == TypeCellml)
				bwC.write (line);
			else
			{
				bwB.write (line);
				if (type == TypeBiomodelsC)
					bwBc.write (line);
				else
					bwBnc.write (line);
			}
			
		}
		
		bwC.close ();
		bwBc.close ();
		bwBnc.close ();
		bwB.close ();
		br.close ();
	}
	

	/** The Constant TypeCellml. */
	private static final int TypeCellml = 0;
	
	/** The Constant TypeBiomodelsC. */
	private static final int TypeBiomodelsC = 1;
	
	/** The Constant TypeBiomodelsNc. */
	private static final int TypeBiomodelsNc = -1;
	
	/**
	 * Gets the type.
	 *
	 * @param line the line
	 * @return the type
	 */
	private static final int getType (String line)
	{
		if (line.contains ("http"))
			return TypeCellml;
		if (line.contains ("BIOMD0000"))
			return TypeBiomodelsC;
		return TypeBiomodelsNc;
	}
}
