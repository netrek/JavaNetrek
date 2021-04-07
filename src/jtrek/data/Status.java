package jtrek.data;

import jtrek.Copyright;

public class Status {
	// Tournament mode?
	public boolean tourn = false;

	// Was I Promoted?
	public boolean promoted = false;

	// These stats only updated during tournament mode
	public int armsbomb; 
	public int planets;
	public int kills; 
	public int losses;
	public int time;

	// Use long for this, so it never wraps
	public long timeprod;

	// flag that indicates we are playing as an observer
	public boolean observer = false;
}
