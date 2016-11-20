/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.ds;


/**
 * The Class ComodiTermCounter.
 *
 * @author martin
 */
public class ComodiTermCounter
{
	
	/** The term. */
	public String term;
	
	/** The n biom. */
	public int nSBML;
	
	/** The n cell ml. */
	public int nCellMl;
	
	/**
	 * The Constructor.
	 *
	 * @param term the term
	 */
	public ComodiTermCounter (String term)
	{
		this.term = term;
		this.nSBML = 0;
		this.nCellMl = 0;
	}
}
