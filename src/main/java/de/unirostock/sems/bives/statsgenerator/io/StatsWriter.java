/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.io;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;


/**
 * The Class StatsWriter.
 *
 * @author martin
 */
public abstract class StatsWriter
implements Closeable
{
	
	/** The writer. */
	protected BufferedWriter writer;
	
	/** The file name. */
	protected String fileName;
	
	/**
	 * Instantiates a new stats writer.
	 *
	 * @param fileName the file name
	 */
	public StatsWriter (String fileName)
	{
		this.fileName = fileName;
	}
	
	/**
	 * Write header.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract void writeHeader () throws IOException;
	
	/**
	 * Creates the.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void create () throws IOException
	{
		if (writer != null)
			throw new RuntimeException ("you cannot create an existing stats writer");
		writer = new BufferedWriter (new FileWriter (fileName));
	}
	
	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	public void close () throws IOException
	{
		writer.flush ();
		writer.close ();
		writer = null;
	}
	
}
