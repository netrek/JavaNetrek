package jtrek.data;

import jtrek.Copyright;
import java.awt.*;

public class Player {
	public final static char[] PLAYER_LETTER = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

	public final static int FREE = 0;
	public final static int OUTFIT = 1;
	public final static int ALIVE = 2;
	public final static int EXPLODE = 3;
	public final static int DEAD = 4;

	public final static int SHIELD = 0x00000001;
	public final static int REPAIR = 0x00000002;
	public final static int BOMB = 0x00000004;
	public final static int ORBIT = 0x00000008;
	public final static int CLOAK = 0x00000010;
	public final static int WEP = 0x00000020;
	public final static int ENG = 0x00000040;
	public final static int ROBOT = 0x00000080;
	public final static int BEAMUP = 0x00000100;
	public final static int BEAMDOWN = 0x00000200;
	public final static int SELFDEST = 0x00000400;
	public final static int GREEN = 0x00000800;
	public final static int YELLOW = 0x00001000;
	public final static int RED = 0x00002000;
	public final static int PLOCK = 0x00004000;			// Locked on a player
	public final static int PLLOCK = 0x00008000;		// Locked on a planet
	public final static int COPILOT = 0x00010000;		// Allow copilots
	public final static int WAR = 0x00020000;			// computer reprogramming for war
	public final static int PRACTR = 0x00040000;		// practice type robot (no kills)
	public final static int DOCK = 0x00080000;			// true if docked to a starbase
	public final static int REFIT = 0x00100000;			// true if about to refit
	public final static int REFITTING = 0x00200000;		// true if currently refitting
	public final static int TRACT = 0x00400000;			// tractor beam activated
	public final static int PRESS = 0x00800000;			// pressor beam activated
	public final static int DOCKOK = 0x01000000;		// docking permission
	public final static int OBSERV = 0x8000000;			// observer
	public final static int CLOAK_PHASES = 8;
	
	public int no;
	public int status;									// Player status
	public int flags;									// Player flags
	public String name = "";
	public String login = "";
	public String monitor = "";							// Monitor being played on
	public String mapchars = "";						// Cache for map window image

	public Ship ship = Ship.SHIPS[Ship.AT]; 			// Personal ship statistics
	public Stats stats = new Stats();					// player statistics
	public Team team = Team.TEAMS[Team.IND];			// Team the player is on

	public int x = -1000;
	public int y = -1000;
	public int dir;										// Real direction
	public int speed;									// Real speed
	public int damage;									// Current damage
	public int shield;									// Current shield power
	public int cloakphase;								// Drawing stage of cloaking engage/disengage.
	public int ntorp;									// Number of torps flying
	public int nplasmatorp;								// Number of plasma torps active
	public int hostile;									// Who my torps will hurt
	public int swar;									// Who am I at sticky war with
	public float kills;									// Enemies killed
	public int planet;             						// Planet orbiting or locked onto
	public int playerl;									// Player locked onto
	public int armies;									// Number of armies carried
	public int fuel;									// Amount of fuel
	public int explode;									// Keeps track of final explosion
	public int etemp;									// Engine Temperature
	public int wtemp;									// Weapon Temperature
	public int whydead;									// Tells you why you died
	public int whodead;									// Tells you who killed you
	public int tractor;									// What player is in tractor lock

	public boolean me = false;
	public boolean ispuck = false;						// for Hockey

	public Player(int number) {
		no = (byte)number;
	}

	// getColor
	final public Color getColor() { 
		return me ? Color.white : team.color;
	}

	// getLetter
	final public char getLetter() {
		return PLAYER_LETTER[no];
	}

	// friendlyPlayer
	public boolean friendlyPlayer(Player other) { 
		return((team.bit & (other.swar | other.hostile)) == 0 && (other.team.bit & (swar | hostile)) == 0);
	}

	/** calculateWins */
	public final int calculateWins() {
		return ((ship.type == Ship.SB) ? stats.sbkills : (stats.kills + stats.tkills));
	}

	/** calculateLoses */
	public final int calculateLosses() {
		return ((ship.type == Ship.SB) ? stats.sblosses : (stats.losses + stats.tlosses));
	}

	/** calculateMaxKills */
	public final double calculateMaxKills() {
		return ((ship.type == Ship.SB) ? stats.sbmaxkills : stats.maxkills);
	}

	/** getPlayerString */
	public String getPlayerString() {
		return name + " (" + mapchars + ')';
	}
}
