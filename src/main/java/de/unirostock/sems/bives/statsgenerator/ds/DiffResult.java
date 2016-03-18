/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator.ds;



/**
 * @author Martin Scharm
 *
 */
public class DiffResult
{
	private int bivesInserts, bivesDeletes, bivesMoves, bivesUpdates;
	private int unixDeletes, unixInserts;
	private int xmlNodes, xmlAttributes, xmlTexts;
	private int triggeredInserts, triggeredDeletes, triggeredMoves, triggeredUpdates;
	
	
	
	/**
	 */
	public DiffResult ()
	{
		super ();
		this.bivesInserts = 0;
		this.bivesDeletes = 0;
		this.bivesMoves = 0;
		this.bivesUpdates = 0;
		this.unixDeletes = 0;
		this.unixInserts = 0;
		this.xmlNodes = 0;
		this.xmlAttributes = 0;
		this.xmlTexts = 0;
		this.triggeredInserts = 0;
		this.triggeredDeletes = 0;
		this.triggeredMoves = 0;
		this.triggeredUpdates = 0;
	}

	
	/**
	 * Increment the unix-deletes-counter.
	 *
	 * @return the the new value
	 */
	public int incrementUnixDeletes ()
	{
		return ++unixDeletes;
	}
	
	/**
	 * Increment the unix-inserts-counter.
	 *
	 * @return the the new value
	 */
	public int incrementUnixInserts ()
	{
		return ++unixInserts;
	}
	
	/**
	 * Increment the triggered-deletes-counter.
	 *
	 * @return the the new value
	 */
	public int incrementTriggeredDeletes ()
	{
		return ++triggeredDeletes;
	}
	
	/**
	 * Increment the triggered-updates-counter.
	 *
	 * @return the the new value
	 */
	public int incrementTriggeredUpdates ()
	{
		return ++triggeredUpdates;
	}
	
	/**
	 * Increment the triggered-moves-counter.
	 *
	 * @return the the new value
	 */
	public int incrementTriggeredMoves ()
	{
		return ++triggeredMoves;
	}
	
	/**
	 * Increment the triggered-inserts-counter.
	 *
	 * @return the the new value
	 */
	public int incrementTriggeredInserts ()
	{
		return ++triggeredInserts;
	}

	/**
	 * @return the bivesInserts
	 */
	public int getBivesInserts ()
	{
		return bivesInserts;
	}
	
	/**
	 * @param bivesInserts the bivesInserts to set
	 */
	public void setBivesInserts (int bivesInserts)
	{
		this.bivesInserts = bivesInserts;
	}
	
	/**
	 * @return the bivesDeletes
	 */
	public int getBivesDeletes ()
	{
		return bivesDeletes;
	}
	
	/**
	 * @param bivesDeletes the bivesDeletes to set
	 */
	public void setBivesDeletes (int bivesDeletes)
	{
		this.bivesDeletes = bivesDeletes;
	}
	
	/**
	 * @return the bivesMoves
	 */
	public int getBivesMoves ()
	{
		return bivesMoves;
	}
	
	/**
	 * @param bivesMoves the bivesMoves to set
	 */
	public void setBivesMoves (int bivesMoves)
	{
		this.bivesMoves = bivesMoves;
	}
	
	/**
	 * @return the bivesUpdates
	 */
	public int getBivesUpdates ()
	{
		return bivesUpdates;
	}
	
	/**
	 * @param bivesUpdates the bivesUpdates to set
	 */
	public void setBivesUpdates (int bivesUpdates)
	{
		this.bivesUpdates = bivesUpdates;
	}
	
	/**
	 * @return the unixDeletes
	 */
	public int getUnixDeletes ()
	{
		return unixDeletes;
	}
	
	/**
	 * @param unixDeletes the unixDeletes to set
	 */
	public void setUnixDeletes (int unixDeletes)
	{
		this.unixDeletes = unixDeletes;
	}
	
	/**
	 * @return the unixInserts
	 */
	public int getUnixInserts ()
	{
		return unixInserts;
	}
	
	/**
	 * @param unixInserts the unixInserts to set
	 */
	public void setUnixInserts (int unixInserts)
	{
		this.unixInserts = unixInserts;
	}
	
	/**
	 * @return the xmlNodes
	 */
	public int getXmlNodes ()
	{
		return xmlNodes;
	}
	
	/**
	 * @param xmlNodes the xmlNodes to set
	 */
	public void setXmlNodes (int xmlNodes)
	{
		this.xmlNodes = xmlNodes;
	}
	
	/**
	 * @return the xmlAttributes
	 */
	public int getXmlAttributes ()
	{
		return xmlAttributes;
	}
	
	/**
	 * @param xmlAttributes the xmlAttributes to set
	 */
	public void setXmlAttributes (int xmlAttributes)
	{
		this.xmlAttributes = xmlAttributes;
	}
	
	/**
	 * @return the xmlTexts
	 */
	public int getXmlTexts ()
	{
		return xmlTexts;
	}
	
	/**
	 * @param xmlTexts the xmlTexts to set
	 */
	public void setXmlTexts (int xmlTexts)
	{
		this.xmlTexts = xmlTexts;
	}
	
	/**
	 * @return the triggeredInserts
	 */
	public int getTriggeredInserts ()
	{
		return triggeredInserts;
	}
	
	/**
	 * @param triggeredInserts the triggeredInserts to set
	 */
	public void setTriggeredInserts (int triggeredInserts)
	{
		this.triggeredInserts = triggeredInserts;
	}
	
	/**
	 * @return the triggeredDeletes
	 */
	public int getTriggeredDeletes ()
	{
		return triggeredDeletes;
	}
	
	/**
	 * @param triggeredDeletes the triggeredDeletes to set
	 */
	public void setTriggeredDeletes (int triggeredDeletes)
	{
		this.triggeredDeletes = triggeredDeletes;
	}
	
	/**
	 * @return the triggeredMoves
	 */
	public int getTriggeredMoves ()
	{
		return triggeredMoves;
	}
	
	/**
	 * @param triggeredMoves the triggeredMoves to set
	 */
	public void setTriggeredMoves (int triggeredMoves)
	{
		this.triggeredMoves = triggeredMoves;
	}
	
	/**
	 * @return the triggeredUpdates
	 */
	public int getTriggeredUpdates ()
	{
		return triggeredUpdates;
	}
	
	/**
	 * @param triggeredUpdates the triggeredUpdates to set
	 */
	public void setTriggeredUpdates (int triggeredUpdates)
	{
		this.triggeredUpdates = triggeredUpdates;
	}
	
	
	
	/*public String toString ()
	{
		if (ins + del + mov + up != nodes + attr + texts)
			LOGGER.error ("inconsitent diff!? types");

		if ((0 < ins + del + mov + up) && ins + del + mov + up <= trIns + trDel + trMov + trUp)
			LOGGER.error ("inconsitent diff!? triggered: " + (ins + del + mov + up) + " -- " + (trIns + trDel + trMov + trUp));
		
		return (unixDelete + unixInsert) + "\t"
			+ unixInsert + "\t"
			+ unixDelete + "\t"
			+ (ins + del + mov + up) + "\t"
			+ ins + "\t"
			+ del + "\t"
			+ mov + "\t"
			+ up + "\t"
				+ trIns + "\t"
				+ trDel + "\t"
				+ trMov + "\t"
				+ trUp + "\t"
			+ nodes + "\t"
			+ attr + "\t"
			+ texts + "\t";
	}*/
}
