/**
 * 
 */
package de.unirostock.sems.bives.statsgenerator;

import de.binfalse.bflog.LOGGER;


/**
 * @author Martin Scharm
 *
 */
public class DiffResult
{
	public int ins, del, mov, up, unixDelete, unixInsert, nodes, attr, texts;
	public int trIns, trDel, trMov, trUp;
	
	public String toString ()
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
	}
}
