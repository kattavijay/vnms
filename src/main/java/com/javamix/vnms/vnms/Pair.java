 
/*
 * This class defines a pair of nodes forming a connection
 */
package com.javamix.vnms.vnms;
public class Pair {
	 
	public String from;
	public String to;
	public int type;
	
	public Pair(String a, String b, int aType) {
		from = a;
		to = b;
		type = aType;
	}

	// a connection of A connecting to B is the same as
	// B connecting to A
	public boolean equals(Pair b) {
		if (
		(from.toString().compareTo(b.from.toString()) == 0) 
		 &&
	    (to.toString().compareTo(b.to.toString()) == 0))
			return true;
		if (
		 (to.toString().compareTo(b.from.toString()) == 0)
		  &&
		 (from.toString().compareTo(b.to.toString()) == 0))
			return true;
		return false;
	}
	
	// should only be called if the pair already exist
	public boolean updateType(Pair p)
	{
		// do not degenerate from secure connection
		if (p.type != 0)
		{
			type = p.type;
		}	
		return true;	
	}
}
