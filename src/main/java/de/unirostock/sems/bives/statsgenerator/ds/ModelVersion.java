/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.ds;

import java.io.File;
import java.util.Date;

import de.unirostock.sems.bives.cellml.parser.CellMLDocument;
import de.unirostock.sems.bives.ds.ModelDocument;
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;



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
	
	private ModelDocument modelDoc;
	
	private Date versionDate;
	private String versionId;
	private Model model;
	
	
	/**
	 * Instantiates a new model version.
	 *
	 * @param versionId the version id
	 * @param versionDate the version date
	 * @param file the file
	 * @param modelDoc the model doc
	 * @param model the model
	 * @param isSbml the indicator for SBML -- if set to false it is CellML encoded
	 */
	public ModelVersion (String versionId, Date versionDate, File file, ModelDocument modelDoc, Model model, boolean isSbml)
	{
		super ();
		this.versionId = versionId;
		this.versionDate = versionDate;
		this.modelDoc = modelDoc;
		this.versionFile = file;
		this.sbml = isSbml;
		this.model = model;
	}
	
	/**
	 * Gets the version id.
	 *
	 * @return the version id
	 */
	public String getVersionId ()
	{
		return versionId;
	}
	
	/**
	 * Gets the version date.
	 *
	 * @return the version date
	 */
	public Date getVersionDate ()
	{
		return versionDate;
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
	 * Gets the model document.
	 *
	 * @return the model document
	 */
	public ModelDocument getModelDocument ()
	{
		return modelDoc;
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
	
	
	public String toString ()
	{
		return versionFile.getAbsolutePath () + " [" + versionId + " -- " + versionDate + "]";
	}

	/**
	 * Gets the xml model name.
	 *
	 * @return the xml model name
	 */
	public String getXmlModelName ()
	{
		if (isSbml ())
		{
			SBMLDocument doc = (SBMLDocument)getModelDocument ();
			return doc.getModel ().getName ();
		}

		if (isCellml ())
		{
			CellMLDocument doc = (CellMLDocument)getModelDocument ();
			return doc.getModel ().getName ();
		}
		return "unknown";
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl ()
	{
		if (model.getId ().startsWith ("urn:model:models.cellml.org"))
		{
			return "http://" + model.getId ().substring (10).replaceAll (":", "/").replaceFirst ("!", "file/" + getVersionId ());
		}
		else if (model.getId ().startsWith ("urn:model:ftp.ebi.ac.uk"))
		{
			String n = model.getName ();
			return "http://www.ebi.ac.uk/biomodels-main/" + n.substring (0, n.length () - 4);
		}
		return "unknown";
	}

	/**
	 * Clean.
	 */
	public void clean ()
	{
		modelDoc = null;
	}
	
}
