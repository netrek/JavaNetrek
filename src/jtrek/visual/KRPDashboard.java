package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.util.*;

public class KRPDashboard extends Dashboard {
	final static int BAR_WIDTH = 56;
	final static int BAR_HEIGHT = 9;

	char[] buffer = new char[80];

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(fixed_font);
		FontMetrics metrics = og.getFontMetrics();

		Color color;
		float kills;

		if ((data.me.flags & (Player.PLOCK | Player.OBSERV)) == (Player.PLOCK | Player.OBSERV))
			kills = data.players[data.me.playerl].kills;
		else
			kills = data.me.kills;

		clearOffscreen(og);

		// draw the flags
		drawFlags(og);

		// draw speed
		int cur_max = (int)((data.me.ship.maxspeed + 2) - (data.me.ship.maxspeed + 1) * ((float) data.me.damage / (float) (data.me.ship.maxdamage)));
		if (cur_max > data.me.ship.maxspeed)
			cur_max = data.me.ship.maxspeed;
		if (cur_max < 0)
			cur_max = 0;
		if (data.me.speed >= data.me.ship.maxspeed - 2)
			color = Color.red;
		else
			color = Color.green;
		drawBar(og, "Sp", 90, 2, data.me.speed, cur_max, data.me.ship.maxspeed, 3, color);

		// draw shields
		int value = (100 * data.me.shield) / data.me.ship.maxshield;
		if (value <= 16)
			color = Color.red;
		else if (value <= 66)
			color = Color.yellow;
		else
			color = Color.green;
		drawBar(og, "Sh", 90, 14, data.me.ship.maxshield - data.me.shield, data.me.ship.maxshield, data.me.ship.maxshield, 3, color);
		
		// draw damage
		value = (100 * (data.me.ship.maxdamage - data.me.damage)) / data.me.ship.maxdamage;
		if (value <= 16)
			color = Color.red;
		else if (value <= 66)
			color = Color.yellow;
		else
			color = Color.green;
		drawBar(og, "Hu", 90, 26, data.me.damage, data.me.ship.maxdamage, data.me.ship.maxdamage, 3, color);

		// draw armies
		if (data.me.ship.type == Ship.AS)
			cur_max = (((kills * 3) > data.me.ship.maxarmies) ? data.me.ship.maxarmies : (int) (kills * 3));
		else if (data.me.ship.type == Ship.SB)
			cur_max = data.me.ship.maxarmies;
		else
			cur_max = (((kills * 2) > data.me.ship.maxarmies) ? data.me.ship.maxarmies : (int) (kills * 2));
		value = data.me.armies;
		if (value <= 0)
			color = Color.green;
		else
			color = Color.red;
		drawBar(og, "Ar", 218, 2, data.me.armies, cur_max, data.me.ship.maxarmies, 3, color);

		// draw weapon temp
		value = (100 * data.me.wtemp) / data.me.ship.maxwpntemp;
		if (value <= 16)
			color = Color.green;
		else if (value <= 66)
			color = Color.yellow;
		else
			color = Color.red;
		drawBar(og, "Wt", 218, 14, data.me.wtemp / 10, data.me.ship.maxwpntemp / 10, data.me.ship.maxwpntemp / 10, 3 ,color);

		// draw the engine temp
		value = (100 * data.me.etemp) / data.me.ship.maxegntemp;
		if (value <= 16)
			color = Color.green;
		else if (value <= 66)
			color = Color.yellow;
		else
			color = Color.red;
		drawBar(og, "Et", 218, 26, data.me.etemp / 10, data.me.ship.maxegntemp / 10, data.me.ship.maxegntemp / 10, 3 ,color);
	
		// draw the fuel
		value = ((100 * data.me.fuel) / data.me.ship.maxfuel);
		if (value <= 16)
			color = Color.red;
		else if (value <= 66)
			color = Color.yellow;
		else
			color = Color.green;
		drawBar(og, "Fu", 346, 2, data.me.fuel, data.me.ship.maxfuel, data.me.ship.maxfuel, 5, color);

		g.drawImage(offscreen, BORDER, BORDER, null);
	}

	void drawFlags(Graphics g) {
		Player player;

		if ((data.me.flags & (Player.PLOCK | Player.OBSERV)) == (Player.PLOCK | Player.OBSERV))
			player = data.players[data.me.playerl];
		else
			player = data.me;

		// draw the flags
		char[] buffer = new char[12];
		buffer[0] = ((data.me.flags & Player.SHIELD) != 0) ? 'S' : ' ';
		if ((data.me.flags & Player.GREEN) != 0)
			buffer[1] = 'G';
		else if ((data.me.flags & Player.YELLOW) != 0)
			buffer[1] = 'Y';
		else
			buffer[1] = 'R';
		buffer[2] = ((data.me.flags & (Player.PLLOCK | Player.PLOCK)) != 0) ? 'L' : ' ';
		buffer[3] = ((data.me.flags & Player.REPAIR) != 0) ? 'R' : ' ';
		buffer[4] = ((data.me.flags & Player.BOMB) != 0) ? 'B' : ' ';
		buffer[5] = ((data.me.flags & Player.ORBIT) != 0) ? 'O' : ' ';
		if (data.me.ship.type == Ship.SB) 
			buffer[6] = ((data.me.flags & Player.DOCKOK) != 0) ? 'D' : ' ';
		else
			buffer[6] = ((data.me.flags & Player.DOCK) != 0) ? 'D' : ' ';
		buffer[7] = ((data.me.flags & Player.CLOAK) != 0) ? 'C' : ' ';
		buffer[8] = ((data.me.flags & Player.WEP) != 0) ? 'W' : ' ';
		buffer[9] = ((data.me.flags & Player.ENG) != 0) ? 'E' : ' ';
		if ((data.me.flags & Player.PRESS) != 0)
			buffer[10] = 'P';
		else if ((data.me.flags & Player.TRACT) != 0)
			buffer[10] = 'T';
		else
			buffer[10] = ' ';
		if ((data.me.flags & Player.BEAMUP) != 0)
			buffer[11] = 'u';
		else if ((data.me.flags & Player.BEAMDOWN) != 0)
			buffer[11] = 'd';
		else
			buffer[11] = ' ';

		g.drawString("Flags", 2, 11);
		g.drawChars(buffer, 0, buffer.length, 2, 23);

		if(data.status.tourn) {
			g.setFont(bold_fixed_font);
			g.drawString("T", 74, 17);
			g.setFont(fixed_font);
		}

		if(player.kills > 0f) {
			g.drawString("Kills: " + BufferUtils.formatFloat(player.kills, 2), 346, 23);
		}

		if (player.ntorp > 0) {
			g.drawString("Torps: " + player.ntorp, 346, 35);
		}
	}

	void drawBar(Graphics g, String header, int x, int y, int value, int tmpmax, int max, int digits, Color color)  {
		char[] buffer = null;
		switch (digits) {
		case 3:
			buffer = new char[11];
			buffer[0] = header.charAt(0);
			buffer[1] = header.charAt(1);
			buffer[2] = '[';
			BufferUtils.formatInteger(value, buffer, 3, 3);
			buffer[6] = '/';
			BufferUtils.formatInteger(tmpmax, buffer, 7, 3);
			buffer[10] = ']';
			break;
		case 5:
			buffer = new char[15];
			buffer[0] = header.charAt(0);
			buffer[1] = header.charAt(1);
			buffer[2] = '[';
			BufferUtils.formatInteger(value, buffer, 3, 5);
			buffer[8] = '/';
			BufferUtils.formatInteger(tmpmax, buffer, 9, 5);
			buffer[14] = ']';
			break;
		}

		int wt = (int) (BAR_WIDTH * ((float)tmpmax / (float)max));
		int wv = (int) (BAR_WIDTH * ((float)value / (float)max));
		if (wt > BAR_WIDTH)
			wt = BAR_WIDTH;
		if (wv > BAR_WIDTH)
			wv = BAR_WIDTH;

		g.setColor(Color.white);
		g.drawChars(buffer, 0, buffer.length, x, y + g.getFontMetrics().getAscent());

		x += g.getFontMetrics().charsWidth(buffer, 0, buffer.length);

		g.setColor(color);

		g.drawRect(x, y, BAR_WIDTH, BAR_HEIGHT);
		g.fillRect(x, y, wv, BAR_HEIGHT);

		if (wt >= wv && wt > 0) {
			g.drawLine(x + wt, y, x + wt, y + BAR_HEIGHT); 
			g.drawLine(x + wt, y + 4, x + BAR_WIDTH, y + 4);
		}

		if (wv > 0)
			g.fillRect(x, y, wv, BAR_HEIGHT);
	}
}