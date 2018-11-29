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
import java.util.Arrays;
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
public class MeanNumNodesCalculator
{

	public static final int REPO_BIOMODELS = 1;
	public static final int REPO_PMR = 2;
	public static final int REPO_LD = 3;
	
	
	/**
	 * @author martin
	 * 
	 */
	public static class SumLine
	{
		
		int	nodes, components, variables, units;
		int	species, reactions, compartments, functions, parameters, rules, events;
		int	nFiles;
		
		
		/**
		 * Instantiates a new sum line.
		 * 
		 * @param line
		 *          the line
		 */
		public void add (Line line)
		{
			nFiles++;
			this.nodes += line.nodes;
			this.components += line.components;
			this.variables += line.variables;
			this.units += line.units;
			this.species += line.species;
			this.reactions += line.reactions;
			this.compartments += line.compartments;
			this.functions += line.functions;
			this.parameters += line.parameters;
			this.rules += line.rules;
			this.events += line.events;
		}
		
		
		public String toString ()
		{
			return nFiles + "\t" + nodes + "\t" + components + "\t" + variables
				+ "\t" + units + "\t" + species + "\t" + reactions + "\t"
				+ compartments + "\t" + functions + "\t" + parameters + "\t" + rules
				+ "\t" + events;
		}
		
		
		/**
		 * Equals.
		 *
		 * @param line the line
		 * @return true, if successful
		 */
		public boolean equals (SumLine line)
		{
			return this.nodes == line.nodes && this.components == line.components
				&& this.variables == line.variables && this.units == line.units
				&& this.species == line.species && this.reactions == line.reactions
				&& this.compartments == line.compartments
				&& this.functions == line.functions
				&& this.parameters == line.parameters && this.rules == line.rules
				&& this.events == line.events;
		}
	}
	
	/**
	 * The Class Line.
	 */
	public static class Line
	{
		
		/** The revision. */
		int	nodes, components, variables, units;
		int	species, reactions, compartments, functions, parameters, rules, events;
		
		/** The type. */
		String	date, file, type, url;
		
		int repository;
		
		
		/**
		 * Instantiates a new line.
		 *
		 * @param nodes the nodes
		 * @param species the species
		 * @param reactions the reactions
		 * @param compartments the compartments
		 * @param functions the functions
		 * @param parameters the parameters
		 * @param rules the rules
		 * @param events the events
		 * @param units the units
		 * @param variables the variables
		 * @param components the components
		 * @param type the type
		 * @param date the date
		 * @param url the url
		 */
		public Line (int nodes, int species, int reactions, int compartments,
			int functions, int parameters, int rules, int events, int units,
			int variables, int components, String type, String date, String url)
		{
			super ();
			this.nodes = nodes;
			this.components = components;
			this.variables = variables;
			this.units = units;
			this.date = date;
			this.type = type;
			this.species = species;
			this.reactions = reactions;
			this.compartments = compartments;
			this.functions = functions;
			this.parameters = parameters;
			this.rules = rules;
			this.events = events;
			this.url = url;
			if (url.contains ("d3d3LmViaS5hYy51ay9iaW9tb2RlbHMtbWFpbi8"))
				this.repository = REPO_BIOMODELS;
			else if (url.contains ("bW9kZWxzLmNlbGxtbC5vcmcv"))
				this.repository = REPO_PMR;
			else if (url.contains("L3Zhci93d3cvaHRtbC9HaXRSZXBvcy9CZW5jaG1hcmstTW9kZWxzL0JlbmNobWFyay1Nb2RlbHMv"))
				this.repository = REPO_LD;
			else	{	
				System.out.println(url);
				LOGGER.error ("don't know which repository: " + url);
				throw new RuntimeException ("don't know which repository: " + url);
			}
		}
	}
	
	/**
	 * The Class File.
	 */
	public static class File
	{
		
		/** The name. */
		String			name;
		
		/** The lines. */
		List<Line>	lines;
		
		
		/**
		 * Instantiates a new file.
		 * 
		 * @param name
		 *          the name
		 */
		public File (String name)
		{
			this.name = name;
			this.lines = new ArrayList<Line> ();
		}
		
		
		/**
		 * Adds the line.
		 * 
		 * @param l
		 *          the l
		 */
		public void addLine (Line l)
		{
			lines.add (l);
		}
		
		
		/**
		 * Gets the lates before.
		 * 
		 * @param date
		 *          the date
		 * @return the lates before
		 */
		public Line getLatesBefore (String date)
		{
			Line line = null;
			
			for (Line l : lines)
				if (l.date.compareTo (date) <= 0)
					if (line == null || l.date.compareTo (line.date) > 0)
						line = l;
			
			return line;
		}
	}
	
	
	/**
	 * Run.
	 * 
	 * @param in
	 *          the in
	 * @param out
	 *          the out
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public static void run (String in, String out) throws IOException
	{
		Map<String, File> files = new HashMap<String, File> ();
		Set<String> dates = new TreeSet<String> ();
		
		BufferedReader br = new BufferedReader (new FileReader (in));
		boolean header = true;
		while (br.ready ())
		{
			String[] line = br.readLine ().split ("\t");
			if (header)
			{
				header = false;
				continue;
			}
			
			if (line.length != 18)
			{
				br.close ();
				throw new IOException ("don't understand line: "
					+ Arrays.toString (line));
			}
			
			// #1 nodes
			// #2 species
			// #3 reactions
			// #4 compartments
			// #5 functions
			// #6 parameters
			// #7 rules
			// #8 events
			// #9 units
			// #0 variables
			// #1 components
			// #2 curated
			// #3 modeltype
			// #4 date
			// #5 version
			// #6 model
			// #7 modelname
			// #8 url
			
			if (files.get (line[15]) == null)
				files.put (line[15], new File (line[15]));
			
			files.get (line[15]).addLine (
				new Line (Integer.parseInt (line[0]), Integer.parseInt (line[1]),
					Integer.parseInt (line[2]), Integer.parseInt (line[3]), Integer
						.parseInt (line[4]), Integer.parseInt (line[5]), Integer
						.parseInt (line[6]), Integer.parseInt (line[7]), Integer
						.parseInt (line[8]), Integer.parseInt (line[9]), Integer
						.parseInt (line[10]), line[12], line[13], line[17]));
			dates.add (line[13]);
		}
		br.close ();
		
		BufferedWriter bw = new BufferedWriter (new FileWriter (out));
		bw.write ("date\ttype\tnFiles\tnodes\tcomponents\tvariables\tunits\tspecies\treactions\tcompartments\tfunctions\tparameters\trules\tevents"
			+ LOGGER.NEWLINE);
		
		SumLine prevSbml = null;
		
		for (String date : dates)
		{
			SumLine biomodels = new SumLine ();
			SumLine pmr = new SumLine ();
			SumLine ld = new SumLine ();
			SumLine all = new SumLine ();
			for (File f : files.values ())
			{
				Line latest = f.getLatesBefore (date);
				if (latest != null)
				{
					all.add (latest);
					
					if (latest.repository == REPO_BIOMODELS)
					{
						// d3d3LmViaS5hYy51ay9iaW9tb2RlbHMtbWFpbi8 = base64 (www.ebi.ac.uk/biomodels-main/)
						biomodels.add (latest);
					}
					else if (latest.repository == REPO_PMR)
					{
						pmr.add (latest);
					} else {
						ld.add(latest);
					}
					/*
					 * numNodes += latest.nodes;
					 * numUnits += latest.units;
					 * numVariables += latest.variables;
					 * numComponents += latest.components;
					 * numFiles++;
					 */
				}
			}
			
			bw.write (date + "\tALL\t" + all + LOGGER.NEWLINE);
			bw.write (date + "\tPMR2\t" + pmr + LOGGER.NEWLINE);
			if (prevSbml == null || !prevSbml.equals (biomodels))
				bw.write (date + "\tBIOMODELS\t" + biomodels + LOGGER.NEWLINE);
			prevSbml = biomodels;
		}
		bw.close ();
	}
	
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *          the arguments
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public static void main (String[] args) throws IOException
	{
		String STORAGE = "/tmp/stats-storage";
		
		MeanNumNodesCalculator.run (STORAGE + "/stats/filestats", STORAGE
			+ "/stats/repo-evolution");
	}
	
}
