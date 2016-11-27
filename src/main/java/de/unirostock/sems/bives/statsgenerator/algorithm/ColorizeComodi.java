/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.JDOMException;

import de.binfalse.bfutils.GeneralTools;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;

/**
 * @author martin
 *
 */
public class ColorizeComodi
{
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws XmlDocumentParseException 
	 */
	public static void main (String[] args) throws IOException, XmlDocumentParseException, JDOMException
	{
		// expects comodi figure in src/main/resources/comodi-fig.svg
		// expects comodi coverage in src/main/resources/data/comodi-terms
		// will write the colorized map to /tmp/colorized-comodi-figure.svg
		// TODO: implement parser for cmd arguments to allow for different paths?
		
		new ColorizeComodi ().run ();
	}

	final static int TYPE_CHANGE = 0;
	final static int TYPE_ENTITY = 1;
	final static int TYPE_TARGET = 2;

	double [] maxOverall;
	double [] maxSbml;
	double [] maxCellml;

	double [] minOverall;
	double [] minSbml;
	double [] minCellml;
	
	class ComodiCoverage
	{
		double overall, sbml, cellml;
		int type;
		

		public ComodiCoverage (int type, String overall, String sbml, String cellml)
		{
			this.overall = Math.log (Integer.parseInt (overall));
			this.sbml = Math.log (Integer.parseInt (sbml));
			this.cellml = Math.log (Integer.parseInt (cellml));

			this.type = type;
			
			
			if (this.overall > maxOverall[type])
				maxOverall[type] = this.overall;
			
			if (this.sbml > maxSbml[type])
				maxSbml[type] = this.sbml;
			
			if (this.cellml > maxCellml[type])
				maxCellml[type] = this.cellml;
			
			
			if (this.overall < minOverall[type])
				minOverall[type] = this.overall;
			
			if (this.sbml < minSbml[type])
				minSbml[type] = this.sbml;
			
			if (this.cellml < minCellml[type])
				minCellml[type] = this.cellml;
		}
		
	}
	
	public void run () throws IOException, XmlDocumentParseException, JDOMException
	{
		maxOverall = new double [3];
		maxSbml = new double [3];
		maxCellml = new double [3];
		
		minOverall = new double [] {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
		minSbml = new double [] {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
		minCellml = new double [] {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
		
		Map<String, ComodiCoverage> coverage = new HashMap<String, ComodiCoverage> ();
		
		// read the coverage as it was detected by the stats generator
		InputStream in = getClass().getResourceAsStream("/data/comodi-terms"); 
		BufferedReader reader = new BufferedReader(new InputStreamReader (in));
		while (reader.ready ())
		{
			String [] line = reader.readLine ().split ("\t");
			if (line.length != 4)
				throw new IOException ("do not understand line " + Arrays.toString (line));
			
			int p = line[0].indexOf ("#");
			if (p > 0)
				line[0] = line[0].substring (p + 1);
			
			// we're neglecting reasons here
			if (line[0].equals ("ChangedSpecification"))
				continue;
			
			coverage.put (line[0], new ComodiCoverage (getType (line[0]), line[1], line[2], line[3]));
			
		}
		reader.close ();
		
		
		
		// update the SVG figure
		TreeDocument svg = new TreeDocument (XmlTools.readDocument (getClass().getResourceAsStream("/comodi-fig.svg")), null);
//		for (DocumentNode dn : svg.getNodesByTag ("ellipse"))
//		{
//			String style = dn.getAttributeValue ("style");
//			style = style.replace (";fill-opacity:1;", ";fill-opacity:0;");
//			dn.setAttribute ("style", style);
//		}
		for (String term : coverage.keySet ())
		{
			
			DocumentNode dn = svg.getNodeById (term);
			if (dn != null)
			{
				String style = dn.getAttributeValue ("style");
				double val = (coverage.get (term).overall - minOverall[coverage.get (term).type] + 1) / (maxOverall[coverage.get (term).type] - minOverall[coverage.get (term).type] + 1);
				style = style.replace (";fill-opacity:0;", ";fill-opacity:" + (val) + ";");
				dn.setAttribute ("style", style);
			}
			else
				System.err.println ("did not find " + term);
		}
		
		File out = new File ("/tmp/colorized-comodi-figure.svg");
		if (out.exists ())
			throw new IOException (out + " exists. will not overwrite it..");
		GeneralTools.stringToFile (XmlTools.prettyPrintDocument (DocumentTools.getDoc (svg)), out);
		
	}
	
	
	private static final int getType (String term) throws IOException
	{
		if (term.equals ("PermutationOfEntities")) return TYPE_CHANGE;
		if (term.equals ("Deletion")) return TYPE_CHANGE;
		if (term.equals ("Update")) return TYPE_CHANGE;
		if (term.equals ("Move")) return TYPE_CHANGE;
		if (term.equals ("Insertion")) return TYPE_CHANGE;
		
		if (term.equals ("XmlNode")) return TYPE_ENTITY;
		if (term.equals ("ModelId")) return TYPE_ENTITY;
		if (term.equals ("EntityIdentifier")) return TYPE_ENTITY;
		if (term.equals ("XmlAttribute")) return TYPE_ENTITY;
		if (term.equals ("ModelName")) return TYPE_ENTITY;
		if (term.equals ("EntityName")) return TYPE_ENTITY;
		if (term.equals ("XmlText")) return TYPE_ENTITY;
		
		if (term.equals ("ModelAnnotation")) return TYPE_TARGET;
		if (term.equals ("CreationDate")) return TYPE_TARGET;
		if (term.equals ("Date")) return TYPE_TARGET;
		if (term.equals ("ModificationDate")) return TYPE_TARGET;
		if (term.equals ("TextualDescription")) return TYPE_TARGET;
		if (term.equals ("OntologyReference")) return TYPE_TARGET;
		if (term.equals ("Person")) return TYPE_TARGET;
		if (term.equals ("Contributor")) return TYPE_TARGET;
		if (term.equals ("Creator")) return TYPE_TARGET;
		if (term.equals ("Attribution")) return TYPE_TARGET;
		if (term.equals ("ModelDefinition")) return TYPE_TARGET;
		if (term.equals ("MathematicalModelDefinition")) return TYPE_TARGET;
		if (term.equals ("ComponentDefinition")) return TYPE_TARGET;
		if (term.equals ("EventDefinition")) return TYPE_TARGET;
		if (term.equals ("FunctionDefinition")) return TYPE_TARGET;
		if (term.equals ("KineticsDefinition")) return TYPE_TARGET;
		if (term.equals ("RuleDefinition")) return TYPE_TARGET;
		if (term.equals ("UnitDefinition")) return TYPE_TARGET;
		if (term.equals ("NetworkDefinition")) return TYPE_TARGET;
		if (term.equals ("HierarchyDefinition")) return TYPE_TARGET;
		if (term.equals ("VariableConnectionDefinition")) return TYPE_TARGET;
		if (term.equals ("PortDefinition")) return TYPE_TARGET;
		if (term.equals ("ReactionNetworkDefinition")) return TYPE_TARGET;
		if (term.equals ("ParticipantDefinition")) return TYPE_TARGET;
		if (term.equals ("ReversibilityDefinition")) return TYPE_TARGET;
		if (term.equals ("ReactionDefinition")) return TYPE_TARGET;
		if (term.equals ("ModelEncoding")) return TYPE_TARGET;
		if (term.equals ("IdentifierEncoding")) return TYPE_TARGET;
		if (term.equals ("MetaIdEncoding")) return TYPE_TARGET;
		if (term.equals ("ModelSetup")) return TYPE_TARGET;
		if (term.equals ("ParameterSetup")) return TYPE_TARGET;
		if (term.equals ("SpeciesSetup")) return TYPE_TARGET;
		if (term.equals ("VariableSetup")) return TYPE_TARGET;

		throw new IOException ("cannot determine type: " + term);
	}
	
}
