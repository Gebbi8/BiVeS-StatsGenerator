/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.io;

import java.io.IOException;



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
			+ "curated\t" + "modeltype\t" + "date\t" + "version\t" + "model");
		writer.newLine ();
	}
	
	
	/**
	 * Write line.
	 * 
	 * @param nodes
	 *          the nodes
	 * @param species
	 *          the species
	 * @param reactions
	 *          the reactions
	 * @param compartments
	 *          the compartments
	 * @param functions
	 *          the functions
	 * @param parameters
	 *          the parameters
	 * @param rules
	 *          the rules
	 * @param events
	 *          the events
	 * @param units
	 *          the units
	 * @param variables
	 *          the variables
	 * @param components
	 *          the components
	 * @param curated
	 *          the curated
	 * @param modelType
	 *          the model type
	 * @param date
	 *          the date
	 * @param version
	 *          the verion
	 * @param model
	 *          the model
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void writeLine (int nodes, int species, int reactions,
		int compartments, int functions, int parameters, int rules, int events,
		int units, int variables, int components, boolean curated,
		String modelType, String date, String version, String model)
		throws IOException
	{
		writer.write (nodes + "\t" + species + "\t" + reactions + "\t"
			+ compartments + "\t" + functions + "\t" + parameters + "\t" + rules
			+ "\t" + events + "\t" + units + "\t" + variables + "\t" + compartments
			+ "\t" + curated + "\t\"" + modelType + "\"\t\"" + date + "\"\t\""
			+ version + "\"\t\"" + model + "\"");
		writer.newLine ();
	}
	
}
