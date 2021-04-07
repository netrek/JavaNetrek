package jtrek.data;

import jtrek.Copyright;

public class Phaser {
	public final static int MAX_DISTANCE = 6000; 	// At this range a player can do damage with phasers
	public final static int FREE = 0x0;
	public final static int HIT  = 0x1;				// When it hits a person 
	public final static int MISS = 0x2;
	public final static int HIT2 = 0x4;				// When it hits a photon 

	public int status;								// What it's up to 
	public int dir;									// direction 
	public Player target;							// Who's being hit (for drawing) 
	public int x; 									// For when it hits a torp 
	public int y;					
	public int fuse;								// Life left for drawing 
	public int maxfuse;								// max fuse, normalized for updates per second 

}
