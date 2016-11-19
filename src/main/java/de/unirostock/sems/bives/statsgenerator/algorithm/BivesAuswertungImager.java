/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.algorithm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;


// TODO: Auto-generated Javadoc
/**
 * The Class BivesAuswertungImager.
 *
 * @author martin
 */
public class BivesAuswertungImager
{
	
	
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
		String STORAGE = "/srv/modelcrawler/storage";
		new File (STORAGE + "/stats/figs").mkdirs ();
		
		BivesAuswertungImager.run (new File (STORAGE + "/stats/diffstats"), new File (STORAGE
			+ "/stats/figs"));
	}
	
	
	/**
	 * Run.
	 *
	 * @param statsFile the stats file
	 * @param outputDir the output dir
	 */
	public static void run (File statsFile, File outputDir)
	{

		int [] scalers = new int [] {30, 50, 100, 150, 200};
		int [] minOps = new int [] {30, 50, 100, 200};

		
		List<Filter> filters = new ArrayList<Filter> ();
		filters.add (new Filter ());
		filters.add (new SbmlFilter ());
		filters.add (new CellMlFilter ());
		
		
		for (Filter f : filters)
			for (int s : scalers)
				for (int m : minOps)
				{
					try
					{
						createImageBivesHorizontally (statsFile, f, new File(outputDir.getAbsolutePath () + "/bives-" + f + "-diffimg-scale-"+s+"-ops-"+m+".png"), new Scale (s), m);
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					try
					{
						createImageBivesHorizontally (statsFile, f, new File(outputDir.getAbsolutePath () + "/bives-" + f + "-diffimg-log-scale-"+s+"-ops-"+m+".png"), new LogScale (s), m);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}		
	}
	
	static class Filter
	{
		public String toString ()
		{
			return "no-filter";
		}
		
		public boolean lineOk (String line, String [] i)
		{
			return true;
		}
	}
	
	static class SbmlFilter
	extends Filter
	{
		public String toString ()
		{
			return "sbml-only";
		}
		
		public boolean lineOk (String line, String [] i)
		{
			return line.contains ("\"SBML\"");
		}
	}
	
	static class CellMlFilter
	extends Filter
	{
		public String toString ()
		{
			return "cellml-only";
		}
		
		public boolean lineOk (String line, String [] i)
		{
			return line.contains ("\"CellML\"");
		}
	}
	
	
	/**
	 * The Class Scale.
	 */
	static class Scale
	{
		
		/** The scaler. */
		double scaler;
		
		/**
		 * Instantiates a new scale.
		 *
		 * @param scaler the scaler
		 */
		public Scale (double scaler)
		{
			this.scaler = scaler;
		}
		
		/**
		 * Scale.
		 *
		 * @param s the s
		 * @return the int
		 */
		public int scale (double s)
		{
			return (int) Math.round (s / scaler);
		}
	}
	
	/**
	 * The Class LogScale.
	 */
	static class LogScale extends Scale
	{
		
		/**
		 * Instantiates a new log scale.
		 *
		 * @param scaler the scaler
		 */
		public LogScale (double scaler)
		{
			super (scaler);
		}
		
		/* (non-Javadoc)
		 * @see de.unirostock.sems.bives.statsgenerator.algorithm.BivesAuswertungImager.Scale#scale(double)
		 */
		public int scale (double s)
		{
			if (s < 1)
				return (int)  Math.round (s);
			return (int)  Math.round (Math.log (s) * scaler);
		}
	}
	
	/**
	 * Creates the image bives horizontally.
	 *
	 * @param in the in
	 * @param out the out
	 * @param scale the scale
	 * @param minOps the min ops
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void createImageBivesHorizontally (File in, Filter filter, File out, Scale scale, int minOps) throws IOException
	{
		BufferedReader br = new BufferedReader (new FileReader (in));
		Vector<double[]> nums = new Vector<double[]> ();
		Vector<String> lines = new Vector<String> ();
		double maxBives = 0;
		int rownum = 0;
		while (br.ready ())
		{
			String l = br.readLine ();
			// skip header
			if (rownum++ == 0)
				continue;
			// only cellml for now
			/*if (!l.contains ("http"))
				continue;*/
			lines.add (l);
		}
    Collections.shuffle(lines);
		for (String l : lines)
		{
			String[] i = l.split ("\t");
			if (!filter.lineOk (l, i))
				continue;
			
			double [] row = new double [5];
			for (int r = 0; r < 5; r++)
			{
				row[r] = Integer.parseInt (i[r + 3]);
			}
			
			if (row[0] > minOps)
			{
				//int mb = sum (2, 25, row);
				if (row[0] > maxBives)
					maxBives = row[0];
				nums.add (row);
			}
		}
		br.close ();

		maxBives = scale.scale (maxBives);
		
		//System.out.println (nums);
		System.out.println (nums.size ());
		System.out.println (maxBives);
		
		BufferedImage img = new BufferedImage(nums.size () + 90, (int) (maxBives+20), 
      BufferedImage.TYPE_INT_RGB);

		Color insert = Color.GREEN;
		Color update = Color.YELLOW;
		Color delete = Color.RED;
		Color move = Color.BLUE;

		int startBives = (int) (5 + maxBives);
		
		Graphics2D g = img.createGraphics ();
		g.setColor (Color.BLACK);
		g.fillRect (0, 0, nums.size () + 10, (int) (maxBives+10));

    RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); 
    g.setRenderingHints(rh);

		g.setColor (Color.WHITE);
		g.fillRect (nums.size () + 10, 0, nums.size () + 100, (int) (maxBives+10));
		g.fillRect (0, (int) (maxBives+10), nums.size () + 100, (int) (maxBives+100));
		g.setColor (Color.BLACK);
		
		
		g.setFont (new Font("Arial", Font.BOLD, 16));
    for (int i : new int [] {1, 5, 10, 50, 100, 500, 1000, 5000, 10000, 50000})
    {
    	if (scale.scale(i) > maxBives)
    		break;
			int y = scale.scale (i);
    	int x = nums.size ()+15;
    	
    	if (i < 1000 && scale instanceof LogScale)
    		g.drawString (i + "", x, startBives - y);
    	if (i == 1000)
      	g.drawString ("1.000", x, startBives - y);
    	if (i == 5000)
      	g.drawString ("5.000", x, startBives - y);
    	if (i == 10000)
      	g.drawString ("10.000", x, startBives - y);
    	if (i == 50000)
      	g.drawString ("50.000", x, startBives - y);
    }
		g.setColor (Color.WHITE);

		for (int i = 0; i < nums.size (); i++)
		{
			double [] n = nums.elementAt (i);
			
			double width = scale.scale (n[0]);
			double inserts = width * n[1] / n[0];
			double deletes = width * n[2] / n[0];
			double moves = width * n[3] / n[0];
			double updates = width * n[4] / n[0];

			
			int cur = startBives;
			int next = startBives;
			int x = i + 5;
			
			g.setColor (update);
			next = next - (int) updates;
			g.drawLine (x, cur, x, next);
			cur = next;
			
			g.setColor (move);
			next = next - (int) moves;
			g.drawLine (x, cur, x, next);
			cur = next;
			
			g.setColor (insert);
			next = next - (int) inserts;
			g.drawLine (x, cur, x, next);
			cur = next;
			
			g.setColor (delete);
			next = next - (int) deletes;
			g.drawLine (x, cur, x, next);
			cur = next;
			
			
		}
		g.dispose();
		
		ImageIO.write(img, "PNG", out);
	}
	
	
	
	/**
	 * Sum.
	 *
	 * @param a the a
	 * @param b the b
	 * @param n the n
	 * @return the double
	 */
	public static double sum (int a, int b, double [] n)
	{
		int s = 0;
		for (int i = a; i <= b; i++)
			s+= n[i];
		return s;
	}
}
