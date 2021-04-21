package jtrek;

import jtrek.comm.*;
import jtrek.visual.*;
import jtrek.data.*;
import jtrek.config.*;

import java.awt.*;
import java.io.*;

public class Main extends Thread implements Copyright {

	public Universe data;
	public Communications comm;	
	public NetrekFrame view;
	boolean ghost_start = false;

	public Main(String[] args) {
		super("JTrek-Main");

		String server = null;
		int port = 0;
		String defaults_file = null;
		String name = null;
		String password = null;
		int ghost_slot = 0;
		int base_udp_local_port = 0;
		int socket_number = 0;
		boolean check_metaserver = true; // setting metaserver to be enabled by default

		for(int a = 0; a < args.length; ++a) {
			if(args[a].length() != 2 || args[a].charAt(0) != '-') {
				System.out.println("jtrek: unknown option '" + args[a] + '\'');
				printUsage();
				return;
			}
			switch(args[a].charAt(1)) {
			case 'B':
				Defaults.browser_mode = true;
				break;
			case 'h':
				try {
					server = args[++a];
					check_metaserver = false; // if we set a server, we dont check metaserver
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("specify a host after '-h'");
					return;
				}
				break;
			case 'p':
				try {
					port = Integer.parseInt(args[++a]);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("specify a port number after '-p'");
					return;
				}
				catch(NumberFormatException n) {
					System.out.println("expecting a number after '-p'");
					return;
				}
				break;
			case 'r':
				if(a + 1 >= args.length) {
					System.out.println("specify a file name '-r'");
					return;
				}
				defaults_file = args[++a];
				break;
			case 'G':
				ghost_start = true;
				try {
					ghost_slot = Integer.parseInt(args[++a]);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("specify a slot after '-G'");
					return;
				}
				catch(NumberFormatException n) {
					System.out.println("expecting a number after '-G'");
					return;
				}
				break;
			case 's':
				try {
					socket_number = Integer.parseInt(args[++a]);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("specify a socket number after '-s'");
					return;
				}
				catch(NumberFormatException n) {
					System.out.println("expecting a number after '-s'");
					return;
				}
				break;
			case 'u':
				printUsage();
				return;
			case 'A':
				try {
					password = args[++a];
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("specify a password after '-A'");
					return;
				}
				break;
			case 'C':
				try {
					name = args[++a];
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("specify a character name after '-C'");
					return;
				}
				break;
			case 'U':
				try {
					base_udp_local_port = Integer.parseInt(args[++a]);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("specify a UDP port after '-U'");
					return;
				}
				catch(NumberFormatException n) {
					System.out.println("expecting a number after '-U'");
					return;
				}
				break;
			case 'm':
				check_metaserver = true;
				break;
			case 'v':
				System.out.println(VERSION);
				return;
			case 'S':
				System.getProperties().list(System.out);
				return;
			default:
				System.out.println("jtrek: unknown option '" + args[a] + '\'');
				printUsage();
				return;
			}
		}
		
		if(defaults_file != null) {
			Defaults.loadProperties(defaults_file);
		}
		else if(!Defaults.browser_mode) {
			Defaults.loadProperties();
		}

		Defaults.init();

		if(name != null) {
			Defaults.name = name;
		}
		if(password != null) {
			Defaults.password = password;
		}
		
		data = new Universe();
		comm = new Communications(data, base_udp_local_port);
		view = new NetrekFrame(comm, data);
		comm.setView(view);
		
		if(ghost_start) {
			comm.setGhostStartSlot(ghost_slot);
		}
		
		// start on the game by listening for a server connect on a given socket
		if(socket_number > 0 && server == null) {
			if(!comm.connectToServer(socket_number)) {
				if(!Defaults.browser_mode) {
					System.exit(1);
				}
				return;
			}
			start();
			return;
		}
		
		if (check_metaserver) {
			MetaServerEntry[] entries = (new MetaServerParser()).readFromMetaServer();
			MetaServerPanel meta_panel = new MetaServerPanel("MetaServer", entries);
			Window win = meta_panel.detachWindow(null);
			meta_panel.setBounds(new java.awt.Rectangle(10, 10, 440, (entries.length + 1) * 20 + 6));
			meta_panel.setVisible(true);
			
			int entry_index =  meta_panel.waitForResult();
			meta_panel.setVisible(false);
			win.dispose();
			if (entry_index == -1) {
				System.out.println("OK, Bye!");
				System.exit(0);
				return;
			}
			server = entries[entry_index].address;
			port = entries[entry_index].port;
		} 
		else if(server == null) {
			server = Defaults.server;
		}
		else {
			String new_server = Defaults.properties.getProperty("server." + server);
			if(new_server != null) {
				server = new_server;	
			}
		}

		if(port == 0) {
			port = Defaults.port;
		}
	
		if(socket_number > 0) {
			if(!comm.connectToServer(server, socket_number)) {
				if(!Defaults.browser_mode) {
					System.exit(1);
				}
				return;
			}
		}
		else if(!comm.callServer(server, port)) {
			if(!Defaults.browser_mode) {
				System.exit(1);
			}
			return;
		}
		start();
	}

	/**
	* run
	*/
	public void run() {

		// find a slot
		while(data.me == null && !view.quit) {
			comm.readFromServerNoBlock();
		}

		// quit if the player decided to quit from the wait queue
		if(view.quit) {
			cleanUp();
			return;
		}
		
		boolean do_outfit = true;
		if(ghost_start) {
			for(int i = 0; i < Team.TEAMS.length; ++i) {
				if(Team.TEAMS[i].letter == data.me.mapchars.charAt(0)) {
					data.me.team = Team.TEAMS[i];
					break;
				}
			}			
			if(data.me.damage > data.me.ship.maxdamage) {
				data.me.status = Player.OUTFIT;
				view.gotoOutfit();
			}
			else {
				data.me.status = Player.ALIVE;
				do_outfit = false;
			}
		}

		// map all the windows
		view.slotFound();
		
		if(!ghost_start) {
			view.showLogin();
			// do login
			while(view.login != null && !view.quit) {
				comm.readFromServerNoBlock();
			}

			// quit if the player wanted to
			if(view.quit) {
				cleanUp();
				return;
			}
		}

		// show connection information
		System.out.println("***  socket " + comm.next_port + ", player " + data.me.no + "  ***");

		// show the ship and team pick windows
		if(do_outfit) {
			view.gotoOutfit();
			// player needs to pick a ship and team
			while(data.me.status == Player.OUTFIT && !view.quit) {
				view.entry.redraw();
				view.player_list.repaint();
				comm.readFromServer();
			}

			// quit if the player wanted to
			if(view.quit) {
				cleanUp();
				return;
			}
		}

		// try UDP if desired
		if(Defaults.try_udp) {
			comm.sendUdpReq(PacketTypes.COMM_UDP);
		}

		// try short packets if desired
		if(Defaults.try_short) {
			comm.sendShortReq(PacketTypes.SPK_VON);
		}

		// send the server options we have set
		comm.sendOptionsPacket();

		// start to ping the server if the user wants us to
		if(!Defaults.dont_ping) {
			comm.setPingOn(true);
		}

		// send the desired updates per second
		comm.sendUpdatePacket(Defaults.updates_per_second);

		// entry window
		while(true) {
			
			view.sound_player.playSound(SoundPlayer.ENTER_SHIP_SOUND);
			view.sound_player.playSound(SoundPlayer.ENGINE_SOUND);

			// play the game
			while(data.me.status != Player.OUTFIT) {
				comm.readFromServer();
				view.redraw();
			}

			if(view.quit) {
				cleanUp();
				return;
			}

			// player is dead, goto outfit
			view.gotoOutfit();

			// now is a good time to clean up resources
			System.gc();
			System.runFinalization();

			// player needs to pick a ship and team
			while(data.me.status == Player.OUTFIT && !view.quit) {
				view.entry.redraw();
				view.player_list.repaint();
				comm.readFromServer();
			}

			if(view.quit) {
				cleanUp();
				return;
			}
		}
	}

	void cleanUp() {
		comm.cleanUp();
		view.dispose();
		System.out.println("OK, Bye!");
		if(!Defaults.browser_mode) {
			System.exit(0);
		}
	}

	static void printUsage() {
		System.out.println(VERSION);
		System.out.println("Usage: java jtrek.Main [options]");
		System.out.println("Options:");
		System.out.println(" [-h servername]      Specify a server");
		System.out.println(" [-p port number]     Specify a port to connect to");
		System.out.println(" [-r defaults file]   Specify defaults file");
		System.out.println(" [-s socketnum]       Specify listen socket port for manual start");
		System.out.println(" [-G slotnum]         Try an emergency start");
		System.out.println(" [-u]   show usage");
		System.out.println(" [-A]   character password");
		System.out.println(" [-C]   character name");
		System.out.println(" [-U udp_port]       Specify client UDP port (useful for some firewalls)");
		//System.out.println(" [-o]   use old-style binary verification)\n");
		//System.out.println(" [-R]   use RSA binary verification\n");
		//System.out.println(" [-P]   Log server packets, repeat for increased information\n");
		//System.out.println(" [-c]   to just do ck_players on the server");
		//System.out.println(" [-f filename]   Record game into 'filename'");
		//System.out.println(" [-l filename]   Record messages into 'filename'");
		System.out.println(" [-m]   check metaserver for active servers");
		//System.out.println(" [-k]   display known servers\n");
		System.out.println(" [-v]   display client version info");
		System.out.println(" [-S]   display system properties");
		System.out.println(" [-B]   browser mode (don't use)");
	}

	public static void main(String[] args) {
		Main game = new Main(args);
	}

}
