package jtrek.data;

import jtrek.Copyright;
import java.awt.*;

public class Planet {
	// the lower bits represent the original owning team
	public final static int REPAIR = 0x010;
	public final static int FUEL = 0x020;
	public final static int AGRI = 0x040;
	public final static int REDRAW = 0x080;		// should the planet be redrawn on the galactic?
	public final static int HOME = 0x100;		// home planet for a given team
	public final static int COUP = 0x200;		// Coup has occured
	public final static int CHEAP = 0x400;		// Planet was taken from undefended team

	public int no;
	public int flags;							// State information
	public Team owner;
	public int x = -1000;
	public int y = -1000;
	public String name = "";
	public int armies;
	public int info;							// Teams which have info on planets

	public int orbit = 0;

	public Planet(int number) {
		no = (byte)number;
		owner = Team.TEAMS[Team.IND];
	}

	public Color getColor() {
		return owner.getColor();
	}

	/** getAbbrev */
	public String getAbbrev() {
		if(name.length() > 3) {
			return name.substring(0, 3).toUpperCase();
		}
		return name;
	}

}
