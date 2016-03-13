/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.imageio.ImageIO;

import de.binfalse.bflog.LOGGER;


/**
 * @author martin
 *
 */
public class BivesAuswertungImager
{
	/*public static String WORKING = "/srv/modelcrawler/statswebsite/copy/workingdir";
	public static String EVALUATING = "/home/martin/unisonSyncPrivate/education/dev/statswebsite/eval";*/
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	/*public static void main (String[] args) throws IOException
	{
		int [] scalers = new int [] {30, 50, 100, 150, 200};
		int [] minOps = new int [] {30, 50, 100, 200};

		int scale = 50;
		int minOp = 10;
		
		createImageBivesHorizontally (true, new File (WORKING + "/diffstats-all-tobeused"), 
			new File(EVALUATING + "/bives-cellml-diffimg-log-scale-"+scale+"-ops-"+minOp+".png"), new LogScale (scale), minOp);
		
	}*/
	
	
	public static void runForNovak (File statsFile, File outputDir) throws IOException
	{
		int [] scalers = new int [] {30, 50, 100, 150, 200};
		int [] minOps = new int [] {30, 50, 100, 200};

		int scale = 50;
		int minOp = 10;
		
		createImageBivesHorizontally (true, statsFile, 
			new File(outputDir.getAbsolutePath () + "/NOVAK-bives-cellml-diffimg-log-scale-"+scale+"-ops-"+minOp+".png"), new LogScale (scale), minOp);
		
	}
	
	
	public static void run (File statsFile, File outputDir)
	{

		int [] scalers = new int [] {30, 50, 100, 150, 200};
		int [] minOps = new int [] {30, 50, 100, 200};

		for (int s : scalers)
			for (int m : minOps)
			{
				try
				{
					createImageBivesHorizontally (false, statsFile, 
						new File(outputDir.getAbsolutePath () + "/bives-cellml-diffimg-scale-"+s+"-ops-"+m+".png"), new Scale (s), m);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				try
				{
					createImageBivesHorizontally (false, statsFile, 
						new File(outputDir.getAbsolutePath () + "/bives-cellml-diffimg-log-scale-"+s+"-ops-"+m+".png"), new LogScale (s), m);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}		
	}
	
	static class Scale
	{
		double scaler;
		public Scale (double scaler)
		{
			this.scaler = scaler;
		}
		public int scale (double s)
		{
			return (int) Math.round (s / scaler);
		}
	}
	
	static class LogScale extends Scale
	{
		public LogScale (double scaler)
		{
			super (scaler);
		}
		
		public int scale (double s)
		{
			if (s < 1)
				return (int)  Math.round (s);
			return (int)  Math.round (Math.log (s) * scaler);
		}
	}
	private static void createImageBivesHorizontally (boolean findNovak, File in, File out, Scale scale, int minOps) throws IOException
	{
		
		BufferedReader br = new BufferedReader (new FileReader (in));
		
		int novakPixel = -10;
		
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
			if (findNovak && i[16].contains ("novak_tyson_1993_b.cellml") && i[17].contains ("2010-07-05"))
			{
				System.out.println (l);
				System.out.println (Arrays.toString (row));
				novakPixel = nums.size ();
			}
		}
		br.close ();
		
		if (findNovak && novakPixel < 1)
		{
			System.err.println ("no novak pixel :(");
			System.exit (2);
		}

		maxBives = scale.scale (maxBives);
		
		//System.out.println (nums);
		System.out.println (nums.size ());
		System.out.println (maxBives);
		
		BufferedImage img = new BufferedImage(nums.size () + 70, (int) (maxBives+200), 
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

		int leftSpace = 100;
		int startBottomLegend = (int) (maxBives+80);
		int widthBottomLegend = 40;
		
		g.fillRect (leftSpace, startBottomLegend, nums.size () + 10 - leftSpace, widthBottomLegend);
		
		
		
		//g.setColor (Color.WHITE);
		g.setFont (new Font("Arial", Font.BOLD, 16));
    for (int i : new int [] {1, 5, 10, 50, 100, 500, 1000, 5000, 10000, 50000})
    {
    	if (scale.scale(i) > maxBives)
    		break;
			int y = scale.scale (i);
    	int x = nums.size ()+15;
    	
    	if (i < 1000)
    	g.drawString (i + "", x, startBives - y);
    	if (i == 1000)
      	g.drawString ("1.000", x, startBives - y);
    	if (i == 5000)
      	g.drawString ("5.000", x, startBives - y);
    	if (i == 10000)
      	g.drawString ("10.000", x, startBives - y);
    }
		g.setColor (Color.WHITE);

    if (novakPixel > 0)
    {
    	System.out.println (novakPixel);
    	g.drawLine (novakPixel + 4, startBives, novakPixel + 4, startBives + 5);
    }
		for (int i = 0; i < nums.size (); i++)
		{
			double [] n = nums.elementAt (i);
			
//			double width = scale.scale (n[0]);
//			double inserts = width * n[26] / n[27];
//			double deletes = width * n[25] / n[27];
			//g.setColor (insert);
			//g.drawLine (startUnix, i, startUnix - (int) inserts, i);
			//g.setColor (delete);
			//g.drawLine (startUnix - (int) inserts, i, startUnix -(int) (inserts + deletes), i);
			
			double width = scale.scale (n[0]);
			double inserts = width * n[1] / n[0];
			double deletes = width * n[2] / n[0];
			double moves = width * n[3] / n[0];
			double updates = width * n[4] / n[0];

System.out.println (width);
			
			if (i + 1 == novakPixel)
			{
				System.out.println ("novak: " + Arrays.toString (n));
				System.out.println ("novak: " + width);
				System.out.println ("novak: " + deletes + " - " + inserts + " - " + updates + " - " + moves);
				
				int ins = (int) n[1];
				int del = (int) n[2];
				int mov = (int) n[3];
				int up = (int) n[4];
//				
				System.out.println ("novak: " + del + " - " + ins + " - " + up + " - " + mov);
				
				// draw in startBives+ 20 bis startBives+ 40
				
				int iW = (int) (n[1] * (nums.size () -leftSpace) / n[0]);
				int dW = (int) (n[2] * (nums.size () -leftSpace) / n[0]);
				int mW = (int) (n[3] * (nums.size () -leftSpace) / n[0]);
				int uW = (int) (n[4] * (nums.size () -leftSpace) / n[0]);
				
				g.setColor (update);
				g.fillRect (leftSpace + 5, startBottomLegend + 5, uW, widthBottomLegend - 10);
				g.setColor (move);
				g.fillRect (leftSpace + 5 + uW, startBottomLegend + 5, mW, widthBottomLegend - 10);
				g.setColor (insert);
				g.fillRect (leftSpace + 5 + uW + mW, startBottomLegend + 5, iW, widthBottomLegend - 10);
				g.setColor (delete);
				g.fillRect (leftSpace + 5 + uW + mW + iW, startBottomLegend + 5, dW, widthBottomLegend - 10);
				
				int s = del + ins + up + mov;//(int) (deletes + inserts + updates + moves);
				int w = uW + mW + iW + dW;

	    	// rechts unten:
				g.setColor (Color.BLACK);
      	g.drawString (s + "", w + leftSpace, startBottomLegend + widthBottomLegend+15);

		    for (int l : new int [] {1, 5, 10, 50, 100, 500, 1000, 5000, 10000, 50000})
		    { 
		    	
					int x = leftSpace + 3 + (int) (scale.scale (l) * w / scale.scale (s));
		    	int y = startBottomLegend + widthBottomLegend+15;
		    	
		    	if (l < 1000)
		    		g.drawString (l + "", x, y);
		    	if (l == 1000)
		      	g.drawString ("1.000", x, y);
		    	if (l == 5000)
		      	g.drawString ("5.000", x, y);
		    	if (l == 10000)
		      	g.drawString ("10.000", x, y);
		    }
			}
			
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
	
	
	
	public static double sum (int a, int b, double [] n)
	{
		int s = 0;
		for (int i = a; i <= b; i++)
			s+= n[i];
		return s;
	}
}
