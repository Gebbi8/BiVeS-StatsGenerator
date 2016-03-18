package de.unirostock.sems.bives.statsgenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.jdom2.Element;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.bives.algorithm.ModelValidator;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.cellml.algorithm.CellMLValidator;
import de.unirostock.sems.bives.cellml.api.CellMLDiff;
import de.unirostock.sems.bives.cellml.parser.CellMLComponent;
import de.unirostock.sems.bives.cellml.parser.CellMLDocument;
import de.unirostock.sems.bives.cellml.parser.CellMLModel;
import de.unirostock.sems.bives.cellml.parser.CellMLUnitDictionary;
import de.unirostock.sems.bives.ds.ModelDocument;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.bives.sbml.algorithm.SBMLValidator;
import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.statsgenerator.ds.DiffResult;
import de.unirostock.sems.bives.statsgenerator.ds.ModelVersion;
import de.unirostock.sems.bives.statsgenerator.io.DiffStatsWriter;
import de.unirostock.sems.bives.statsgenerator.io.FileStatsWriter;




// TODO: Auto-generated Javadoc
/**
 * Hello world!.
 */
public class App
{
	
	/** The Constant formatter. */
	private final static SimpleDateFormat	formatter			= new SimpleDateFormat (
																												"yyyy-MM-dd");
	
	/** The Constant PMR_BLACKLIST. */
	private final static String[]					PMR_BLACKLIST	= new String[] { "vm",
		"png", "bmp", "jpg", "jpeg", "html", "htm", "xhtml", "svg", "pdf", "json",
		"pl", "rdf", "rar", "msh"												};
	
	
	/** The fsw. */
	private FileStatsWriter								fsw;
	
	/** The dsw. */
	private DiffStatsWriter								dsw;
	
	/** The too large. */
	private static long										tooLarge			= 20 * 1024 * 1024;																// 1mb
	
	/** The Constant valSBML. */
	private final static SBMLValidator		valSBML				= new SBMLValidator ();
	
	/** The Constant valCellMl. */
	private final static CellMLValidator	valCellMl			= new CellMLValidator ();
	
	/** The speed. */
	private static boolean								speed					= false;
	
	private String sourceDir;
	private String workingDir;
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main (String[] args) throws IOException
	{
		// to be provided on the command line...
		String						SOURCE				= "/srv/modelcrawler/statswebsite/copy/storage";
		String						WORKING				= "/srv/modelcrawler/statswebsite/copy/workingdir";
	
		new File (WORKING).mkdirs ();
		
		speed = true;
		LOGGER.setMinLevel (LOGGER.WARN);
		LOGGER.setLogFile (new File (WORKING + "/bflog"));
		
		App app = new App (SOURCE, WORKING);
		app.goForIt ();
		
		// errorLog.close ();
		LOGGER.closeLogger ();
	}
	
	/**
	 * Instantiates a new app.
	 *
	 * @param sourceDir the source dir
	 * @param workingDir the working dir
	 */
	public App (String sourceDir, String workingDir)
	{
		this.sourceDir = sourceDir;
		this.workingDir = workingDir;
		
		fsw = new FileStatsWriter (workingDir + "/filestats");
		dsw = new DiffStatsWriter (workingDir + "/diffstats");
	}
	
	/**
	 * Go for it.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void goForIt () throws IOException
	{
		fsw.writeHeader ();
		dsw.writeHeader ();
		
		String biomodelsSource = sourceDir + "/biomodels-clone/remodeled";
		String biomodelsWorking = workingDir + "/biomodels";
		String cellmlSource = sourceDir + "/cellml-clone/remodeled";
		String cellmlWorking = workingDir + "/cellml";
		
		processBiomodels (biomodelsSource, biomodelsWorking);
		processCellml (cellmlSource, cellmlWorking);
	}
	
	
	/**
	 * Process cellml.
	 *
	 * @param cellmlDir the cellml dir
	 * @param cellmlWd the cellml wd
	 */
	public void processCellml (String cellmlDir, String cellmlWd)
	{
		File repositories = new File (cellmlDir);
		for (File repository : repositories.listFiles ())
		{
			processCellmlRepo (repository, cellmlWd);
		}
	}
	
	
	/**
	 * Process cellml repo.
	 *
	 * @param repository the repository
	 * @param cellmlWd the cellml wd
	 */
	public void processCellmlRepo (File repository, String cellmlWd)
	{
		Map<String, HashMap<String, ModelVersion>> versionBank = new HashMap<String, HashMap<String, ModelVersion>> ();
		
		LOGGER.info (">>>>   processing repository ", repository.getName ());
		System.out
			.println (">>>>   processing repository " + repository.getName ());
		String repoName = new String (GeneralTools.decodeBase64 (repository
			.getName ()));
		while (repoName.endsWith ("\n"))
			repoName = repoName.substring (0, repoName.length () - 1);
		for (File version : repository.listFiles ())
		{
			LOGGER.info ("-- processing version ", version.getName ());
			System.out.println ("-- processing version " + version.getName ());
			HashMap<String, ModelVersion> models = processCellMlRepoVersion (
				new HashMap<String, ModelVersion> (), repoName, version.getName (),
				version, version.getAbsolutePath ().length ());
			if (models.size () > 0)
				versionBank.put (version.getName (), models);
		}
		
		doCellMlVersionStats (versionBank, cellmlWd);
	}
	
	
	/**
	 * Do cellml file stats.
	 *
	 * @param aV the a v
	 * @param versionName the version name
	 * @param modelName the model name
	 */
	private void doCellmlFileStats (ModelVersion aV, String versionName,
		String modelName)
	{
		
		ModelDocument doc = null;
		if (aV.isSbml ())
		{
			valSBML.validate (aV.getFile ());
			doc = valSBML.getDocument ();
		}
		else
		{
			valCellMl.validate (aV.getFile ());
			doc = valCellMl.getDocument ();
		}
		Map<String, Integer> docstats = doc.getTreeDocument ().getNodeStats ();
		doFileStats (aV.getFile (), docstats,
			formatter.format (new Date (Long.parseLong (versionName) * 1000L)),
			versionName, modelName, !aV.isSbml (), doc);
		
	}
	
	
	/**
	 * Do cell ml version stats.
	 *
	 * @param versionBank the version bank
	 * @param cellmlWd the cellml wd
	 */
	private void doCellMlVersionStats (
		Map<String, HashMap<String, ModelVersion>> versionBank, String cellmlWd)
	{
		Object[] keys = versionBank.keySet ().toArray ();
		Arrays.sort (keys);
		
		if (keys.length > 0)
		{
			// do all file stats for the first commit
			HashMap<String, ModelVersion> a = versionBank.get (keys[0]);
			for (String model : a.keySet ())
			{
				ModelVersion aV = a.get (model);
				doCellmlFileStats (aV, (String) keys[0], model);
			}
		}
		
		for (int i = 1; i < keys.length; i++)
		{
			HashMap<String, ModelVersion> a = versionBank.get (keys[i - 1]);
			HashMap<String, ModelVersion> b = versionBank.get (keys[i]);
			
			for (String model : b.keySet ())
			{
				ModelVersion aV = a.get (model);
				ModelVersion bV = b.get (model);
				
				if (aV != null)
				{
					if (compare (aV.getFile (), formatter.format (new Date (Long
						.parseLong ((String) keys[i - 1]) * 1000L)), bV.getFile (),
						formatter.format (new Date (
							Long.parseLong ((String) keys[1]) * 1000L)), cellmlWd, model,
						aV.isSbml ()))
					{
						// filestats of b if it differs from a
						doCellmlFileStats (bV, (String) keys[i], model);
					}
				}
				else
				{
					// filestats of b because in a it doesn't exist
					doCellmlFileStats (bV, (String) keys[i], model);
				}
			}
			
		}
		
	}
	
	
	/**
	 * Process cell ml repo version.
	 *
	 * @param files the files
	 * @param repo the repo
	 * @param versionName the version name
	 * @param versionDir the version dir
	 * @param discardStart the discard start
	 * @return the hash map
	 */
	private HashMap<String, ModelVersion> processCellMlRepoVersion (
		HashMap<String, ModelVersion> files, String repo, String versionName,
		File versionDir, int discardStart)
	{
		
		for (File file : versionDir.listFiles ())
		{
			if (file.isDirectory ())
			{
				processCellMlRepoVersion (files, repo, versionName, file, discardStart);
			}
			else
			{
				// test file
				
				// let's skip large files if we need to speed up
				if (speed && file.length () > tooLarge)
				{
					LOGGER.warn ("skipping file ", file, " as it is too large: ",
						file.length ());
					continue;
				}
				
				// blacklist extensions too speed up that stuff
				boolean goon = true;
				for (String blacklist : PMR_BLACKLIST)
					if (file.getName ().endsWith (blacklist))
					{
						goon = false;
						break;
					}
				if (!goon)
					continue;
				
				System.out.println ("processing file " + file.getAbsolutePath ());
				
				String modelName = repo
					+ file.getAbsolutePath ().substring (discardStart);
				
				// try to read the file
				boolean isCellml = valCellMl.validate (file);
				boolean isSbml = isCellml ? false : valSBML.validate (file);
				
				if (isCellml || isSbml)
				{
					// try
					// {
					System.out.println ("is cellml or sbml: " + isCellml + " -- "
						+ isSbml);
					// ModelDocument doc = isCellml ? valCellMl.getDocument () :
					// valSBML.getDocument ();
					files.put (modelName, new ModelVersion (file, isSbml));
					
					/*
					 * int nUnits = getNumber (docstats.get ("unitDefinition"));
					 * int nVariables = 0;
					 * // TODO: is there something like compartments in cellml? -> ask
					 * david
					 * int nComponents = 0;
					 * int nReactions = getNumber (docstats.get ("reaction"));
					 * if (isCellml)
					 * {
					 * CellMLModel model = ((CellMLDocument)doc).getModel ();
					 * nReactions = 0;
					 * nUnits = model.getUnits ().getModelUnits ().size ();
					 * HashMap<String, CellMLComponent> components = model.getComponents
					 * ();
					 * CellMLUnitDictionary unitDict = model.getUnits ();
					 * for (String c : components.keySet ())
					 * {
					 * CellMLComponent comp = components.get (c);
					 * nComponents++;
					 * if (unitDict.getComponentUnits (comp) != null)
					 * nUnits += unitDict.getComponentUnits (comp).size ();
					 * nVariables += comp.getVariables ().size ();
					 * nReactions += comp.getReactions ().size ();
					 * }
					 * }
					 * 
					 * filestatsWriter.write (doc.getTreeDocument ().getNumNodes () + "\t"
					 * + getNumber (docstats.get ("species")) + "\t"
					 * + nReactions + "\t"
					 * + getNumber (docstats.get ("compartment")) + "\t"
					 * + getNumber (docstats.get ("functionDefinition")) + "\t"
					 * + getNumber (docstats.get ("parameter")) + "\t"
					 * + (getNumber (docstats.get ("assignmentRule")) + getNumber
					 * (docstats.get ("rateRule")) + getNumber (docstats.get
					 * ("algebraicRule"))) + "\t"
					 * 
					 * + getNumber (docstats.get ("event")) + "\t"
					 * + nUnits + "\t"
					 * + nVariables + "\t"
					 * + nComponents + "\t"
					 * + file.getName ().startsWith ("BIOM") + "\t"
					 * + (isCellml ? "CellML\t" : "SBML\t")
					 * + formatter.format (new Date (Long.parseLong (versionName)*1000L))
					 * + "\t"
					 * + versionName + "\t"
					 * + "\"" + modelName + "\"");
					 * filestatsWriter.newLine ();
					 */
					// }
					// catch (IOException e)
					// {
					// LOGGER.error (e, "cannot write stats to disk: ", file.getName ());
					// }
				}
				else if (file.getName ().endsWith ("cellml"))
				{
					doubleCheckFileError (file, valCellMl);
				}
			}
		}
		return files;
	}
	
	
	/**
	 * Process biomodels.
	 *
	 * @param biomodelsDir the biomodels dir
	 * @param biomodelsWd the biomodels wd
	 */
	public void processBiomodels (String biomodelsDir, String biomodelsWd)
	{
		File biomodels = new File (biomodelsDir);
		for (File model : biomodels.listFiles ())
		{
			processBiomodelsModel (model, biomodelsWd);
		}
	}
	
	
	/**
	 * Process biomodels model.
	 *
	 * @param modelDir the model dir
	 * @param biomodelsWd the biomodels wd
	 */
	public void processBiomodelsModel (File modelDir, String biomodelsWd)
	{
		File[] versions = modelDir.listFiles ((FileFilter) FileFileFilter.FILE);
		Arrays.sort (versions, NameFileComparator.NAME_COMPARATOR);
		for (int i = 0; i < versions.length; i++)
		{
			// LOGGER.warn (versions[i]);
			// let's skip large files if we need to speed up
			if (speed && versions[i].length () > tooLarge)
			{
				LOGGER.warn ("skipping file ", versions[i], " as it is too large: ",
					versions[i].length ());
				continue;
			}
			System.out.println (modelDir.getName () + " -> " + versions[i]);
			
			// valSBML = new SBMLValidator ();
			if (!valSBML.validate (versions[i]))
			{
				LOGGER.error ("sbml document is not valid? ", versions[i]);
				continue;
			}
			
			ModelDocument doc = valSBML.getDocument ();
			Map<String, Integer> docstats = doc.getTreeDocument ().getNodeStats ();
			
			if (i != 0)
			{
				compare (versions[i - 1], versions[i - 1].getName (), versions[i],
					versions[i].getName (), biomodelsWd, modelDir.getName (), true);
				// doFileStats (versions[i], docstats, modelDir.getName ().startsWith
				// ("BIOM"), versions[i].getName (), versions[i].getName (),
				// modelDir.getName (), false, doc);
			}
			// else
			doFileStats (versions[i], docstats, versions[i].getName (),
				versions[i].getName (), modelDir.getName (), false, doc);
		}
	}
	
	
	/**
	 * Do file stats.
	 *
	 * @param file the file
	 * @param docstats the docstats
	 * @param date the date
	 * @param versionName the version name
	 * @param modelName the model name
	 * @param isCellml the is cellml
	 * @param doc the doc
	 */
	public void doFileStats (File file,
		Map<String, Integer> docstats, String date, String versionName,
		String modelName, boolean isCellml, ModelDocument doc)
	{
		int nUnits = getNumber (docstats.get ("unitDefinition"));
		int nVariables = 0;
		// TODO: is there something like compartments in cellml? -> ask david
		int nComponents = 0;
		int nReactions = getNumber (docstats.get ("reaction"));
		if (isCellml)
		{
			CellMLModel model = ((CellMLDocument) doc).getModel ();
			nReactions = 0;
			nUnits = model.getUnits ().getModelUnits ().size ();
			HashMap<String, CellMLComponent> components = model.getComponents ();
			CellMLUnitDictionary unitDict = model.getUnits ();
			for (String c : components.keySet ())
			{
				CellMLComponent comp = components.get (c);
				nComponents++;
				if (unitDict.getComponentUnits (comp) != null)
					nUnits += unitDict.getComponentUnits (comp).size ();
				nVariables += comp.getVariables ().size ();
				nReactions += comp.getReactions ().size ();
			}
		}
		
		String fileType = isCellml ? "CellML" : "SBML";
		
		try
		{
			fsw.writeLine (doc.getTreeDocument ().getNumNodes (), getNumber (docstats.get ("species")),
				nReactions, getNumber (docstats.get ("compartment")), getNumber (docstats.get ("functionDefinition")), getNumber (docstats.get ("parameter")),
				(getNumber (docstats.get ("assignmentRule"))
					+ getNumber (docstats.get ("rateRule")) + getNumber (docstats
						.get ("algebraicRule"))), getNumber (docstats.get ("event")), nUnits, nVariables, nComponents,
						file.getAbsolutePath ().contains ("BIOMD0000"), fileType, date, versionName, modelName);
		}
		catch (Exception e)
		{
			LOGGER.error (e, "cannot write filestats of ", file);
		}
	}
	
	
	/*
	 * public static final void doFileStats (File file, Map<String, Integer>
	 * docstats, boolean curated, String date, String version, String model)
	 * {
	 * 
	 * try
	 * {
	 * filestatsWriter.write (valSBML.getDocument ().getTreeDocument
	 * ().getNumNodes () + "\t"
	 * + getNumber (docstats.get ("species")) + "\t"
	 * + getNumber (docstats.get ("reaction")) + "\t"
	 * + getNumber (docstats.get ("compartment")) + "\t"
	 * + getNumber (docstats.get ("functionDefinition")) + "\t"
	 * + getNumber (docstats.get ("parameter")) + "\t"
	 * + (getNumber (docstats.get ("assignmentRule")) + getNumber (docstats.get
	 * ("rateRule")) + getNumber (docstats.get ("algebraicRule"))) + "\t"
	 * 
	 * + getNumber (docstats.get ("event")) + "\t"
	 * + getNumber (docstats.get ("unitDefinition")) + "\t"
	 * + getNumber (docstats.get ("variable")) + "\t"
	 * + getNumber (docstats.get ("component")) + "\t"
	 * + curated + "\t"
	 * + "SBML\t"
	 * + date + "\t"
	 * + version + "\t"
	 * + "\"" + model + "\"");
	 * filestatsWriter.newLine ();
	 * }
	 * catch (Exception e)
	 * {
	 * LOGGER.error (e, "cannot write filestats of ", file);
	 * }
	 * }
	 */
	
	


	
	
	/**
	 * Converts a possibly null to an integer. Will return what you provide in most cases. But if you provide a null it returns 0.
	 *
	 * @param i the integer or null
	 * @return the integer number
	 */
	private static final int getNumber (Integer i)
	{
		if (i == null)
			return 0;
		return i;
	}
	
	/**
	 * Compare.
	 *
	 * @param a the a
	 * @param aV the a v
	 * @param b the b
	 * @param bV the b v
	 * @param wDir the w dir
	 * @param modelName the model name
	 * @param sbml the sbml
	 * @return true, if successful
	 */
	public boolean compare (File a, String aV, File b, String bV,
		String wDir, String modelName, boolean sbml)
	{
		DiffResult dr = new DiffResult ();
		
		String compareResult = wDir + File.separatorChar + modelName
			+ File.separatorChar + aV + "__" + bV;
		File wd = new File (wDir + File.separatorChar + modelName);
		wd.mkdirs ();
		
		// compare using unix diff
		try
		{
			File diffUnix = new File (compareResult + "__unixdiff");
			ProcessBuilder pb = new ProcessBuilder ("diff", a.getAbsolutePath (),
				b.getAbsolutePath ());
			pb.directory (wd);
			Process p = pb.start ();
			BufferedReader br = new BufferedReader (new InputStreamReader (
				p.getInputStream ()));
			BufferedWriter bw = new BufferedWriter (new FileWriter (diffUnix));
			String line;
			while ( (line = br.readLine ()) != null)
			{
				if (line.startsWith ("<"))
					dr.incrementTriggeredDeletes ();
				else if (line.startsWith (">"))
					dr.incrementTriggeredInserts ();
				bw.write (line);
				bw.newLine ();
			}
			br.close ();
			bw.close ();
		}
		catch (Exception e)
		{
			LOGGER.error (e, "cannot unix-compare sbml models ", a, " and ", b);
			return false;
		}
		
		// do not include equal files
		if (dr.getUnixDeletes () + dr.getUnixInserts () == 0)
			return false;
		
		// compare using bives
		try
		{
			Diff diff = sbml ? new SBMLDiff (a, b) : new CellMLDiff (a, b);
			diff.mapTrees ();
			GeneralTools.stringToFile (diff.getDiff (), new File (compareResult
				+ "__bivesdiff"));
			Patch patch = diff.getPatch ();
			dr.setBivesInserts (patch.getNumInserts ());
			dr.setBivesDeletes (patch.getNumDeletes ());
			dr.setBivesMoves (patch.getNumMoves ());
			dr.setBivesUpdates (patch.getNumUpdates ());
			
			Element e = patch.getDeletes ();
			for (Element el : e.getChildren ())
				if (el.getAttribute ("triggeredBy") != null)
					dr.incrementTriggeredDeletes ();
			e = patch.getInserts ();
			for (Element el : e.getChildren ())
				if (el.getAttribute ("triggeredBy") != null)
					dr.incrementTriggeredInserts ();
			e = patch.getMoves ();
			for (Element el : e.getChildren ())
				if (el.getAttribute ("triggeredBy") != null)
					dr.incrementTriggeredMoves ();
			e = patch.getUpdates ();
			for (Element el : e.getChildren ())
				if (el.getAttribute ("triggeredBy") != null)
					dr.incrementTriggeredUpdates ();
			
			dr.setXmlNodes (patch.getNumNodeChanges ());
			dr.setXmlAttributes (patch.getNumAttributeChanges ());
			dr.setXmlTexts (patch.getNumTextChanges ());
			
			LOGGER.warn (dr);
		}
		catch (Exception e)
		{
			LOGGER.error (e, "cannot bives-compare sbml models ", a, " and ", b);
			return false;
		}
		
		String modelType = sbml ? "SBML" : "CellML";
		
		try
		{
			dsw.write (dr, modelType, modelName, aV, bV);
		}
		catch (Exception e)
		{
			LOGGER.error (e, "cannot write comparison of ", a, " and ", b);
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Double check file error.
	 *
	 * @param f the f
	 * @param validator the validator
	 */
	private static final void doubleCheckFileError (File f,
		ModelValidator validator)
	{
		if (speed)
			return;
		
		try
		{
			BufferedReader br = new BufferedReader (new FileReader (f));
			while (br.ready ())
			{
				String line = br.readLine ();
				if ( (line.contains ("xmlns=\"http://www.cellml.org/cellml") && line
					.contains ("<model")) || line.contains ("<sbml"))
				{
					LOGGER.error (">>>> error parsing file " + f + "\n\t "
						+ validator.getError ().getMessage ());
				}
			}
			br.close ();
		}
		catch (IOException e)
		{
			System.err.println ("error double checking file error");
			e.printStackTrace ();
		}
	}
}
