/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.ds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.cellml.algorithm.CellMLValidator;
import de.unirostock.sems.bives.ds.ModelDocument;
import de.unirostock.sems.bives.sbml.algorithm.SBMLValidator;
import de.unirostock.sems.bives.statsgenerator.App;


/**
 * @author martin
 *
 */
public class InfoJs
{
	private Map<String, Model> models;
	private JSONParser parser;
	private SBMLValidator		valSBML;
	private CellMLValidator	valCellMl;
	
	/**
	 * Instantiates a new info js.
	 *
	 * @param valSBML the val sbml
	 * @param valCellMl the val cell ml
	 */
	public InfoJs (SBMLValidator		valSBML, CellMLValidator	valCellMl)
	{
		this.parser = new JSONParser();
		this.valCellMl = valCellMl;
		this.valSBML = valSBML;
		this.models = new HashMap <String, Model> ();
	}
	
	/**
	 * Gets the models.
	 *
	 * @return the models
	 */
	public Map<String, Model> getModels ()
	{
		return models;
	}
	
	
	/**
	 * Parses the info.js.
	 *
	 * @param file the file
	 * @return true, if that model can be used
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public boolean parseInfoJs (File file) throws FileNotFoundException, IOException, ParseException
	{
		models.clear ();
		parser.reset ();
		Object obj = parser.parse(new FileReader(file));
		JSONObject jsonObject = (JSONObject) obj;
		
		JSONObject models = ((JSONObject)jsonObject.get ("models"));
		for (Object model : models.keySet ())
		{
			String modelId = (String) model;
			String [] modelInfo = modelId.replaceAll (":", "/").split ("!");
			Model m = new Model (modelId);
			
			JSONObject versions = ((JSONObject)((JSONObject)models.get (model)).get ("versions"));
			for (Object v : versions.keySet ())
			{
				String dir = (String) v;
				long time = (Long) versions.get (v);
				if (time < 10000000000L)
					time = time * 1000L;

				File versionFile = new File (file.getParentFile ().getAbsolutePath () + File.separatorChar + dir + File.separatorChar + modelInfo[1]);
				if (App.speed && versionFile.length () > App.tooLarge)
					return false;
				
				ModelDocument modelDoc = null;
				boolean isSbml = false;
				
				if (valSBML.validate (versionFile))
				{
					modelDoc = valSBML.getDocument ();
					isSbml = true;
				}
				else if (valCellMl.validate (versionFile))
				{
					modelDoc = valCellMl.getDocument ();
					isSbml = false;
				}
				else
				{
					LOGGER.warn ("this is neither SBML nor CellML!?: ", versionFile);
					continue;
				}
				
				m.addVersion (dir, new Date (time), versionFile, modelDoc, isSbml);
			}
			this.models.put (modelId, m);
		}
		return true;
	}
	
}
