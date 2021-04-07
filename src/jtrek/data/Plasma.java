package jtrek.data;

import jtrek.Copyright;
import java.awt.*;

public class Plasma {
	public final static int FREE = 0;
	public final static int MOVE = 1;
	public final static int EXPLODE = 2;
	public final static int DET = 3;

	public int no;
	public int status;				// State information
	public Player owner;
	public int x;
	public int y;
	public int fuse;				// Life left in current state
	public int war;					// enemies
	public int team;				// launching team

	public Plasma(int number, Player owner) {
		this.owner = owner;
		no = (byte)number;
	}

	final public Color getColor() { 
		return owner.getColor();
	}
}
