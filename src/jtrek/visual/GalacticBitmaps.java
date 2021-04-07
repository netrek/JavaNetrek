package jtrek.visual;

import java.awt.*;
import java.awt.image.ImageObserver;
import jtrek.Copyright;

public class GalacticBitmaps implements ImageObserver {
	final static String[] TEAMS = {
			"ind", "fed", "rom", "kli", "ori"
		};

		// planets
		Image[][] planets = new Image[4][];
		Image noinfo;

		// emphasis
		Image emphasis_player;
		Image emphasis_planet;

		public int planet_size = 0;
		public int planet_radius = 0;
		public int emphasis_player_size = 0;
		public int emphasis_planet_size = 0;

		public int emphasis_player_frames = 1;
		public int emphasis_planet_frames = 1;

		/** 
		* Construct a new GalacticBitmaps
		*/
		public GalacticBitmaps(Component component) {

			planets[0] = new Image[5];
			planets[1] = new Image[5];
			planets[2] = new Image[5];
			planets[3] = new Image[5];
	 
			// load planets
			for(int p = 0; p < 5; ++p) {
				planets[0][p] = ImageLoader.loadImage("/jtrek/images/galactic/planets/teams/" + TEAMS[p] + ".gif");
				planets[1][p] = ImageLoader.loadImage("/jtrek/images/galactic/planets/resources/" + TEAMS[p] + ".gif");
				planets[2][p] = ImageLoader.loadImage("/jtrek/images/galactic/planets/nothing/" + TEAMS[p] + ".gif");
				planets[3][p] = ImageLoader.loadImage("/jtrek/images/galactic/planets/moo/" + TEAMS[p] + ".gif");
			}

			noinfo = ImageLoader.loadImage("/jtrek/images/galactic/planets/noinfo.gif");

			emphasis_player = ImageLoader.loadImage("/jtrek/images/galactic/emp_player.gif");
			emphasis_planet = ImageLoader.loadImage("/jtrek/images/galactic/emp_planet.gif");

			component.prepareImage(noinfo, this);
			component.prepareImage(emphasis_player, this);
			component.prepareImage(emphasis_planet, this);
		}

		public boolean imageUpdate(Image img, int flags, int x, int y, int width, int height) {
			if(img == noinfo && (flags & ImageObserver.WIDTH) != 0) {
				planet_size = width;
				planet_radius = width / 2;
				return false;
			}
			int dimensions = ImageObserver.WIDTH | ImageObserver.HEIGHT;
			if(img == emphasis_player && (flags & dimensions) == dimensions) {
				emphasis_player_size = width;
				emphasis_player_frames = height / width;
				return false;
			}
			if(img == emphasis_planet && (flags & dimensions) == dimensions) {
				emphasis_planet_size = width;
				emphasis_planet_frames = height / width;
				return false;
			}
			return true;
		}
}
