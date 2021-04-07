package jtrek.data;

import jtrek.Copyright;
import java.awt.*;

public class Torp {
	public final static int FREE = 0;
	public final static int MOVE = 1;
	public final static int EXPLODE = 2;
	public final static int DET = 3;
	public final static int OFF = 4;
	public final static int STRAIGHT = 5;		// Non-wobbling torp

	public int no;
	public int status;							// State information
	public Player owner;
	public int x;
	public int y;
	public int dir;								// direction
	public int fuse;							// Life left in current state
	public int war;								// enemies

	public Torp(int number, Player owner) {
		this.owner = owner;
		no = (short)number;
	}

	final public Color getColor() { 
		return owner.getColor();
	}

}
