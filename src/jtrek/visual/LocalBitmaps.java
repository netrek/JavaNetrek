package jtrek.visual;

import java.awt.image.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.util.*;

public class LocalBitmaps implements ImageObserver {
	final static ImageFilter IMAGE_FILTER = new ColorToGrayImageFilter();

	final static String[] SHIPS = {
		"SC", "DD", "CA", "BB", "AS", "SB", "GA", "AT"	
	};
	
	final static String[] TEAMS = {
		"ind", "fed", "rom", "kli", "ori"
	};

	final static int IND = 0;
	final static int FED = 1;
	final static int ROM = 2;
	final static int KLI = 3;
	final static int ORI = 4;

	final static int SC = 0;
	final static int DD = 1;
	final static int CA = 2;
	final static int BB = 3;
	final static int AS = 4;
	final static int SB = 5;
	final static int GA = 6;
	final static int AT = 7;

	public int[] rosette = new int[256];

	public int ship_size;
	public int hull_size;
	public int shield_size;
	public int torp_size;
	public int torp_cloud_size;
	public int plasma_size;
	public int plasma_cloud_size;
	public int sb_explosion_size;
	public int explosion_size;
	public int emphasis_player_size;
	
	public int background_width;
	public int background_height;

	public int torp_frames = -1;
	public int torp_cloud_frames = -1;
	public int plasma_frames = -1;
	public int plasma_cloud_frames = -1;
	public int sb_explosion_frames = -1;
	public int explosion_frames = -1;
	public int emphasis_player_frames = -1;
	public int planet_frames = -1;

	int last_team = -1;
	int last_ship = -1;
	
	Image background;

	// ships
	Image[][] ships = new Image[5][8];
	Image myship;

	// weapons
	Image my_torp;
	Image my_torp_cloud;
	Image my_plasma;
	Image my_plasma_cloud;
	Image friend_torp;
	Image friend_torp_cloud;
	Image friend_plasma;
	Image friend_plasma_cloud;
	Image enemy_torp;
	Image enemy_torp_cloud;
	Image enemy_plasma;
	Image enemy_plasma_cloud;

	// ship explosions
	Image sb_explosion;
	Image explosion;

	// cloak
	Image cloak;
	
	// hull
	Image hull;

	// shield
	Image my_shield;
	Image[] shield = new Image[5];
	
	// planets
	Image[] bare_planets = new Image[5];
	Image[] agri_planets = new Image[5];
	Image noinfo;

	Image teams;
	Image resources;

	// emphasis
	Image emphasis_player;
	
	/** 
	* Construct a new LocalBitmaps
	*/
	public LocalBitmaps(Component component) {	
		
		background = ImageLoader.loadImage("/jtrek/images/local/background.gif");
		
		cloak = ImageLoader.loadImage("/jtrek/images/local/cloak.gif");
		hull = ImageLoader.loadImage("/jtrek/images/local/hull.gif");
		my_shield = ImageLoader.loadImage("/jtrek/images/local/shield.gif");	

		for(int t = IND; t <= ORI; ++t) {
			// load ships
			for(int s = SC; s <= AT; ++s) {
				ships[t][s] = ImageLoader.loadImage("/jtrek/images/local/ships/" + TEAMS[t] + '/' + SHIPS[s] + ".gif");
			}

			// load planets
			bare_planets[t] = ImageLoader.loadImage("/jtrek/images/local/planets/" + TEAMS[t] + "/bare.gif");
			agri_planets[t] = ImageLoader.loadImage("/jtrek/images/local/planets/" + TEAMS[t] + "/agri.gif");

			// load shields
			shield[t] = component.createImage(new FilteredImageSource(my_shield.getSource(), new GrayToColorImageFilter(Team.TEAMS[t].getColor().getRGB())));
		}

		// load planets and planet overlays
		noinfo = ImageLoader.loadImage("/jtrek/images/local/planets/noinfo.gif");
		teams = ImageLoader.loadImage("/jtrek/images/local/planets/teams.gif");
		resources = ImageLoader.loadImage("/jtrek/images/local/planets/resources.gif");
		
		// load weapons
		my_torp = ImageLoader.loadImage("/jtrek/images/local/weapons/my/torp.gif");
		my_torp_cloud = ImageLoader.loadImage("/jtrek/images/local/weapons/my/torp_cloud.gif");
		my_plasma = ImageLoader.loadImage("/jtrek/images/local/weapons/my/plasma.gif");
		my_plasma_cloud = ImageLoader.loadImage("/jtrek/images/local/weapons/my/plasma_cloud.gif");
		
		friend_torp = ImageLoader.loadImage("/jtrek/images/local/weapons/friend/torp.gif");
		friend_torp_cloud = ImageLoader.loadImage("/jtrek/images/local/weapons/friend/torp_cloud.gif");
		friend_plasma = ImageLoader.loadImage("/jtrek/images/local/weapons/friend/plasma.gif");
		friend_plasma_cloud = ImageLoader.loadImage("/jtrek/images/local/weapons/friend/plasma_cloud.gif");
		
		enemy_torp = ImageLoader.loadImage("/jtrek/images/local/weapons/enemy/torp.gif");
		enemy_torp_cloud = ImageLoader.loadImage("/jtrek/images/local/weapons/enemy/torp_cloud.gif");
		enemy_plasma = ImageLoader.loadImage("/jtrek/images/local/weapons/enemy/plasma.gif");
		enemy_plasma_cloud = ImageLoader.loadImage("/jtrek/images/local/weapons/enemy/plasma_cloud.gif");
		
		sb_explosion = ImageLoader.loadImage("/jtrek/images/local/explosion.gif");
		explosion = ImageLoader.loadImage("/jtrek/images/local/SBexplosion.gif");
		
		emphasis_player = ImageLoader.loadImage("/jtrek/images/local/emp_player.gif");

		component.prepareImage(ships[0][0], this);
		component.prepareImage(background, this);
		component.prepareImage(hull, this);
		component.prepareImage(my_shield, this);
		component.prepareImage(my_torp, this);
		component.prepareImage(my_torp_cloud, this);
		component.prepareImage(my_plasma, this);
		component.prepareImage(my_plasma_cloud, this);
		component.prepareImage(explosion, this);
		component.prepareImage(sb_explosion, this);
		component.prepareImage(emphasis_player, this);
		component.prepareImage(noinfo, this);
	}

	public boolean imageUpdate(Image img, int flags, int x, int y, int width, int height) {
		if(img == ships[0][0] && (flags & ImageObserver.WIDTH) != 0) {
			ship_size = width;

			// initialize rosette
			for(int r = rosette.length; --r >= 0;) {
				rosette[r] = (((r + 8) / 16) & 15) * width;
			}

			// enough of the image is loaded for now
			return false;
		}			
		if(img == hull && (flags & ImageObserver.WIDTH) != 0) {
			hull_size = width;
			// enough of the image is loaded for now
			return false;
		}
		if(img == my_shield && (flags & ImageObserver.WIDTH) != 0) {
			shield_size = width;
			// enough of the image is loaded for now
			return false;
		}
		int dimensions = ImageObserver.WIDTH | ImageObserver.HEIGHT;
		if(img == background && (flags & dimensions) == dimensions) {
			background_width = width;
			background_height = height;
			return false;
		}
		if(img == my_torp && (flags & dimensions) == dimensions) {
			torp_size = width;
			torp_frames = height / width;
			// enough of the image is loaded for now
			return false;
		}
		if(img == my_torp_cloud && (flags & dimensions) == dimensions) {
			torp_cloud_size = width;
			torp_cloud_frames = height / width;
			// enough of the image is loaded for now
			return false;
		}
		if(img == my_plasma && (flags & dimensions) == dimensions) {
			plasma_size = width;
			plasma_frames = height / width;
			// enough of the image is loaded for now
			return false;
		}
		if(img == my_plasma_cloud && (flags & dimensions) == dimensions) {
			plasma_cloud_size = width;
			plasma_cloud_frames = height / width;
			// enough of the image is loaded for now
			return false;
		}
		if(img == explosion && (flags & dimensions) == dimensions) {
			explosion_size = width;
			explosion_frames = height / width;
			// continue loading until the whole image is loaded
			return (flags & ImageObserver.ALLBITS) != ImageObserver.ALLBITS;
		}
		if(img == sb_explosion && (flags & dimensions) == dimensions) {
			sb_explosion_size = width;
			sb_explosion_frames = height / width;
			// continue loading until the whole image is loaded
			return (flags & ImageObserver.ALLBITS) != ImageObserver.ALLBITS;
		}
		if(img == emphasis_player && (flags & dimensions) == dimensions) {
			emphasis_player_size = width;
			emphasis_player_frames = height / width;
			// enough of the image is loaded for now
			return false;
		}
		if(img == noinfo && (flags & dimensions) == dimensions) {
			planet_frames = height / width;
			// enough of the image is loaded for now
			return false;
		}
		return true;
	}

	/** createMyShip */
	public void createMyShip(Component component, int team, int ship) {
		if(team != last_team || ship != last_ship) {
			last_team = team;
			last_ship = ship;
			if(myship != null) {
				myship.flush();
			}
			myship = component.createImage(
				new FilteredImageSource(ships[team][ship].getSource(), IMAGE_FILTER));

			component.prepareImage(myship, component);
		}
	}
}
