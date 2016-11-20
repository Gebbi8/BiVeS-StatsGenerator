/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Statement;
import org.jdom2.Element;
import org.json.simple.parser.ParseException;

import de.binfalse.bflog.LOGGER;
import de.binfalse.bfutils.GeneralTools;
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
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;
import de.unirostock.sems.bives.statsgenerator.ds.ComodiTermCounter;
import de.unirostock.sems.bives.statsgenerator.ds.DiffResult;
import de.unirostock.sems.bives.statsgenerator.ds.InfoJs;
import de.unirostock.sems.bives.statsgenerator.ds.Model;
import de.unirostock.sems.bives.statsgenerator.ds.ModelVersion;
import de.unirostock.sems.bives.statsgenerator.io.DiffStatsWriter;
import de.unirostock.sems.bives.statsgenerator.io.FileStatsWriter;
import de.unirostock.sems.comodi.Change;



/**
 * The Class BiomodelsProcessor.
 * 
 * @author martin
 */
public class RepositoryProcessor
{
	
	/** The source dir. */
	private File						storageDir;
	
	/** The working dir. */
	private File						globalWorkingDir;
	/** The fsw. */
	private FileStatsWriter	fsw;
	
	/** The dsw. */
	private DiffStatsWriter	dsw;
	private InfoJs info;
	private List<Date> modelVersions;
	
	private Map<String, ComodiTermCounter> comodiTerms;
	
	/**
	 * Instantiates a new biomodels processor.
	 *
	 * @param storageDir the storage dir
	 * @param workingDir the working dir
	 * @param valSBML the val sbml
	 * @param valCellMl the val cell ml
	 * @param fsw the fsw
	 * @param dsw the dsw
	 * @param comodiTerms the comodi terms
	 */
	public RepositoryProcessor (File storageDir, File workingDir,
		SBMLValidator valSBML, CellMLValidator valCellMl, FileStatsWriter fsw,
		DiffStatsWriter dsw, Map<String, ComodiTermCounter> comodiTerms)
	{
		this.info = new InfoJs (valSBML, valCellMl);
		
		this.storageDir = storageDir;
		this.globalWorkingDir = workingDir;
		
		this.fsw = fsw;
		this.dsw = dsw;
		
		this.modelVersions = new ArrayList<Date> ();
		
		this.comodiTerms = comodiTerms;
	}
	
	
	/**
	 * Process.
	 */
	public void process ()
	{
		process (storageDir);
	}
	
	
	/**
	 * Process biomodels.
	 * 
	 * @param dir
	 *          the dir
	 */
	public void process (File dir)
	{
		for (File f : dir.listFiles ())
		{
			if (f.isDirectory ())
				process (f);
			if (f.isFile () && f.getName ().equals ("info.json"))
				try
				{
					processWorkingDir (f);
				}
				catch (Exception e)
				{
					LOGGER.error (e, "couldn't process working directory for ",
						f.getAbsolutePath ());
				}
		}
	}
	
	
	/**
	 * Process model.
	 * 
	 * @param f
	 *          the f
	 * @throws FileNotFoundException
	 *           the file not found exception
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *           the parse exception
	 */
	public void processWorkingDir (File f)
		throws FileNotFoundException,
			IOException,
			ParseException
	{
		// File modelDir = f.getParentFile ();
		if (!info.parseInfoJs (f))
		{
			LOGGER.warn ("ignoring working directory for ",
				f.getAbsolutePath (), " bause parsing info told me so");
			return;
		}
		Map<String, Model> models = info.getModels ();
		for (String m : models.keySet ())
		{
			LOGGER.info ("processing ", m);
			processModel (models.get (m));
		}
		clean (models);
	}

	
	
	/**
	 * Clean.
	 * 
	 * @param models
	 *          the models
	 */
	public static void clean (final Map<String, Model> models)
	{
		for (Model m : models.values ())
			m.clean ();
		Runtime.getRuntime ().gc ();
	}

	
	/**
	 * Process model.
	 * 
	 * @param m
	 *          the m
	 */
	public void processModel (Model m)
	{
		System.out.println (m.getId ());
		modelVersions.clear ();
		modelVersions.addAll (m.getVersions ());
		Collections.sort (modelVersions);
		for (int i = 0; i < modelVersions.size (); i++)
		{
			ModelVersion modelVersion = m.getVersion (modelVersions.get (i));
			System.out.println ("  -> " + modelVersions.get (i));
			
			// do filestats
			generateFileStats (modelVersion, modelVersions.get (i), m);
			
			if (i > 0)
			{
				// do model stats
				ModelVersion originalVersion = m.getVersion (modelVersions.get (i - 1));
				
				File wd = new File (globalWorkingDir.getAbsolutePath ()
					+ File.separatorChar + fixFileName (m.getId ()));
				compare (originalVersion, modelVersion, m, wd);
			}
		}
	}
	
	
	private String fixFileName (String name)
	{
		return name.replaceAll ("[^a-zA-Z0-9]", "_");
	}
	
	
	/**
	 * Compare.
	 * 
	 * @param originalVersion
	 *          the original version
	 * @param modifiedVersion
	 *          the modified version
	 * @param m
	 *          the m
	 * @param workingDir
	 *          the working dir
	 * @return true, if successful
	 */
	public boolean compare (ModelVersion originalVersion,
		ModelVersion modifiedVersion, Model m, File workingDir)
	{
		if (originalVersion.isSbml () != modifiedVersion.isSbml ())
		{
			LOGGER.error ("cannot compare different model types ", originalVersion,
				" (", originalVersion.isSbml (), ") and ", modifiedVersion, " (",
				modifiedVersion.isSbml (), ")");
			return false;
		}
		
		DiffResult dr = new DiffResult ();
		
		workingDir.mkdirs ();
		String compareResult = workingDir.getAbsolutePath () + File.separatorChar
			+ originalVersion.getVersionId () + "__"
			+ modifiedVersion.getVersionId ();
		
		LOGGER.debug ("  --> unix:");
		// compare using unix diff
		try
		{
			File diffUnix = new File (compareResult + "__unixdiff");
			ProcessBuilder pb = new ProcessBuilder ("diff", originalVersion
				.getFile ().getAbsolutePath (), modifiedVersion.getFile ()
				.getAbsolutePath ());
			pb.directory (workingDir);
			Process p = pb.start ();
			BufferedReader br = new BufferedReader (new InputStreamReader (
				p.getInputStream ()));
			BufferedWriter bw = new BufferedWriter (new FileWriter (diffUnix));
			String line;
			while ( (line = br.readLine ()) != null)
			{
				if (line.startsWith ("<") || line.startsWith ("-"))
					dr.incrementUnixDeletes ();
				else if (line.startsWith (">") || line.startsWith ("+"))
					dr.incrementUnixInserts ();
				bw.write (line);
				bw.newLine ();
			}
			br.close ();
			bw.close ();
		}
		catch (Exception e)
		{
			LOGGER.error (e, "cannot unix-compare sbml models ", originalVersion,
				" and ", modifiedVersion);
			return false;
		}
		LOGGER.debug  ("  -->     " + (dr.getUnixDeletes () + dr.getUnixInserts ()));
		
		// do not include equal files
		if (dr.getUnixDeletes () + dr.getUnixInserts () == 0)
			return false;

		LOGGER.debug  ("  --> bives:");
		// compare using bives
		try
		{
			Diff diff = originalVersion.isSbml () ? new SBMLDiff (
				(SBMLDocument) originalVersion.getModelDocument (),
				(SBMLDocument) modifiedVersion.getModelDocument ()) : new CellMLDiff (
				(CellMLDocument) originalVersion.getModelDocument (),
				(CellMLDocument) modifiedVersion.getModelDocument ());
			diff.mapTrees ();
			GeneralTools.stringToFile (diff.getDiff (), new File (compareResult
				+ "__bivesdiff"));
			Patch patch = diff.getPatch ();
			dr.setBivesInserts (patch.getNumInserts ());
			dr.setBivesDeletes (patch.getNumDeletes ());
			dr.setBivesMoves (patch.getNumMoves ());
			dr.setBivesUpdates (patch.getNumUpdates ());
			
			for (Change change: patch.getAnnotations ().getChanges ())
				for (Statement s : change.getStatements ())
					if (s.getObject ().toString ().contains ("comodi"))
					{
						//System.out.println (s.getObject ().toString ());
						ComodiTermCounter ctc = comodiTerms.get (s.getObject ().toString ());
						if (ctc == null)
						{
							ctc = new ComodiTermCounter (s.getObject ().toString ());
							comodiTerms.put (s.getObject ().toString (), ctc);
						}
						if (originalVersion.isSbml ())
							ctc.nSBML++;
						if (originalVersion.isCellml ())
							ctc.nCellMl++;
					}
			
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
			
//			LOGGER.warn (dr);
			LOGGER.debug  ("  -->     " + (patch.getNumInserts ()+patch.getNumDeletes ()+patch.getNumMoves ()+patch.getNumUpdates ()));
		}
		catch (Exception e)
		{
			LOGGER.error (e, "cannot bives-compare sbml models ", originalVersion,
				" and ", modifiedVersion);
			return false;
		}
		
		String modelType = originalVersion.isSbml () ? "SBML" : "CellML";
		
		try
		{
			dsw.write (dr,
			 modelType,
				m.getName (),
				// originalVersion.getVersionDate (), modifiedVersion.getVersionDate (),
				originalVersion.getVersionId (), modifiedVersion.getVersionId ());
		}
		catch (Exception e)
		{
			LOGGER.error (e, "cannot write comparison of ", originalVersion, " and ",
				modifiedVersion);
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Do file stats.
	 * 
	 * @param modelVersion
	 *          the model version
	 * @param date
	 *          the date
	 * @param m
	 *          the m
	 */
	public void generateFileStats (ModelVersion modelVersion, Date date, Model m)
	{
		Map<String, Integer> docstats = modelVersion.getModelDocument ()
			.getTreeDocument ().getNodeStats ();
		ModelDocument doc = modelVersion.getModelDocument ();
		
		int nUnits = getNumber (docstats.get ("unitDefinition"));
		int nVariables = 0;
		int nComponents = 0;
		int nReactions = getNumber (docstats.get ("reaction"));
		String fileType = "SBML";
		if (modelVersion.isCellml ())
		{
			fileType = "CellML";
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
		
		try
		{
			fsw.writeLine (
				doc.getTreeDocument ().getNumNodes (),
				getNumber (docstats.get ("species")),
				nReactions,
				getNumber (docstats.get ("compartment")),
				getNumber (docstats.get ("functionDefinition")),
				getNumber (docstats.get ("parameter")),
				(getNumber (docstats.get ("assignmentRule"))
					+ getNumber (docstats.get ("rateRule")) + getNumber (docstats
					.get ("algebraicRule"))), getNumber (docstats.get ("event")), nUnits,
				nVariables, nComponents, modelVersion.getFile ().getAbsolutePath ()
					.contains ("BIOMD0000"), fileType, date,
				modelVersion.getVersionId (), m.getName (), modelVersion
					.getXmlModelName (), modelVersion.getUrl ());
		}
		catch (Exception e)
		{
			LOGGER.error (e, "cannot write filestats of ", modelVersion.getFile ());
		}
	}
	
	
	/**
	 * Converts a possibly null to an integer. Will return what you provide in
	 * most cases. But if you provide a null it returns 0.
	 * 
	 * @param i
	 *          the integer or null
	 * @return the integer number
	 */
	private static final int getNumber (Integer i)
	{
		if (i == null)
			return 0;
		return i;
	}
}
