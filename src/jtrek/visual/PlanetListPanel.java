package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.util.*;

public class PlanetListPanel extends BasePanel {
	final static String HEADER = "Planet Name      own armies REPAIR FUEL AGRI info";

	Universe data;

	char[] buffer = new char[51];

	/** Offscreen image and Graphics object */
	Image offscreen;

	/** PlayerListPanel */
	public PlanetListPanel(String name, Universe data) {
		super(name);
		this.data = data;
	}

		/** setup */
	void setup() {
		super.setup();
		offscreen = createImage
		  (view_size.width, getFontMetrics(fixed_font).getHeight() - 2);
	}

	/** paint */
	public void paint(Graphics g) {
		update(g);
		paintBorder(g);
	}

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		int line_height = og.getFontMetrics().getHeight() - 2;
		int y = BORDER;
		og.setFont(fixed_font);
		og.setColor(Color.black);
		og.fillRect(0, 0, view_size.width, line_height - 2);
		og.setColor(Color.white);
		og.drawString(HEADER, 5, line_height);
		g.drawImage(offscreen, BORDER, y, null);
		Planet planet;
		for(int p = 0; p < data.planets.length; ++p) {
			y += line_height;
			planet = data.planets[p];
			og.setColor(Color.black);
			og.fillRect(0, 0, view_size.width, line_height);
			og.setColor(planet.getColor());
			fillBufferWithPlanetInfo(planet);			
			og.drawChars(buffer, 0, 51, 5, line_height - 2);
			g.drawImage(offscreen, BORDER, y, null);
		}
	}

	/** fillBufferWithPlanetInfo */
	void fillBufferWithPlanetInfo(Planet planet) {
		BufferUtils.fillWithSpaces(buffer);
		planet.name.getChars(0, (planet.name.length() < 16 ? planet.name.length() : 16), buffer, 0);
		planet.owner.abbrev.getChars(0, 3, buffer, 17);
		BufferUtils.formatInteger(planet.armies, buffer, 21, 3);
		if((planet.flags & Planet.REPAIR) != 0) {
			"REPAIR".getChars(0, 6, buffer, 28);
		}
		if((planet.flags & Planet.FUEL) != 0) {
			"FUEL".getChars(0, 4, buffer, 35);
		}
		if((planet.flags & Planet.AGRI) != 0) {
			"AGRI".getChars(0, 4, buffer, 40);
		}
		for(int t = 1; t < Team.TEAMS.length; ++t) {
			if((planet.info & Team.TEAMS[t].bit) != 0) {
				buffer[46 + t] = Team.TEAMS[t].letter;
			}
		}
	}

}