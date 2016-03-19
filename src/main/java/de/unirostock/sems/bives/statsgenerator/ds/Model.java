/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.ds;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.unirostock.sems.bives.ds.ModelDocument;


/**
 * @author martin
 *
 */
public class Model
{
	private String id;
	private String name;
	private Map<Date, ModelVersion> versions;
	
	/**
	 * Instantiates a new model.
	 *
	 * @param id the id
	 */
	public Model (String id)
	{
		this.id = id;
		String [] modelInfo = id.split ("!");
		this.name = modelInfo[1].replaceAll (":", "/");
		versions = new HashMap<Date, ModelVersion> ();
	}
	
	/**
	 * Clean.
	 */
	public void clean ()
	{
		for (ModelVersion v : versions.values ())
			v.clean ();
		versions.clear ();
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName ()
	{
		return name;
	}
	
	/**
	 * Adds the version.
	 *
	 * @param versionId the version id
	 * @param date the date
	 * @param versionFile the version file
	 * @param model the model
	 * @param isSbml the is sbml
	 */
	public void addVersion (String versionId, Date date, File versionFile, ModelDocument model, boolean isSbml)
	{
		versions.put (date, new ModelVersion (versionId, date, versionFile, model, this, isSbml));
	}
	
	/**
	 * Gets the version.
	 *
	 * @param date the date
	 * @return the version
	 */
	public ModelVersion getVersion (Date date)
	{
		return versions.get (date);
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId ()
	{
		return id;
	}
	
	/**
	 * Gets the versions.
	 *
	 * @return the versions
	 */
	public Set<Date> getVersions ()
	{
		return versions.keySet ();
	}
	
	
}
