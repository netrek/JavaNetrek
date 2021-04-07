package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.data.*;

public class InfoPanel extends XFocusPanel {
	//-------------------
	//-- class members --
	//-------------------

	final static int LINE_DELTA = 50;
	final static char[] CLEARLINE = "\033\030CLEAR_MOTD".toCharArray();

	final static byte KQUIT			= 0x01;	// Player quit 
	final static byte KTORP			= 0x02;	// killed by torp 
	final static byte KPHASER		= 0x03;	// killed by phaser 
	final static byte KPLANET		= 0x04;	// killed by planet 
	final static byte KSHIP			= 0x05;	// killed by other ship 
	final static byte KDAEMON		= 0x06;	// killed by dying daemon 
	final static byte KWINNER		= 0x07;	// killed by a winner 
	final static byte KGHOST		= 0x08;	// killed because a ghost 
	final static byte KGENOCIDE		= 0x09;	// killed by genocide 
	final static byte KPROVIDENCE	= 0x0a;	// killed by a hacker 
	final static byte KPLASMA		= 0x0b;	// killed by a plasma torpedo 
	final static byte TOURNEND		= 0x0c;	// tournament game ended 
	final static byte KOVER			= 0x0d;	// game over  
	final static byte TOURNSTART	= 0x0e;	// tournament game starting 
	final static byte KBINARY		= 0x0f;	// bad binary 

	//----------------------
	//-- instance members --
	//----------------------

	String[] lines = new String[LINE_DELTA];
	int extent = 0;
	int position = 0;
	int visible;
	boolean show_death_info = false;
	boolean query_reset = false;
	
	NetrekFrame view;
	Universe data; 
	Communications comm;

	InfoPanel(String name) {
		super(name);
		data = null;
	}

	InfoPanel(String name, NetrekFrame view, Universe data, Communications comm) {
		super(name); 
		this.view = view;
		this.data = data;
		this.comm = comm;
	}

	/** setup */
	void setup() {
		super.setup();
		// save 4 lines at the bottom for death messages
		visible = view_size.height / getFontMetrics(fixed_font).getHeight();
		if(data != null) {
			visible -= 4;
		}
	}

	/** update */
	public void update(Graphics g) {
		g.translate(BORDER, BORDER);
		g.clearRect(0, 0, view_size.width, view_size.height);
		g.setFont(fixed_font);
		g.setColor(Color.white);
		int line_height = g.getFontMetrics().getHeight();
		int y = 3;
		int bottom = (position + visible < extent) ? position + visible : extent;
		for(int l = position; l < bottom; ++l) {
			g.drawString(lines[l], 12, y += line_height);
		}
		if(show_death_info) {
			drawDeathInfo(g);
		}
	}

	/** showDeathInfo */
	public void showDeathInfo(boolean value) {
		show_death_info = value;
	}

	/** drawDeathInfo */
	void drawDeathInfo(Graphics g) {
		g.setColor(Color.orange);
		Player me = data.me;
		if(me == null) {
			return;
		}
		Player other;
		int line_height = g.getFontMetrics().getHeight();
		int y = view_size.height - (int)(line_height * 3.5);
		switch(me.whydead) {
		case KQUIT:
			g.drawString("You have self-destructed.", 12, y);
			break;
		case KTORP:
			other = data.players[me.whodead];
			g.drawString("You were killed by a photon torpedo from " + other.name + " (" + other.team.letter + other.getLetter() + ").", 12, y);
			break;
		case KPLASMA:
			other = data.players[me.whodead];
			g.drawString("You were killed by a plasma torpedo from " + other.name + " (" + other.team.letter + other.getLetter() + ").", 12, y);
			break;
		case KPHASER:
			other = data.players[me.whodead];
			g.drawString("You were killed by a phaser shot from " + other.name + " (" + other.team.letter + other.getLetter() + ").", 12, y);
			break;
		case KPLANET:
			Planet planet = data.planets[me.whodead];
			g.drawString("You were killed by planetary fire from " + planet.name + " (" + planet.owner.abbrev + ").", 12, y);	
			break;
		case KSHIP:
			other = data.players[me.whodead];
			g.drawString("You were killed by an exploding ship formerly owned by " + other.name + " (" + other.team.letter + other.getLetter() + ").", 12, y);
			break;
		case KDAEMON:
			g.drawString("You were killed by a dying daemon.", 12, y);
			break;
		case KWINNER:
			other = data.players[me.whodead];
			g.drawString("Galaxy has been conquered by " + other.name + " (" + other.team.letter + other.getLetter() + ") " + other.team.abbrev + '.', 12, y);
			break;
		case KGHOST:
			g.drawString("You were killed by a confused daemon.", 12, y);
			break;
		case KGENOCIDE:
			other = data.players[me.whodead];
			g.drawString("Your team was genocided by " + other.name + " (" + other.team.letter + other.getLetter() + ") " + other.team.abbrev + '.', 12, y);
			break;
		case KPROVIDENCE:
			g.drawString("You were removed from existence by divine mercy.", 12, y);
			break;
		case KOVER:
			g.drawString("The game is over!", 12, y);
			break;
		case TOURNSTART:
			g.drawString("The a tournament game has begun!", 12, y);			
			break;
		case TOURNEND:
			g.drawString("The a tournament game has ended.", 12, y);
			break;
		case KBINARY:
			g.drawString("Your netrek executable didn't verify correctly.", 12, y);
			y += line_height;	
			g.setColor(Color.pink);
			g.drawString("(might be an old copy -- check the FAQ on rec.games.netrek)", 12, y);
			break;
		default:
			g.drawString("You were killed by something unknown to this game?", 12, y);
			break;
		}
		y += line_height;
		if(data.status.promoted) {
			data.status.promoted = false;
			g.setColor(Color.yellow);
			g.drawString("Congratulations!  You have been promoted to " + me.stats.rank.name, 12, y + 5);
		}
	}

	/** addLine */
	public void addLine(String line) {
		if(extent >= lines.length) {
			String[] new_lines = new String[lines.length + LINE_DELTA];
			System.arraycopy(lines, 0, new_lines, 0, lines.length);
			lines = new_lines;
		}
		lines[extent++] = line;
		if(extent - visible <= position) {
			repaint();
		}
	}

	/** handleKey */
	public boolean handleKey(KeyEvent e) {
		switch(e.getKeyChar()) {
		case 'f':
			// Scroll contents forward
			if(position + visible < extent) {
				position += visible;
				repaint();
			}
			break;
		case 'b':
			// Scroll contents backward
			if(position > 0) {
				position -= visible;
				repaint();
			}
			break;
		case 'R':
			query_reset = true;
			view.warning.setWarning("Please confirm reset request. (y/n)");
			break;
		case 'y':
			if (query_reset) {
				query_reset = false;
				comm.sendResetStatsReq((byte)'Y');
				view.warning.setWarning("OK, your reset request has been sent to the server.");
			}
			break;
		case 'n':
			if (query_reset) {
				query_reset = false;
				view.warning.setWarning("Yeah, WHATever.");
			}
			break;
		default :
			return false;
		}
		return true;
	}
}