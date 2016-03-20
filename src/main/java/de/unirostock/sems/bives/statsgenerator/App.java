package de.unirostock.sems.bives.statsgenerator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.cellml.algorithm.CellMLValidator;
import de.unirostock.sems.bives.sbml.algorithm.SBMLValidator;
import de.unirostock.sems.bives.statsgenerator.algorithm.MeanNumNodesCalculator;
import de.unirostock.sems.bives.statsgenerator.algorithm.RepositoryProcessor;
import de.unirostock.sems.bives.statsgenerator.io.DiffStatsWriter;
import de.unirostock.sems.bives.statsgenerator.io.FileStatsWriter;




/**
 * Hello world!.
 */
public class App
{
	
	/** The Constant formatter. */
	public final static SimpleDateFormat	formatter			= new SimpleDateFormat (
																												"yyyy-MM-dd");
	
	/** The fsw. */
	private FileStatsWriter								fsw;
	
	/** The dsw. */
	private DiffStatsWriter								dsw;
	
	/** The too large. */
	public static long										tooLarge			= 10 * 1024 * 1024; // 10Mb
	
	/** The Constant valSBML. */
	private final static SBMLValidator		valSBML				= new SBMLValidator ();
	
	/** The Constant valCellMl. */
	private final static CellMLValidator	valCellMl			= new CellMLValidator ();
	
	/** The speed. */
	public static boolean								speed					= false;
	
	/** The url to the stats website. */
	public static String statsUrl = "https://stats.sems.uni-rostock.de/";
	
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main (String[] args) throws IOException
	{
		// to be provided on the command line...
		String						STORAGE				= "/srv/modelcrawler/storage";
		String						WORKING				= "/srv/modelcrawler/wd";
		speed = false;
	
		new File (WORKING).mkdirs ();
		
		speed = true;
		LOGGER.setMinLevel (LOGGER.WARN);
		LOGGER.setLogFile (new File (WORKING + "/bflog-differ"));
		LOGGER.setLogStackTrace (true);
		
		App app = new App (STORAGE, WORKING);
		app.goForIt (STORAGE, WORKING);
		
		LOGGER.closeLogger ();
	}
	
	/**
	 * Instantiates a new app.
	 *
	 * @param storageDir the source dir
	 * @param workingDir the working dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public App (String storageDir, String workingDir) throws IOException
	{
		new File (storageDir + "/stats/").mkdirs ();
		fsw = new FileStatsWriter (storageDir + "/stats/filestats");
		fsw.create ();
		dsw = new DiffStatsWriter (storageDir + "/stats/diffstats");
		dsw.create ();
	}
	
	/**
	 * Go for it.
	 *
	 * @param storageDir the storage dir
	 * @param workingDir the working dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void goForIt (String storageDir, String workingDir) throws IOException
	{
		fsw.writeHeader ();
		dsw.writeHeader ();
		
		String biomodelsSource = storageDir + "/ftp.ebi.ac.uk";
		String biomodelsWorking = workingDir + "/biomodels-differ";
		String cellmlSource = storageDir + "/models.cellml.org";
		String cellmlWorking = workingDir + "/cellml-differ";
		
		// doing stats
		long startMillis = System.currentTimeMillis ();
		new RepositoryProcessor (new File (biomodelsSource), new File (biomodelsWorking), valSBML, valCellMl, fsw, dsw).process ();
		long firstStop = System.currentTimeMillis ();
		new RepositoryProcessor (new File (cellmlSource), new File (cellmlWorking), valSBML, valCellMl, fsw, dsw).process ();
		long secondStop = System.currentTimeMillis ();
		fsw.close ();
		dsw.close ();

		
		// post-processing
		MeanNumNodesCalculator.run (storageDir + "/stats/filestats", storageDir + "/stats/repo-evolution");
		
		
		System.out.println ("done doing statistics");
		System.out.println ("startMillis " + startMillis);
		System.out.println ("firstStop " + firstStop);
		System.out.println ("secondStop " + secondStop);
		
		long time = firstStop - startMillis;
		int seconds = (int) (time / 1000) % 60 ;
		int minutes = (int) ((time / (1000*60)) % 60);
		int hours   = (int) ((time / (1000*60*60)) % 24);
		System.out.println ("biomodels took: " + time + "ms => " + hours + "h " + minutes + "m " + seconds + "s");
		
		time = secondStop - firstStop;
		seconds = (int) (time / 1000) % 60 ;
		minutes = (int) ((time / (1000*60)) % 60);
		hours   = (int) ((time / (1000*60*60)) % 24);
		System.out.println ("cellml model repository took: " + time + "ms => " + hours + "h " + minutes + "m " + seconds + "s");
		
		time = secondStop - startMillis;
		seconds = (int) (time / 1000) % 60 ;
		minutes = (int) ((time / (1000*60)) % 60);
		hours   = (int) ((time / (1000*60*60)) % 24);
		System.out.println ("everything took: " + time + "ms => " + hours + "h " + minutes + "m " + seconds + "s");
		
	}
}
