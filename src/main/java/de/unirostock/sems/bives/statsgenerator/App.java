package de.unirostock.sems.bives.statsgenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.jdom2.Element;
import org.jdom2.JDOMException;

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
import de.unirostock.sems.bives.exception.BivesDocumentConsistencyException;
import de.unirostock.sems.bives.sbml.algorithm.SBMLValidator;
import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.sbml.exception.BivesSBMLParseException;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;

/**
 * Hello world!
 *
 */
public class App 
{
	private final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private final static String [] PMR_BLACKLIST = new String [] {"vm", "png", "bmp", "jpg", "jpeg", "html", "htm", "xhtml", "svg", "pdf", "json", "pl", "rdf", "rar", "msh"};
	
	public static final String SOURCE = "/srv/modelcrawler/statswebsite/copy/storage";
	public static final String WORKING = "/srv/modelcrawler/statswebsite/copy/workingdir";
	
	public static BufferedWriter diffWriter;
	public static BufferedWriter filestatsWriter;
	public static BufferedWriter errorLog;
	
	public static long tooLarge = 20 * 1024 * 1024; // 1mb
	private final static SBMLValidator valSBML = new SBMLValidator ();
	private final static CellMLValidator valCellMl = new CellMLValidator ();
	
	private static boolean speed = false;
	
    public static void main( String[] args ) throws IOException
    {
    	new File (WORKING).mkdirs ();
    	
    	speed = true;
    	LOGGER.setMinLevel (LOGGER.WARN);
    	LOGGER.setLogFile (new File (WORKING + "/bflog"));
    	
    	errorLog = new BufferedWriter (new FileWriter (WORKING + "/errorlog"));
    	
    	filestatsWriter = new BufferedWriter (new FileWriter (WORKING + "/filestats"));
    	filestatsWriter.write ("#nodes\t"
    		+ "#species\t"
    		+ "#reactions\t"
    		+ "#compartments\t"
    		+ "#functions\t"
    		+ "#parameters\t"
    		+ "#rules\t"
    		+ "#events\t"
    		+ "#units\t"
    		+ "#variables\t"
    		+ "#components\t"
    		+ "curated\t"
    		+ "modeltype\t"
    		+ "date\t"
    		+ "version\t"
    		+ "model");
    	filestatsWriter.newLine ();
    	
    	diffWriter = new BufferedWriter (new FileWriter (WORKING + "/diffstats"));
    	diffWriter.write ("unix\t"
    		+ "unixinsert\t"
    		+ "unixdelete\t"
    		+ "bives\t"
    		+ "bivesinsert\t"
    		+ "bivesdelete\t"
    		+ "bivesmove\t"
    		+ "bivesupdate\t"
    		+ "bivestriggeredinsert\t"
    		+ "bivestriggereddelete\t"
    		+ "bivestriggeredmove\t"
    		+ "bivestriggeredupdate\t"
    		+ "bivesnode\t"
    		+ "bivesattribute\t"
    		+ "bivestext\t"
    		+ "modeltype\t"
    		+ "model\t"
    		+ "version1\t"
    		+ "version2");
    	diffWriter.newLine ();

    	
    	String biomodelsSource = SOURCE + "/biomodels-clone/remodeled";
    	String biomodelsWorking = WORKING + "/biomodels";
    	String cellmlSource = SOURCE + "/cellml-clone/remodeled";
    	String cellmlWorking = WORKING + "/cellml";
    	
    	processBiomodels (biomodelsSource, biomodelsWorking);
    	processCellml (cellmlSource, cellmlWorking);
    	
    	filestatsWriter.flush ();
    	filestatsWriter.close ();
    	
    	diffWriter.flush ();
    	diffWriter.close ();
    	
    	errorLog.close ();
    	LOGGER.closeLogger ();
    }
    
    public static void processCellml (String cellmlDir, String cellmlWd)
    {
    	File repositories = new File (cellmlDir);
    	for (File repository : repositories.listFiles ())
    	{
    		processCellmlRepo (repository, cellmlWd);
    	}
    }
    
    public static void processCellmlRepo (File repository, String cellmlWd)
    {
    	Map<String, HashMap<String, ModelVersion>> versionBank = new HashMap<String, HashMap<String, ModelVersion>> ();
    	
  			LOGGER.info (">>>>   processing repository ", repository.getName ());
  			System.out.println (">>>>   processing repository " + repository.getName ());
    	String repoName = new String (GeneralTools.decodeBase64 (repository.getName ()));
    	while (repoName.endsWith ("\n"))
    		repoName = repoName.substring (0, repoName.length () - 1);
    	for (File version : repository.listFiles ())
  		{
  			LOGGER.info ("-- processing version ", version.getName ());
  			System.out.println ("-- processing version " + version.getName ());
  			HashMap<String, ModelVersion> models = processCellMlRepoVersion (new HashMap<String, ModelVersion> (), repoName, version.getName (), version, version.getAbsolutePath ().length ());
  			if (models.size () > 0)
  				versionBank.put (version.getName (), models);
  		}
  		
  		
  		
  		doCellMlVersionStats (versionBank, cellmlWd);
    }
    
    
    private static void doCellmlFileStats (ModelVersion aV, String versionName, String modelName)
    {
    	
    	
    	
    	ModelDocument doc = null;
    	if (aV.sbml)
    	{
    		valSBML.validate (aV.file);
    		doc = valSBML.getDocument ();
    	}
    	else
    	{
    		valCellMl.validate (aV.file);
    		doc = valCellMl.getDocument ();
    	}
	    		Map<String, Integer> docstats = doc.getTreeDocument ().getNodeStats ();
					doFileStats (aV.file, docstats, 
			    	formatter.format (new Date (Long.parseLong (versionName)*1000L)),
			    	versionName,
			    	modelName, !aV.sbml, doc);
    	
    }
    
    
    private static void doCellMlVersionStats (
			Map<String, HashMap<String, ModelVersion>> versionBank, String cellmlWd)
		{
  		Object [] keys = versionBank.keySet().toArray();
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
  	    		if (compare (aV.file, formatter.format (new Date (Long.parseLong ((String) keys[i - 1])*1000L)), bV.file, formatter.format (new Date (Long.parseLong ((String) keys[1])*1000L)), cellmlWd, model, aV.sbml))
  	    		{
  	    			//filestats of b if it differs from a
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
    
    private static class ModelVersion
    {
    	public File file;
    	public boolean sbml;
			public ModelVersion (File file, boolean sbml)
			{
				super ();
				this.file = file;
				this.sbml = sbml;
			}
    }

		private static HashMap<String, ModelVersion> processCellMlRepoVersion (HashMap<String, ModelVersion> files, String repo, String versionName, File versionDir, int discardStart)
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
      			LOGGER.warn ("skipping file ", file, " as it is too large: ", file.length ());
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
  				
  				String modelName = repo + file.getAbsolutePath ().substring (discardStart);
  				
  				// try to read the file
  				boolean isCellml = valCellMl.validate (file);
  				boolean isSbml = isCellml ? false : valSBML.validate (file);
  				
  				if (isCellml || isSbml)
  				{
//  					try
//  					{
  						System.out.println ("is cellml or sbml: " + isCellml + " -- " + isSbml);
//  						ModelDocument doc = isCellml ? valCellMl.getDocument () : valSBML.getDocument ();
  						files.put (modelName, new ModelVersion (file, isSbml));
  						
  		    		
  		    		/*int nUnits = getNumber (docstats.get ("unitDefinition"));
  		    		int nVariables = 0;
  		    		// TODO: is there something like compartments in cellml? -> ask david
  		    		int nComponents = 0;
  		    		int nReactions = getNumber (docstats.get ("reaction"));
  		    		if (isCellml)
  		    		{
  		    			CellMLModel model = ((CellMLDocument)doc).getModel ();
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
  		    		
  		    		filestatsWriter.write (doc.getTreeDocument ().getNumNodes () + "\t"
  			    		+	getNumber (docstats.get ("species")) + "\t"
  			    		+ nReactions + "\t"
  				    	+ getNumber (docstats.get ("compartment")) + "\t"
  				    	+ getNumber (docstats.get ("functionDefinition")) + "\t"
  			    		+ getNumber (docstats.get ("parameter")) + "\t"
  			    		+ (getNumber (docstats.get ("assignmentRule")) + getNumber (docstats.get ("rateRule")) + getNumber (docstats.get ("algebraicRule"))) + "\t"
  			    		
  			    		+ getNumber (docstats.get ("event")) + "\t"
  			      	+ nUnits + "\t"
  			      	+ nVariables + "\t"
  			      	+ nComponents + "\t"
  			      	+ file.getName ().startsWith ("BIOM") + "\t"
  					    + (isCellml ? "CellML\t" : "SBML\t")
  				    	+ formatter.format (new Date (Long.parseLong (versionName)*1000L)) + "\t"
  			    		+ versionName + "\t"
  			    		+ "\"" + modelName + "\"");
  		    		filestatsWriter.newLine ();*/
//  					}
//  					catch (IOException e)
//  					{
//  						LOGGER.error (e, "cannot write stats to disk: ", file.getName ());
//  					}
  				}
  				else if (file.getName ().endsWith ("cellml"))
  				{
  					doubleCheckFileError (file, valCellMl);
  				}
  			}
  		}
  		return files;
		}

		public static void processBiomodels (String biomodelsDir, String biomodelsWd)
    {
    	File biomodels = new File (biomodelsDir);
    	for (File model : biomodels.listFiles ())
    	{
    		processBiomodelsModel (model, biomodelsWd);
    	}
    }
    
    
    public static void processBiomodelsModel (File modelDir, String biomodelsWd)
    {
    	File[] versions = modelDir.listFiles((FileFilter) FileFileFilter.FILE);
    	Arrays.sort(versions, NameFileComparator.NAME_COMPARATOR);
    	for (int i = 0; i < versions.length; i++)
    	{
    		//LOGGER.warn (versions[i]);
    		// let's skip large files if we need to speed up
    		if (speed && versions[i].length () > tooLarge)
    		{
    			LOGGER.warn ("skipping file ", versions[i], " as it is too large: ", versions[i].length ());
    			continue;
    		}
    		System.out.println (modelDir.getName () + " -> " + versions[i]);
    		
    		//valSBML = new SBMLValidator ();
    		if (!valSBML.validate (versions[i]))
    		{
    			LOGGER.error ("sbml document is not valid? ", versions[i]);
    			continue;
    		}
    		
    		ModelDocument doc = valSBML.getDocument ();
    		Map<String, Integer> docstats = doc.getTreeDocument ().getNodeStats ();

    		
    		if (i != 0)
    		{
    			compare (versions[i - 1], versions[i - 1].getName (), versions[i], versions[i].getName (), biomodelsWd, modelDir.getName (), true);
//    			doFileStats (versions[i], docstats, modelDir.getName ().startsWith ("BIOM"), versions[i].getName (), versions[i].getName (), modelDir.getName (), false, doc);
    		}
//    		else
    			doFileStats (versions[i], docstats, versions[i].getName (), versions[i].getName (), modelDir.getName (), false, doc);
    	}
    }
    
    public static final void doFileStats (File file, Map<String, Integer> docstats, String date, String versionName, String modelName, boolean isCellml, ModelDocument doc)
    {
    	int nUnits = getNumber (docstats.get ("unitDefinition"));
  		int nVariables = 0;
  		// TODO: is there something like compartments in cellml? -> ask david
  		int nComponents = 0;
  		int nReactions = getNumber (docstats.get ("reaction"));
  		if (isCellml)
  		{
  			CellMLModel model = ((CellMLDocument)doc).getModel ();
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

			try
			{
	  		filestatsWriter.write (doc.getTreeDocument ().getNumNodes () + "\t"
	    		+	getNumber (docstats.get ("species")) + "\t"
	    		+ nReactions + "\t"
		    	+ getNumber (docstats.get ("compartment")) + "\t"
		    	+ getNumber (docstats.get ("functionDefinition")) + "\t"
	    		+ getNumber (docstats.get ("parameter")) + "\t"
	    		+ (getNumber (docstats.get ("assignmentRule")) + getNumber (docstats.get ("rateRule")) + getNumber (docstats.get ("algebraicRule"))) + "\t"
	    		
	    		+ getNumber (docstats.get ("event")) + "\t"
	      	+ nUnits + "\t"
	      	+ nVariables + "\t"
	      	+ nComponents + "\t"
	      	+ file.getAbsolutePath ().contains ("BIOMD0000") + "\t"
			    + (isCellml ? "CellML\t" : "SBML\t")
		    	+ date + "\t"
	    		+ versionName + "\t"
	    		+ "\"" + modelName + "\"");
	  		filestatsWriter.newLine ();
			}
			catch (Exception e)
			{
				LOGGER.error (e, "cannot write filestats of ", file);
			}
    }
    
   /* public static final void doFileStats (File file, Map<String, Integer> docstats, boolean curated, String date, String version, String model)
    {

			try
			{
    		filestatsWriter.write (valSBML.getDocument ().getTreeDocument ().getNumNodes () + "\t"
	    		+	getNumber (docstats.get ("species")) + "\t"
	    		+ getNumber (docstats.get ("reaction")) + "\t"
	    		+ getNumber (docstats.get ("compartment")) + "\t"
		    	+ getNumber (docstats.get ("functionDefinition")) + "\t"
	    		+ getNumber (docstats.get ("parameter")) + "\t"
	    		+ (getNumber (docstats.get ("assignmentRule")) + getNumber (docstats.get ("rateRule")) + getNumber (docstats.get ("algebraicRule"))) + "\t"
	    		
	    		+ getNumber (docstats.get ("event")) + "\t"
	      	+ getNumber (docstats.get ("unitDefinition")) + "\t"
	      	+ getNumber (docstats.get ("variable")) + "\t"
	      	+ getNumber (docstats.get ("component")) + "\t"
	      	+ curated + "\t"
			    + "SBML\t"
		    	+ date + "\t"
	    		+ version + "\t"
	    		+ "\"" + model + "\"");
    		filestatsWriter.newLine ();
			}
			catch (Exception e)
			{
				LOGGER.error (e, "cannot write filestats of ", file);
			}
    }*/
    
    
    public static final boolean compare (File a, String aV, File b, String bV, String wDir, String modelName, boolean sbml)
    {
  		DiffResult dr = new DiffResult ();
  		
  		String compareResult = wDir + File.separatorChar + modelName + File.separatorChar
  			+ aV + "__" + bV;
  		File wd = new File (wDir + File.separatorChar + modelName );
  		wd.mkdirs ();
  		
  		// compare using unix diff
  		try
  		{
  		File diffUnix = new File (compareResult + "__unixdiff");
			ProcessBuilder pb = new ProcessBuilder("diff", a.getAbsolutePath (), b.getAbsolutePath ());
			pb.directory(wd);
			Process p = pb.start();
			BufferedReader br = new BufferedReader (new InputStreamReader(p.getInputStream()));
			BufferedWriter bw = new BufferedWriter (new FileWriter (diffUnix));
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith ("<"))
					dr.unixDelete++;
				else if (line.startsWith (">"))
					dr.unixInsert++;
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
  		if (dr.unixDelete + dr.unixInsert == 0)
  			return false;
  		
  		
  		// compare using bives
			try
			{
				Diff diff = sbml ? new SBMLDiff (a, b) : new CellMLDiff (a, b);
    		diff.mapTrees ();
    		GeneralTools.stringToFile (diff.getDiff (), new File (compareResult + "__bivesdiff"));
    		Patch patch = diff.getPatch ();
    		dr.ins = patch.getNumInserts ();
    		dr.del = patch.getNumDeletes ();
    		dr.mov = patch.getNumMoves ();
    		dr.up = patch.getNumUpdates ();
    		
    		
    		
    		Element e = patch.getDeletes ();
    		for (Element el : e.getChildren ())
    			if (el.getAttribute ("triggeredBy") != null)
    				dr.trDel++;
    		e = patch.getInserts ();
    		for (Element el : e.getChildren ())
    			if (el.getAttribute ("triggeredBy") != null)
    				dr.trIns++;
    		e = patch.getMoves ();
    		for (Element el : e.getChildren ())
    			if (el.getAttribute ("triggeredBy") != null)
    				dr.trMov++;
    		e = patch.getUpdates ();
    		for (Element el : e.getChildren ())
    			if (el.getAttribute ("triggeredBy") != null)
    				dr.trUp++;
    		
    		

    		dr.nodes = patch.getNumNodeChanges ();
    		dr.attr = patch.getNumAttributeChanges ();
    		dr.texts = patch.getNumTextChanges ();
    		
    		
    		LOGGER.warn (dr);
			}
			catch (Exception e)
			{
				LOGGER.error (e, "cannot bives-compare sbml models ", a, " and ", b);
				return false;
			}
			
			
			try
			{
				diffWriter.append (dr.toString () + (sbml ? "SBML\t" : "CellML\t")
					+ "\"" + modelName + "\"\t"
					+ aV + "\t"
					+ bV);
				
	    	diffWriter.newLine ();
			}
			catch (Exception e)
			{
				LOGGER.error (e, "cannot write comparison of ", a, " and ", b);
				return false;
			}
			
			
			return true;
    }
    
    
    
    
    
    
    
    
    
    
    private static final int getNumber (Integer i)
  	{
  		if (i == null)
  			return 0;
  		return i;
  	}
    
    private static final void doubleCheckFileError (File f, ModelValidator validator)
    {
    	if (speed)
    		return;
    	
    	try
    	{
	    	BufferedReader br = new BufferedReader (new FileReader (f));
	    	while (br.ready ())
	    	{
	    		String line = br.readLine ();
	    		if ((line.contains ("xmlns=\"http://www.cellml.org/cellml")
	    			&& line.contains ("<model")) || line.contains ("<sbml"))
	    		{
	    			errorLog.write (">>>> error parsing file " + f + "\n\t " + validator.getError ().getMessage ());
	    			errorLog.newLine ();
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
