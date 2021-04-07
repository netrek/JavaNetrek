package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;

public class StatsPanel extends BasePanel {
	final static int BAR_X = 77;
	final static int BAR_WIDTH = 120;

	final static String[] LABEL = {
		"Shields",
		"Damage",
		"Fuel",
		"Warp",
		"Weapon Temp",
		"Engine Temp",
		"Armies"
	};

	final static ColorBar[] BARS = {
		new ColorBar(  100,   20,  100, true),
		new ColorBar(  100,    0,    0),
		new ColorBar(10000, 2000,  100, true),
		new ColorBar(    9),
		new ColorBar( 1200,    0, 1200),
		new ColorBar( 1200,    0, 1200),
		new ColorBar(   10)
	};

	Universe data;
	Ship ship = null;
	float kills = 0.0f;
	Image offscreen;


	/** PlayerListPanel */
	public StatsPanel(String name, Universe data) {
		super(name);
		this.data = data;
	}

	/** setup */
	void setup() {
		super.setup();
		offscreen = createImage(view_size.width - 5, getFontMetrics(variable_font).getHeight());
	}

	/** paint */
	public void paint(Graphics g) {
		update(g);
		paintBorder(g);
	}

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(variable_font);
		FontMetrics metrics = og.getFontMetrics();
		int line_height = metrics.getHeight();
		int bar_height = line_height - 3;

		if(ship != data.me.ship || kills != data.me.kills) {
			calibrateStats();
		}
		int y = 7;
		for(int p = 0; p < LABEL.length; ++p) {

			og.setColor(Color.black);
			og.fillRect(0, 0, view_size.width - 5, line_height);

			// draw the label
			og.setColor(Color.white);
			og.drawString(LABEL[p], BAR_X - 5 - metrics.stringWidth(LABEL[p]), metrics.getAscent());

			// get the value, and make sure it is within bounds
			BARS[p].draw(og, BAR_X, 1, BAR_WIDTH, bar_height, getStatsValue(p));

			g.drawImage(offscreen, 5, y, null);

			y += line_height;
		}
	}

	/** getStatsValue */
	int getStatsValue(int index) {
		switch(index) {
		case 0 :
			return data.me.shield;
		case 1 :
			return data.me.damage;
		case 2 :
			return data.me.fuel;
		case 3 :
			return data.me.speed;
		case 4 :
			return data.me.wtemp;
		case 5 :
			return data.me.etemp;
		default :
			return data.me.armies;
		}
	}

	/** calibrateStats */
	void calibrateStats() {
		ship = data.me.ship;
		kills = data.me.kills;
		BARS[0].setValues(ship.maxshield, 0.2f, 0.0f);
		BARS[1].setValues(ship.maxdamage, 0, 0);
		BARS[2].setValues(ship.maxfuel, 0.2f, 0.05f);
		BARS[3].setMax(ship.maxspeed);
		BARS[4].setValues(ship.maxwpntemp, 0.667f, 1.0f);
		BARS[5].setValues(ship.maxegntemp, 0.667f, 1.0f);

		int max_kills = data.me.ship.maxarmies;
		if(kills > 0f) {
			if (ship.type == Ship.AS) {
				max_kills = (((kills * 3) > ship.maxarmies) ? ship.maxarmies : (int) (kills * 3));
			}
			else if (data.me.ship.type != Ship.SB) {
				max_kills = (((kills * 2) > ship.maxarmies) ? ship.maxarmies : (int) (kills * 2));
			}
		}
		BARS[6].setMax(max_kills);
	}
}