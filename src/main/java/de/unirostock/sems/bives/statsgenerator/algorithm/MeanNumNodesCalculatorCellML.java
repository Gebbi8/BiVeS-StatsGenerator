/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.binfalse.bflog.LOGGER;


// TODO: Auto-generated Javadoc
/**
 * The Class MeanNumNodesCalculatorCellML.
 *
 * @author Martin Scharm
 */
public class MeanNumNodesCalculatorCellML
{


	/**
	 * The Class Line.
	 */
	public static class Line
	{
		
		/** The revision. */
		int nodes, components, variables, units, revision;
		
		/** The type. */
		String date, file, type;
		
		/**
		 * Instantiates a new line.
		 *
		 * @param nodes the nodes
		 * @param components the components
		 * @param variables the variables
		 * @param units the units
		 * @param date the date
		 * @param file the file
		 * @param revision the revision
		 * @param type the type
		 */
		public Line (int nodes, int components, int variables, int units, String date, String file, int revision, String type)
		{
			super ();
			this.nodes = nodes;
			this.components = components;
			this.variables = variables;
			this.units = units;
			this.revision = revision;
			this.date = date;
			this.file = file;
			this.type = type;
		}
	}
	
	/**
	 * The Class File.
	 */
	public static class File
	{
		
		/** The name. */
		String name;
		
		/** The lines. */
		List<Line> lines;
		
		/**
		 * Instantiates a new file.
		 *
		 * @param name the name
		 */
		public File (String name)
		{
			this.name = name;
			this.lines = new ArrayList<Line> ();
		}
		
		/**
		 * Adds the line.
		 *
		 * @param l the l
		 */
		public void addLine (Line l)
		{
			lines.add (l);
		}

		/**
		 * Gets the lates before.
		 *
		 * @param date the date
		 * @return the lates before
		 */
		public Line getLatesBefore (String date)
		{
			Line line = null;
			
			for (Line l : lines)
				if (l.date.compareTo (date) <= 0)
					if (line == null || line.revision < l.revision)
							line = l;
			
			return line;
		}
	}
	
	
	/**
	 * Run.
	 *
	 * @param in the in
	 * @param out the out
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void run (String in, String out) throws IOException
	{
		Map<String, File> files = new HashMap<String, File> ();
		Set<String> dates = new TreeSet<String> ();
		
		BufferedReader br = new BufferedReader (new FileReader (in));
		boolean header = true;
		while (br.ready ())
		{
			String [] line = br.readLine ().split ("\\s+");
			if (header)
			{
				header = false;
				continue;
			}
			
			if (line.length < 16)
			{
				br.close ();
				throw new IOException ("don't understand line: " + line);
			}
//			if (line.length > 9)
//				for (int i = 0; i < line.length - 9; i++)
//					line[15] += line[7 + i];
			if (files.get (line[15]) == null)
				files.put (line[15], new File (line[15]));

//#1 nodes
//#2 species
//#3 reactions
//#4 compartments
//#5 functions
//#6 parameters
//#7 rules
//#8 events
//#9 units
//#0 variables
//#1 components
//#2 curated
//#3 modeltype
//#4 date
//#5 version
//#6 model
			
			files.get (line[15]).addLine (new Line (
				Integer.parseInt (line[0]),
				Integer.parseInt (line[10]),
				Integer.parseInt (line[9]),
				Integer.parseInt (line[8]),
				line[13],
				line[15],
				Integer.parseInt (line[14]),
				line[12]
				));
			dates.add (line[13]);
		}
		br.close ();
		
		BufferedWriter bw = new BufferedWriter (new FileWriter (out));
		for (String date : dates)
		{
			// search for files bevor this release
			int numComponents = 0;
			int numImports = 0;
			int numVariables = 0;
			int numUnits = 0;
			int numNodes = 0;
			int numFiles = 0;
			for (File f : files.values ())
			{
				Line latest = f.getLatesBefore (date);
				if (latest != null)
				{
					numNodes += latest.nodes;
					numUnits += latest.units;
					numVariables += latest.variables;
					numComponents += latest.components;
					numFiles++;
				}
			}
			
			bw.write (date + "\t" + numNodes + "\t" + numFiles + "\t" + numUnits + "\t" + numVariables + "\t" + numImports + "\t" + numComponents + LOGGER.NEWLINE);
		}
		bw.close ();
	}
	
}
