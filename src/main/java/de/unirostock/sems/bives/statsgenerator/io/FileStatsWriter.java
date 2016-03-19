/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.io;

import java.io.IOException;
import java.util.Date;

import de.unirostock.sems.bives.statsgenerator.App;



/**
 * @author martin
 * 
 */
public class FileStatsWriter
	extends StatsWriter
{
	
	/**
	 * Instantiates a new file stats writer.
	 * 
	 * @param fileName
	 *          the name of the file to write to
	 */
	public FileStatsWriter (String fileName)
	{
		super (fileName);
	}
	
	
	@Override
	public void writeHeader () throws IOException
	{
		writer.write ("#nodes\t" + "#species\t" + "#reactions\t"
			+ "#compartments\t" + "#functions\t" + "#parameters\t" + "#rules\t"
			+ "#events\t" + "#units\t" + "#variables\t" + "#components\t"
			+ "curated\t" + "modeltype\t" + "date\t" + "versionid\t" + "model\t" + "modelname\t" + "url");
		writer.newLine ();
	}
	
	
	/**
	 * Write line.
	 *
	 * @param nodes          the nodes
	 * @param species          the species
	 * @param reactions          the reactions
	 * @param compartments          the compartments
	 * @param functions          the functions
	 * @param parameters          the parameters
	 * @param rules          the rules
	 * @param events          the events
	 * @param units          the units
	 * @param variables          the variables
	 * @param components          the components
	 * @param curated          the curated
	 * @param modelType          the model type
	 * @param date          the date
	 * @param version          the verion
	 * @param model          the model
	 * @param modelName the model name
	 * @param url the url
	 * @throws IOException           Signals that an I/O exception has occurred.
	 */
	public void writeLine (int nodes, int species, int reactions,
		int compartments, int functions, int parameters, int rules, int events,
		int units, int variables, int components, boolean curated,
		String modelType, Date date, String version, String model, String modelName, String url)
		throws IOException
	{
		writer.write (nodes + "\t" + species + "\t" + reactions + "\t"
			+ compartments + "\t" + functions + "\t" + parameters + "\t" + rules
			+ "\t" + events + "\t" + units + "\t" + variables + "\t" + components
			+ "\t" + curated + "\t\"" + modelType + "\"\t\"" + App.formatter.format (date) + "\"\t\""
				+ version + "\"\t\"" + model + "\"\t\"" + modelName + "\"\t\"" + url + "\"");
		writer.newLine ();
	}
	
}
