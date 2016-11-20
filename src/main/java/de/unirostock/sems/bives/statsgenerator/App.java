package de.unirostock.sems.bives.statsgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.ModelCrawler.CrawlerAPI;
import de.unirostock.sems.ModelCrawler.databases.Interface.Change;
import de.unirostock.sems.ModelCrawler.databases.Interface.ChangeSet;
import de.unirostock.sems.bives.cellml.algorithm.CellMLValidator;
import de.unirostock.sems.bives.sbml.algorithm.SBMLValidator;
import de.unirostock.sems.bives.statsgenerator.algorithm.MeanNumNodesCalculator;
import de.unirostock.sems.bives.statsgenerator.algorithm.RepositoryProcessor;
import de.unirostock.sems.bives.statsgenerator.ds.ComodiTermCounter;
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
	public static String statsUrl = "http://most.sems.uni-rostock.de/";
	
	private Map<String, ComodiTermCounter> comodiTerms;
	
	private String date;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException signals problems with parsing the modelcrawlser's config
	 */
	public static void main (String[] args) throws IOException, ParseException
	{		
		// to be provided on the command line...
		String						STORAGE				= "/srv/modelstats/storage";
		String						WORKING				= "/srv/modelstats/working";
		speed = false;
		
		Options options = new Options();
		options.addOption("f", "fast", false, "be quick and neglect files bigger than 10M");
		options.addOption("s", "storage", true, "set the storage location, defaults to " + STORAGE);
		options.addOption("w", "working", true, "set the working directory, defaults to " + WORKING);
		options.addOption("h", "help", false, "print this help message");

		try
		{
			CommandLine line = new DefaultParser ().parse (options, args);
			if (line.hasOption ("f"))
				speed = true;
			if (line.hasOption ("s"))
				STORAGE = line.getOptionValue("s");
			if (line.hasOption ("w"))
				WORKING = line.getOptionValue("w");
			if (line.hasOption ("h"))
				throw new org.apache.commons.cli.ParseException ("you need help");
		}
		catch (org.apache.commons.cli.ParseException exp)
		{
		    System.out.println ("Unexpected exception: " + exp.getMessage ());
		    HelpFormatter formatter = new HelpFormatter ();
			formatter.setOptionComparator (new Comparator<Option> ()
			{
				
				private static final String	OPTS_ORDER	= "hcrio";
				
				
				public int compare (Option o1, Option o2)
				{
					return OPTS_ORDER.indexOf (o1.getLongOpt ())
						- OPTS_ORDER.indexOf (o2.getLongOpt ());
				}
			});
			formatter.printHelp ("java -jar statsgenerator.jar", options, true);
			return;
		}
	
		new File (WORKING).mkdirs ();
		
		LOGGER.setMinLevel (LOGGER.WARN);
		LOGGER.setLogFile (new File (WORKING + "/bflog-differ"));
		LOGGER.setLogToFile (true);
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
		date = new SimpleDateFormat ("yyyy-MM-dd_HH-mm-ss").format(new Date ());
		new File (storageDir + "/stats/").mkdirs ();
		fsw = new FileStatsWriter (storageDir + "/stats/filestats-" + date);
		fsw.create ();
		dsw = new DiffStatsWriter (storageDir + "/stats/diffstats-" + date);
		dsw.create ();
		
		comodiTerms = new HashMap<String, ComodiTermCounter> ();
	}
	
	/**
	 * Go for it.
	 *
	 * @param storageDir the storage dir
	 * @param workingDir the working dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException signals problems with parsing the modelcrawlser's config
	 */
	@SuppressWarnings("unchecked")
	public void goForIt (String storageDir, String workingDir) throws IOException, ParseException
	{
		JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader (getClass().getClassLoader().getResourceAsStream("modelcrawler.template")));
		json.put("workingDir", workingDir);
		((JSONObject) json.get("storage")).put("baseDir", storageDir);
		File tmp = File.createTempFile("stats-generator-", ".bives");
		tmp.deleteOnExit();
		GeneralTools.stringToFile(json.toJSONString(), tmp);
		
		String [] crawlerArgs = new String [] {"-c", tmp.getAbsolutePath(), "--no-morre"};
		new CrawlerAPI(crawlerArgs);
		
		fsw.writeHeader ();
		dsw.writeHeader ();
		
		String biomodelsSource = storageDir + "/ftp.ebi.ac.uk";
		String biomodelsWorking = workingDir + "/biomodels-differ";
		String cellmlSource = storageDir + "/models.cellml.org";
		String cellmlWorking = workingDir + "/cellml-differ";
		
		// doing stats
		long startMillis = System.currentTimeMillis ();
		new RepositoryProcessor (new File (biomodelsSource), new File (biomodelsWorking), valSBML, valCellMl, fsw, dsw, comodiTerms).process ();
		long firstStop = System.currentTimeMillis ();
		new RepositoryProcessor (new File (cellmlSource), new File (cellmlWorking), valSBML, valCellMl, fsw, dsw, comodiTerms).process ();
		long secondStop = System.currentTimeMillis ();
		fsw.close ();
		dsw.close ();

		// post-processing
		MeanNumNodesCalculator.run (storageDir + "/stats/filestats-" + date, storageDir + "/stats/repo-evolution-" + date);
		
		// dump comodi terms
		BufferedWriter bw = new BufferedWriter (new FileWriter (storageDir + "/stats/comodi-terms-" + date));
		for (String term: comodiTerms.keySet ())
		{
			ComodiTermCounter ctc = comodiTerms.get (term);
			bw.write (ctc.term + "\t" + (ctc.nSBML + ctc.nCellMl) + "\t" + ctc.nSBML + "\t" + ctc.nCellMl);
			bw.newLine ();
		}
		bw.close ();
		
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
