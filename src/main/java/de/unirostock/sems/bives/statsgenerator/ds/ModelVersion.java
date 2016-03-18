/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.ds;

import java.io.File;



// TODO: Auto-generated Javadoc
/**
 * The Class ModelVersion.
 *
 * @author martin
 */
public class ModelVersion
{
	
	/** The file encoding for that model version. */
	private File versionFile;
	
	/** The sbml-flag -- false means it is cellml. obvious, istn't it? */
	private boolean	sbml;
	
	
	/**
	 * Instantiates a new model version.
	 *
	 * @param file the file
	 * @param isSbml the indicator for SBML -- if set to false it is CellML encoded
	 */
	public ModelVersion (File file, boolean isSbml)
	{
		super ();
		this.versionFile = file;
		this.sbml = isSbml;
	}
	
	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile ()
	{
		return versionFile;
	}
	
	/**
	 * Checks if is sbml.
	 *
	 * @return true, if is sbml
	 */
	public boolean isSbml ()
	{
		return sbml;
	}
	
	/**
	 * Checks if is cellml.
	 *
	 * @return true, if is cellml
	 */
	public boolean isCellml ()
	{
		return !sbml;
	}
	
}
