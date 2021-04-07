package jtrek.config;

import java.util.*;

import java.io.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.message.*;
import jtrek.visual.*;

public class Defaults {
	public final static String DEFAULTS_FILE = "/jtrek/config/defaults.txt";
	
	// use the Netrek Properties in order to get multi-line macros to work
	public static JTrekProperties properties;

	public static boolean browser_mode = false;
	public static boolean keep_peace;
	public static boolean report_kills;
	public static boolean report_kills_in_review;
	public static boolean report_all_in_review;
	public static boolean report_team_in_review;
	public static boolean report_ind_in_review;
	public static boolean show_tractor_pressor;
	public static boolean fill_lock_triangle;
	public static boolean continuous_tractor;
	public static boolean continuous_mouse;
	public static boolean try_udp;
	public static boolean udp_sequence_check;
	public static boolean try_short;
	public static boolean fast_quit_ok;
	public static boolean new_info;
	public static int keep_info;
	public static boolean dont_ping;
	public static boolean def_lite;
	public static boolean use_lite;
	public static boolean use_rsa;
	public static boolean warn_shields;
	public static boolean vary_shields;
	public static boolean vary_hull;
	
	public static boolean sound;

	public static int name_mode;
	public static int show_local;
	public static int show_galactic;
	public static int show_lock;
	public static int udp_client_send;
	public static int udp_client_recv;
	//public static int tclock;
	public static int updates_per_second;
	public static int phaser_message;
	public static int max_scroll_lines;
	public static int beep_lite_cycle_time_player;
	public static int beep_lite_cycle_time_planet;

	public static char[] player_list;
	public static String cloak_chars;
	public static String name;
	public static String password;
	public static boolean autologin;
	public static String server;
	public static int port;

	public static Hashtable macros = new Hashtable();
	public static Hashtable single_macros = new Hashtable();
	public static RCM[] rcms;
	public static RCD[] rcds;
	public static RCD[] preferred_rcds;
	
	static {
		rcms = new RCM[RCM.DEFAULTS.length];
		for(int i = 0; i < rcms.length; ++i) {
			rcms[i] = (RCM)RCM.DEFAULTS[i].clone();
		}
		rcds = RCD.DEFAULTS;
		preferred_rcds = new RCD[rcds.length];
		for(int i = 0; i < rcds.length; ++i) {
			preferred_rcds[i] = (RCD)rcds[i].clone();
		}
	}

	public static String dashboard_class;
	public static String player_list_class;	
	public static String sound_engine_class;
	
	//public static boolean netstat;
	//public static boolean abbr_kmesg;
	
	static JTrekProperties loadDefaults() {
		JTrekProperties defaults = new JTrekProperties();
		InputStream in = null;
		try {
			in = jtrek.config.Defaults.class.getResourceAsStream(DEFAULTS_FILE);
			if(in == null) {
				System.err.println("Couldn't load the defaults file: " + DEFAULTS_FILE);			
			}
			else {
				defaults.load(new BufferedInputStream(in));
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		return defaults;
	}

	/** loadProperties */
	public static void loadProperties() {
		loadProperties("src/jtrek/resources/jtrek.props");
	}

	/** loadProperties */
	public static void loadProperties(String file) {
		BufferedInputStream in = null;
		try {
			System.out.println("Reading " + file);
			in = new BufferedInputStream(new FileInputStream(file), 8192);		
			loadProperties(in);
		}
		catch(FileNotFoundException fe) {
			System.err.println("Property File: " + file + " not found.");
			properties = loadDefaults();
			return;
		}
		try {
			in.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadProperties(InputStream in) {		
		properties = new JTrekProperties(loadDefaults());
		
		try {
		   properties.load(in);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void init() {
		// load the keymap
		String s = properties.getProperty("keymap");
		if(s != null) {
			Keymap.mapKeys(s);
		}

		// load the control keymap
		s = properties.getProperty("ckeymap");
		if(s != null) {
			Keymap.mapControlKeys(s);
		}

		s = properties.getProperty("buttonmap");
		if(s != null) {
			Keymap.mapButtons(s);
		}

		keep_peace = properties.getPropertyAsBoolean("keepPeace");
		report_kills = properties.getPropertyAsBoolean("reportKills");
		report_kills_in_review = properties.getPropertyAsBoolean("reportKillsInReview");
		report_all_in_review = properties.getPropertyAsBoolean("reportAllInReview");
		report_team_in_review = properties.getPropertyAsBoolean("reportTeamInReview");
		report_ind_in_review = properties.getPropertyAsBoolean("reportIndInReview");		
		show_tractor_pressor = properties.getPropertyAsBoolean("showTractorPressor");
		fill_lock_triangle = properties.getPropertyAsBoolean("fillTriangle");
		continuous_tractor = properties.getPropertyAsBoolean("continuousTractor");
		continuous_mouse = properties.getPropertyAsBoolean("continuousMouse");
		try_udp = properties.getPropertyAsBoolean("tryUdp");
		udp_sequence_check = properties.getPropertyAsBoolean("udpSequenceCheck");
		try_short = properties.getPropertyAsBoolean("tryShort");
		sound = properties.getPropertyAsBoolean("sound");

		fast_quit_ok = properties.getPropertyAsBoolean("enableFastQuit");
		new_info = properties.getPropertyAsBoolean("newInfo");
		keep_info = properties.getPropertyAsInt("keepInfo");
		dont_ping = properties.getPropertyAsBoolean("dontPing");
		def_lite = properties.getPropertyAsBoolean("defLite");
		use_lite = properties.getPropertyAsBoolean("useLite");
		use_rsa = properties.getPropertyAsBoolean("useRsa");
		warn_shields = properties.getPropertyAsBoolean("warnShields");
		vary_shields = properties.getPropertyAsBoolean("varyShields");
		vary_hull = properties.getPropertyAsBoolean("varyHull");

		name_mode = properties.getPropertyAsIntWithMax("showPlanetNames", 2);
		show_local = properties.getPropertyAsIntWithMax("showLocal", 3);
		show_galactic = properties.getPropertyAsIntWithMax("showGalactic", 3);
		show_lock = properties.getPropertyAsIntWithMax("showLock", 3);
		udp_client_send = properties.getPropertyAsInt("udpClientSend");
		udp_client_recv = properties.getPropertyAsInt("udpClientReceive");
		updates_per_second = properties.getPropertyAsInt("updatesPerSecond");
		max_scroll_lines = properties.getPropertyAsInt("scrollSaveLines");
		phaser_message = properties.getPropertyAsInt("phaserMsg");
		beep_lite_cycle_time_player = properties.getPropertyAsInt("playerCycleTime");
		beep_lite_cycle_time_planet = properties.getPropertyAsInt("planetCycleTime");

		player_list = properties.getPropertyAsString("playerList").toCharArray();

		name = properties.getPropertyAsString("name");
		password = properties.getPropertyAsString("password");
		autologin = properties.getPropertyAsBoolean("autologin");
		cloak_chars = properties.getPropertyAsString("cloakChars");

		server = properties.getPropertyAsString("server");
		port = properties.getPropertyAsInt("port");

		dashboard_class = properties.getPropertyAsString("dashboardClass");
		player_list_class = properties.getPropertyAsString("playerListClass");
		sound_engine_class = properties.getPropertyAsString("soundEngineClass");

		//netstat = properties.getPropertyAsBoolean("netstats");
		//abbr_kmesg = properties.getPropertyAsBoolean("shortKillMesg");
		//tclock = properties.getPropertyAsInt("clock");

		parseMacros();
	}

	/** parseMacros */
	static void parseMacros() {
		macros.clear();
		Macro macro;
		String key;
		String value;
		Enumeration keys = properties.keys();
		Enumeration values = properties.elements();
top:	while(keys.hasMoreElements()) {
			key = (String)keys.nextElement();
			value = (String)values.nextElement();
			if(key.startsWith("mac.")) {
				try {
					macro = new Macro(key, value);
				}
				catch(Exception e) {
					// use the variable 'value' temporarily
					value = e.getMessage();
					if(value != null) {
						System.err.println("Bad Macro: " + value);
					}
					continue;
				}
				macros.put(new Character(macro.key), macro);
			}
			else if(key.startsWith("dist.")) {
				int index = key.lastIndexOf('.');
				String name = key.substring(index + 1);
				for(int r = 0; r < preferred_rcds.length; ++r) {
					if(name.equals(preferred_rcds[r].name)) {
						preferred_rcds[r].macro = value;
						if(index != 4) {
							preferred_rcds[r].key = (key.charAt(5) == '^') ? 128 + key.charAt(6) : key.charAt(5);
						}
						continue top;
					}
				}
				System.out.println("Unknown distress: " + key);
			}
			else if(key.startsWith("lite.")) {
				int index = key.lastIndexOf('.');
				String name = key.substring(index + 1);
				for(int r = 0; r < preferred_rcds.length; ++r) {
					if(name.equals(preferred_rcds[r].name)) {
						preferred_rcds[r].lite = value;
						continue top;
					}
				}
				System.out.println("Unknown distress: " + key);
			}
			else if(key.startsWith("msg.")) {
				int index = key.lastIndexOf('.');
				String name = key.substring(index + 1);
				for(int r = 0; r < rcms.length; ++r) {
					if(name.equals(rcms[r].name)) {
						rcms[r].macro = value;
						continue top;
					}
				}
				System.out.println("Unknown message: " + key);
			}
		}
		single_macros.clear();
		String singles = properties.getProperty("singleMacro");
		if(singles != null) {
			char[] chars = singles.toCharArray();
			for(int c = 0; c < chars.length; ++c) {
				Character keyc;
				if(chars[c] == '^' && c < chars.length - 1) {
					keyc = new Character((char)(128 + chars[++c]));
				}
				else {
					keyc = new Character(chars[c]);
				}
				Object temp = macros.get(keyc);
				if(temp != null) {				
					single_macros.put(keyc, temp);
				}
			}
		}

		if(def_lite) {
			if(preferred_rcds[DistressConstants.TAKE].lite == null) {
				preferred_rcds[DistressConstants.TAKE].lite = "/c/l";
			}
			if(preferred_rcds[DistressConstants.BASE_OGG].lite == null) {
				preferred_rcds[DistressConstants.BASE_OGG].lite = "/g/m";
			}
			if(preferred_rcds[DistressConstants.PICKUP].lite == null) {
				preferred_rcds[DistressConstants.PICKUP].lite = "/p";
			}
			if(preferred_rcds[DistressConstants.GENERIC].lite == null) {
				preferred_rcds[DistressConstants.GENERIC].lite = "%?%S=SB%{/c%}";
			}
		}
	}

	/** getWindowGeometry */
	public static Rectangle getWindowGeometry(String window) {
		Rectangle rect = new Rectangle(0, 0, 1, 1);

		window += ".geometry";
		String s = properties.getProperty(window);
		if(s == null) {
			System.err.println("Property: " + window + " not found, returning " + rect);
		}
		else {
			try {
				StringTokenizer tok = new StringTokenizer(s, ",", false);
				rect.x = Integer.parseInt(tok.nextToken());
				rect.y = Integer.parseInt(tok.nextToken());
				rect.width = Integer.parseInt(tok.nextToken());
				rect.height = Integer.parseInt(tok.nextToken());
			}
			catch(Exception e) {
				System.err.println("Property: " + window + " , with a value of: " + s + " is not formatted properly, returning " + rect);
			}
		}
		return rect;
	}

	/** getWindowParent */
	public static String getWindowParent(String window) {
		window += ".parent";
		String s = properties.getProperty(window);
		if(s == null) {
			System.err.println("Property: " + window + " not found, returning \"root\".");
			return "root";
		}
		return s;
	}

	/** getWindowMapped */
	public static boolean getWindowVisible(String window) {
		window += ".visible";
		String s = properties.getProperty(window);
		if(s == null) {
			System.err.println("Property: " + window + " not found, returning true.");
			return true;
		}
		try {
			return Boolean.valueOf(s).booleanValue();
		}
		catch(Exception e) {
			System.err.println("Property: " + window + ", with a value of: " + s + " is not a boolean, returning true.");
			return true;
		}
	}

	/** getWindowTitle */
	public static String getWindowTitle(String window) {
		String s = properties.getProperty(window + ".title");
		return (s != null) ? s : window;
	}
}
