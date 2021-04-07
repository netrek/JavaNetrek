package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.util.BufferUtils;

public class TextDashboard extends Dashboard {

	int lastdamage = -1;
	int lastkills = -1;
	int lastship = -1;

	boolean clock_on = true;

	char[] buffer = new char[80];
	
	/**
	* constructs a new Text Dashboard
	*/
	public TextDashboard() {
		super();
	}

	/** setup */
	void setup() {
		super.setup();
		// draw the header
		Graphics og = offscreen.getGraphics();
		og.setFont(fixed_font);
		FontMetrics metrics = og.getFontMetrics();
		String str = "Flags        Warp Dam Shd Torps  Kills Armies   Fuel  Wtemp Etemp";
		if (clock_on) {
			str += "  Time";
		}
		og.setColor(Color.black);
		og.fillRect(0, metrics.getDescent(), view_size.width, metrics.getHeight());
		og.setColor(Color.white);
		og.drawString(str, 26, metrics.getHeight());
		repaint();
	}

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(fixed_font);
		FontMetrics metrics = og.getFontMetrics();

		//
		// draw the status
		//

		// fill the buffer with spaces
		BufferUtils.fillWithSpaces(buffer);
 
		Player me = data.me;
		int flags = me.flags;

		if((flags & Player.SHIELD) != 0) {
			buffer[0] = 'S';
		}
		if((flags & Player.YELLOW) != 0) {
			buffer[1] = 'Y';
		}
		else if ((flags & Player.RED) != 0) {
			buffer[1] = 'R';
		}
		else {
			buffer[1] = 'G';
		}
		if((flags & (Player.PLLOCK | Player.PLOCK)) != 0) {
			buffer[2] = 'L';
		}
		if((flags & Player.REPAIR) != 0) {
			buffer[3] = 'R';	
		}
		if((flags & Player.BOMB) != 0) {
			buffer[4] = 'B';	
		}
		if((flags & Player.ORBIT) != 0) {
			buffer[5] = 'O';	
		}

		if ((me.ship.type != Ship.SB && (flags & Player.DOCK) != 0) 
			|| (me.ship.type == Ship.SB && (flags & Player.DOCKOK) != 0)) {
			buffer[6] = 'D';
		}

		if((flags & me.CLOAK) != 0) {
			buffer[7] = 'C';	
		}
		if((flags & me.WEP) != 0) {
			buffer[8] = 'W';	
		}
		if((flags & me.ENG) != 0) {
			buffer[9] = 'E';	
		}

		if((flags & me.TRACT) != 0) {
			buffer[10] = 'T';
		}
		else if ((flags & me.PRESS) != 0) {
			buffer[10] = 'P';
		}

		if((flags & me.BEAMUP) != 0) {
			buffer[11] = 'u';
		}
		else if ((flags & me.BEAMDOWN) != 0) {
			buffer[11] = 'd';
		}

		if(data.status.tourn) {
			buffer[12] = 't';
		}

		// speed
		BufferUtils.formatInteger(me.speed, buffer, 14, 2);

		// damage
		BufferUtils.formatInteger(me.damage, buffer, 18, 3);
		
		// shields
		BufferUtils.formatInteger(me.shield, buffer, 22, 3);

		// number of torps
		BufferUtils.formatInteger(me.ntorp, buffer, 27, 1);

		// kills
		BufferUtils.formatFloat(me.kills, buffer, 32, 2, 2);

		// armies
		BufferUtils.formatInteger(me.armies, buffer, 41, 2);

		// fuel
		BufferUtils.formatInteger(me.fuel, buffer, 47, 5);

		// weapon temp
		BufferUtils.formatInteger(me.wtemp / 10, buffer, 55, 3);

		// engine temp
		BufferUtils.formatInteger(me.etemp / 10, buffer, 61, 3);

		og.setColor(Color.black);
		og.fillRect(0, metrics.getHeight() + metrics.getDescent(), view_size.width, metrics.getHeight());
		og.setColor(Color.white);
		og.drawChars(buffer, 0, buffer.length, 26, metrics.getHeight() * 2);

		//
		// draw maximums
		//

		int mykills = (int) (10.0f * data.me.kills);

		// don't really need a update if nothing's changed!
		if (lastkills != mykills || lastship != data.me.ship.type || lastdamage != data.me.damage) {
			lastkills = mykills;
			lastdamage = data.me.damage;
			lastship = data.me.ship.type;

			Ship ship = data.me.ship;

			int troop_capacity;
			if (ship.type == Ship.AS) {
				troop_capacity = (((data.me.kills * 3) > ship.maxarmies) ? ship.maxarmies : (int) (data.me.kills * 3));
			}
			else if (data.me.ship.type != Ship.SB) {
				troop_capacity = (((data.me.kills * 2) > ship.maxarmies) ? ship.maxarmies : (int) (data.me.kills * 2));
			}
			else {
				troop_capacity = ship.maxarmies;
			}

			int maxspeed = (int)((float)((ship.maxspeed + 2) - (ship.maxspeed + 1)) * ((float)data.me.damage / (float)(ship.maxdamage)));
			if (maxspeed > ship.maxspeed) {
				maxspeed = ship.maxspeed;
			}
			if (maxspeed < 0) {
				maxspeed = 0;
			}

			// fill the buffer with spaces
			BufferUtils.fillWithSpaces(buffer);

			"Maximum:".getChars(0, 8, buffer, 0);

			// speed
			BufferUtils.formatInteger(maxspeed, buffer, 11, 2);
			buffer[13] = '/';
			BufferUtils.formatInteger(ship.maxspeed, buffer, 14, 2);

			// damage
			BufferUtils.formatInteger(ship.maxdamage, buffer, 18, 3);

			// shields
			BufferUtils.formatInteger(ship.maxshield, buffer, 22, 3);

			// number of torps
			buffer[27] = '8';

			// armies
			BufferUtils.formatInteger(troop_capacity, buffer, 38, 2);
			buffer[40] = '/';
			BufferUtils.formatInteger(ship.maxarmies, buffer, 41, 2);

			// fuel
			BufferUtils.formatInteger(ship.maxfuel, buffer, 47, 5);

			// weapon temp
			BufferUtils.formatInteger((ship.maxwpntemp / 10), buffer, 55, 3);

			// engine temp
			BufferUtils.formatInteger((ship.maxegntemp / 10), buffer, 61, 3);

			og.setColor(Color.black);
			og.fillRect(0, metrics.getHeight() * 2 + metrics.getDescent(), view_size.width, metrics.getHeight());

			og.setColor(Color.white);
			og.drawChars(buffer, 0, buffer.length, 26, metrics.getHeight() * 3);
		}

		g.drawImage(offscreen, BORDER, BORDER, null);
	}
}