/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.io;

import java.io.IOException;

import de.unirostock.sems.bives.statsgenerator.ds.DiffResult;


/**
 * The Class DiffStatsWriter.
 *
 * @author martin
 */
public class DiffStatsWriter
	extends StatsWriter
{
	
	/**
	 * Instantiates a new diff stats writer.
	 * 
	 * @param fileName
	 *          the name of the file to write to
	 */
	public DiffStatsWriter (String fileName)
	{
		super (fileName);
	}
	
	
	/* (non-Javadoc)
	 * @see de.unirostock.sems.bives.statsgenerator.io.StatsWriter#writeHeader()
	 */
	@Override
	public void writeHeader () throws IOException
	{
		writer.write ("unix\t" + "unixinsert\t" + "unixdelete\t" + "bives\t"
			+ "bivesinsert\t" + "bivesdelete\t" + "bivesmove\t" + "bivesupdate\t"
			+ "bivestriggeredinsert\t" + "bivestriggereddelete\t"
			+ "bivestriggeredmove\t" + "bivestriggeredupdate\t" + "bivesnode\t"
			+ "bivesattribute\t" + "bivestext\t" + "modeltype\t" + "model\t"
			+ "version1\t" + "version2");
		writer.newLine ();
	}
	
	
	/**
	 * Write.
	 *
	 * @param dr the dr
	 * @param modelType the model type
	 * @param modelName the model name
	 * @param originalVersion the original version
	 * @param modifiedVersion the modified version
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void write (DiffResult dr, String modelType, String modelName, String originalVersion, String modifiedVersion) throws IOException
	{
		writer.append (

			(dr.getUnixInserts () + dr.getUnixDeletes ()) + "\t" + 
			dr.getUnixInserts () + "\t" + 
			dr.getUnixDeletes () + "\t" + 
			
			(dr.getBivesDeletes () + 
			dr.getBivesInserts () + 
			dr.getBivesMoves () + 
			dr.getBivesUpdates ()) +"\t" + 
			 
			dr.getBivesInserts () +"\t" + 
			dr.getBivesDeletes () + "\t" + 
			dr.getBivesMoves () + "\t" + 
			dr.getBivesUpdates () + "\t" + 
			
			dr.getTriggeredInserts () + "\t" + 
			dr.getTriggeredDeletes () + "\t" + 
			dr.getTriggeredMoves () + "\t" + 
			dr.getTriggeredUpdates () +"\t" + 
			
			dr.getXmlNodes () +"\t" + 
			dr.getXmlAttributes () +"\t" + 
			dr.getXmlTexts () +  "\t" + 
			"\"" + modelType + "\"\t" +
			"\"" + modelName + "\"\t" +
			"\"" + originalVersion + "\"\t" + 
			"\"" + modifiedVersion + "\"");
	}
	
}
