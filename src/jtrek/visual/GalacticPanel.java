package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.comm.*;
import jtrek.config.*;

public class GalacticPanel extends BaseMapPanel {
	static float SCALE = (float)500 / (float)Universe.PIXEL_SIZE;

	GalacticBitmaps bitmaps;

	/** Offscreen image and Graphics object */
	Image planet_offscreen;

	public boolean redraw_all_planets = true;

	/** Constructor */
	GalacticPanel(String name, NetrekFrame view, Universe data, InformationPanel information, Beeplite beeplite) {
		super(name, view, data, information, beeplite);
	}

	/** setup */
	void setup() {
		super.setup();
		planet_offscreen = createImage(view_size.width, view_size.height);
		bitmaps = new GalacticBitmaps(this);	
	}

	/** getCourse */
	byte getCourse() {	
		int xclick = mx * Universe.PIXEL_SIZE / this.getWidth();
		int yclick = my * Universe.PIXEL_SIZE / this.getHeight();
		return (byte)Math.rint(
			Math.atan2(xclick - data.me.x, data.me.y - yclick) / Math.PI * 128);
	}

	/** getTarget */
	Object getTarget(int target_type) {
		return data.getTarget((mx * Universe.PIXEL_SIZE) / this.getWidth(), (my * Universe.PIXEL_SIZE) / this.getHeight(), target_type);
	}

	/**
	 * Added support to rescale galactic window based on resizing
	 */
	private void updateScale() {
		if (this.getWidth() < 100) { // abort recalculation if window too small
			return;
		}
		GalacticPanel.SCALE = (float)this.getWidth() /  (float)Universe.PIXEL_SIZE;
	}
	/** update */
	public void update(Graphics g) {
		this.updateScale();
		redraw_all_planets = true;
		
		Graphics og = offscreen.getGraphics();
		og.setFont(getFont());
		Graphics pg = planet_offscreen.getGraphics();
		pg.setFont(getFont());

		FontMetrics metrics = og.getFontMetrics();

		int font_ascent = metrics.getAscent();
		int font_ascent_divided_by_2 = font_ascent / 2;

		if(redraw_all_planets) {
			pg.setColor(Color.black);			
			pg.fillRect(0, 0, view_size.width, view_size.height);
		}

		int dx, dy;
		Player me = data.me;

		// draw out the planets
		for(int p = data.planets.length; --p >= 0;) {
			Planet planet = data.planets[p];
			if (!redraw_all_planets && (planet.flags & Planet.REDRAW) == 0) {
				continue;
			}

			// turn redraw flag off.
			planet.flags &= ~Planet.REDRAW;

			dx = (int)(planet.x * SCALE) - bitmaps.planet_radius;
			dy = (int)(planet.y * SCALE) - bitmaps.planet_radius;
			
			// do we have info on the planet?
			Image planet_image = null;
			int source_y = 0;
			if ((planet.info & me.team.bit) != 0) {
				planet_image = bitmaps.planets[Defaults.show_galactic][planet.owner.no];
				// for the odd bitmaps types resources and moo we need to calculate the source_y
				if(Defaults.show_galactic % 2 != 0) {
					int planet_type = (planet.armies > 4) ? 4 : 0;
					if ((planet.flags & Planet.REPAIR) != 0) {
						planet_type += 2;
					}
					if ((planet.flags & Planet.FUEL) != 0) {
						planet_type += 1;
					}
					source_y = planet_type * bitmaps.planet_size;
				}
				pg.setColor(planet.getColor());
			}
			else {
				planet_image = bitmaps.noinfo;
				pg.setColor(Team.TEAMS[Team.NOBODY].getColor());
			}
			
			// draw the planet, transparent pixels are drawn in the planet's color.
			if(!pg.drawImage(planet_image, 
							 dx, 
							 dy, 
							 dx + bitmaps.planet_size, 
							 dy + bitmaps.planet_size, 
							 0, source_y, 
							 bitmaps.planet_size, 
							 source_y + bitmaps.planet_size, 
							 Color.black, 
							 null)) {
				// The image wasn't fully loaded, the planet needs to be draw again, next update.
				planet.flags |= Planet.REDRAW;
			}

			// draw the name of the planet
			String mapstr = planet.name.length() > 3 ? planet.name.substring(0, 3) : planet.name;
			pg.drawString(mapstr, dx + 8 - metrics.stringWidth(mapstr) / 2, dy + 24);
		}

		redraw_all_planets = false;

		og.drawImage(planet_offscreen, 0, 0, null); 

		// draw out any highlights around planets
		if(Defaults.use_lite) {
			for(int p = data.planets.length; --p >= 0;) {
				if(beeplite.planet[p] > 0) {
					Planet planet = data.planets[p];

					int source_y = bitmaps.emphasis_planet_size * (beeplite.planet[p] % bitmaps.emphasis_planet_frames);				
					int ex = (int)(planet.x * SCALE) - bitmaps.emphasis_planet_size / 2;
					int ey = (int)(planet.y * SCALE) - bitmaps.emphasis_planet_size / 2;

					og.drawImage(
							bitmaps.emphasis_planet, 
							ex, 
							ey, 
							ex + bitmaps.emphasis_planet_size, 
							ey + bitmaps.emphasis_planet_size, 
							0,
							source_y,
							bitmaps.emphasis_planet_size, 
							source_y + bitmaps.emphasis_planet_size, 
							null);
					
					// Turn redraw on until done highlighting
					if(--beeplite.planet[p] > 0) {
						planet.flags |= Planet.REDRAW;
					}
				}
			}
		}
		
		// draw the players
		for(int p = data.players.length; --p >= 0;) {
			Player player = data.players[p];

			if (player.status != Player.ALIVE || (player.flags & Player.OBSERV) != 0) {
				continue;
			}

			// determine if the player is cloaked
			boolean cloaked = ((player.flags & Player.CLOAK) != 0);
			
			// determine what string to place on the screen for this player
			String mapstr = cloaked ? Defaults.cloak_chars : player.mapchars;

			int mapstr_width = metrics.stringWidth(mapstr);
			
			// get the coordinates of where to place the string
			dx = (int)(player.x * SCALE) - mapstr_width / 2;
			dy = (int)(player.y * SCALE) + font_ascent_divided_by_2;

			// fill in the area behind the string with black
			og.setColor(Color.black);
			og.fillRect(dx, dy - font_ascent, mapstr_width, font_ascent);

			// draw the string in the appropriate color
			og.setColor(cloaked ? Color.white : player.getColor());
			og.drawString(mapstr, dx, dy - 2);

			if (Defaults.use_lite && beeplite.player[p] > 0 && (beeplite.flag & Beeplite.PLAYERS_MAP) != 0) {

				int source_y = bitmaps.emphasis_player_size * (beeplite.player[p] % bitmaps.emphasis_player_frames);

				int ex = (int)(player.x * SCALE) - bitmaps.emphasis_player_size / 2;
				int ey = (int)(player.y * SCALE) - bitmaps.emphasis_player_size / 2;

				og.drawImage(
						bitmaps.emphasis_player, 
						ex,
						ey, 
						ex + bitmaps.emphasis_player_size, 
						ey + bitmaps.emphasis_player_size, 
						0,
						source_y,
						bitmaps.emphasis_player_size, 
						source_y + bitmaps.emphasis_player_size, 
						null);

				--beeplite.player[p];
			}
		}

		// show lock triangle on galactic map
		if((Defaults.show_lock % 2) != 0) {
			og.setColor(Color.white);
			if ((me.flags & Player.PLOCK) != 0) {
				// locked onto a ship
				Player player = data.players[me.playerl];
				if (player.status == Player.ALIVE && (player.flags & Player.CLOAK) == 0) {
					dx = (int)(player.x * SCALE);
					dy = (int)(player.y * SCALE) + 8;
					drawTriangle(og, dx, dy, false);					
				}
			}
			else if ((me.flags & Player.PLLOCK) != 0) {
				// locked onto a planet
				Planet planet = data.planets[me.planet];
				dx = (int)(planet.x * SCALE);
				dy = (int)(planet.y * SCALE) - 14;
				drawTriangle(og, dx, dy, true);
			}
		}

		g.drawImage(offscreen, BORDER, BORDER, null);
	}
}