package jtrek.comm;

import java.io.*;
import java.awt.Point;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.visual.*;
import jtrek.config.*;
import jtrek.message.*;
import jtrek.rsa.RSA;
import jtrek.util.*;

//abstract class ServerReader implements PacketTypes, MessageConstants  {
abstract class ServerReader implements PacketTypes, MessageConstants  {
	final static int SPWINSIDE = 500;

	final static int SHORT_WARNING = 33;
	final static int STEXTE_STRING = 34;

	// number of bits per byte
	final static int[] NUMOFBITS = {
		0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
		1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 
		1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
		1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
		3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 
		1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
		3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 
		3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 
		3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 
		4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8
	};

	/** sizes of variable torpbuffers */
	final static int VTSIZE[] = {
		4, 8, 8, 12, 12, 16, 20, 20, 24
	};

	/** 4 byte Header + torpdata */
	final static int VTISIZE[] = {
		4, 7, 9, 11, 13, 16, 18, 20, 22
	};

	final static int[] PACKET_SIZES = {
		 0,		// NULL
		84,		// SP_MESSAGE
		 4,		// SP_PLAYER_INFO
		 8,		// SP_KILLS
		12,		// SP_PLAYER
		 8,		// SP_TORP_INFO
		12,		// SP_TORP
		16,		// SP_PHASER
		 8,		// SP_PLASMA_INFO
		12,		// SP_PLASMA
		84,		// SP_WARNING
		84,		// SP_MOTD
		32,		// SP_YOU
		 4,		// SP_QUEUE
		28,		// SP_STATUS
		12,		// SP_PLANET
		 4,		// SP_PICKOK
	   104,		// SP_LOGIN
		 8,		// SP_FLAGS
		 4,		// SP_MASK
		 4,		// SP_PSTATUS
		 4,		// SP_BADVERSION
		 4,		// SP_HOSTILE
		56,		// SP_STATS
		52,		// SP_PL_LOGIN
		20,		// SP_RESERVED
		28,		// SP_PLANET_LOC
		 0,		// SP_SCAN
		 8,		// SP_UDP_REPLY
		 4,		// SP_SEQUENCE
		 4,		// SP_SC_SEQUENCE
		36,		// SP_RSA_KEY
		12,		// SP_MOTD_PIC
		 0,		// 33
		 0,		// 34
		 0,		// 35
		 0,		// 36
		 0,		// 37
		 0,		// 38
		60,		// SP_SHIP_CAP
		 8,		// SP_S_REPLY
		-1,		// SP_S_MESSAGE
		-1,		// SP_S_WARNING
		12,		// SP_S_YOU
		12,		// SP_S_YOU_SS
		-1,		// SP_S_PLAYER
		 8,		// SP_PING
		-1,		// SP_S_TORP
		-1,		// SP_S_TORP_INFO
		20,		// SP_S_8_TORP
		-1,		// SP_S_PLANET
		 0,		// 51
		 0,		// 52
		 0,		// 53
		 0,		// 54
		 0,		// 55
		 0,		// SP_S_SEQUENCE
		-1,		// SP_S_PHASER
		-1,		// SP_S_KILLS
		36,		// SP_S_STATS
		88,		// SP_FEATURE
		524		// SP_BITMAP
	};

	Communications comm;
	Universe data;
	NetrekFrame view;
	boolean motd_done = false;
	byte[] buffer;
	int count = 0;

	/** for rotation we need to keep track of our real coordinates */
	int my_x = 0;
	int my_y = 0;

	/** a short packet warning handler, it will be created as needed */
	ShortPacketWarningHandler swarning = null;

	public ServerReader(Communications comm, Universe data, NetrekFrame view) {
		this.view = view;
		this.data = data;
		this.comm = comm;
	}
	
	/** doRead */
	abstract void doRead() throws IOException;

	/** readFromServer */
	void readFromServer() throws IOException {
		doRead();
		while(count > 0)  {
			int ptype = buffer[0] & 0xFF;
			if(ptype < 1 || ptype > PacketTypes.SP_BITMAP) {
				System.err.println("Unknown packet type: " + ptype + ".  Flushing packet buffer & input stream.");
				System.err.println("Last packet type: " + ptype);
				continue;
			}
			int size = PACKET_SIZES[ptype];
			if (size == -1) {
				// variable buffer
				if(count < 4) {
					return;
				}
				switch(ptype) {
				case PacketTypes.SP_S_MESSAGE:
					size = buffer[4] & 0xFF;
					break;
				case PacketTypes.SP_S_WARNING:
					if(buffer[1] == STEXTE_STRING || buffer[1] == SHORT_WARNING) { 						
						size = buffer[3] & 0xFF;
					} 
					else {
						size = 4;	// Normal Packet
					}
					break;
				case PacketTypes.SP_S_PLAYER:
					if((buffer[1] & 0x80) != 0) { 
						// Small + extended Header
						size = (buffer[1] & 0x3F) * 4 + 4;							
					} 
					else if((buffer[1] & 0x40) != 0) {	
						// Small Header
						if (comm.short_version == PacketTypes.SHORTVERSION) {
							size = (buffer[1] & 0x3F) * 4 + 4 + (buffer[2] & 0xFF) * 4;
						}
						else {
							size = (buffer[1] & 0x3F) * 4 + 4;
						}
					} 
					else {  
						// Big Header
						size = (buffer[1] & 0xFF) * 4 + 12;
					}
					break;
				case PacketTypes.SP_S_TORP:
					size = VTSIZE[NUMOFBITS[buffer[1] & 0xFF]];
					break;
				case PacketTypes.SP_S_TORP_INFO :
					size = VTISIZE[NUMOFBITS[buffer[1] & 0xFF]] + NUMOFBITS[buffer[3] & 0xFF];
					break;
				case PacketTypes.SP_S_PLANET:
					size = (buffer[1] & 0xFF) * 6 + 2;
					break;
				case PacketTypes.SP_S_PHASER:	// S_P2
					switch(buffer[1] & 0x0F) {
					case Phaser.FREE :
					case Phaser.HIT :
					case Phaser.MISS :
					   size = 4;
					   break;
					case Phaser.HIT2 :
					   size = 8;
					   break;
					default:
					   size = 12;
					   break;
					}
					break;
				case PacketTypes.SP_S_KILLS :
					size = (buffer[1] & 0xFF) * 2 + 2;
					break;
				default:					
					System.err.println("Unknown variable buffer: " + ptype + ".  Flushing buffer buffer.");
					return;
				}
				if ((size % 4) != 0) {
					size += (4 - (size % 4));
				}
				if (size <= 0) {
					System.err.println("Bad short-buffer size value: " + size + ".  Flushing buffer buffer.");
					System.err.println("For packet type: " + ptype);
					return;
				}
			}
			if(count < size) {
				return;
			}
			handlePacket(ptype);
			count -= size;
			try {
				System.arraycopy(buffer, size, buffer, 0, count);
			}
			catch(ArrayIndexOutOfBoundsException e) {
				System.err.println("===================");
				System.err.println("Class=" + getClass().getName());
				System.err.println("ptype=" + ptype);
				System.err.println("size=" + size);
				System.err.println("buffer.length=" + buffer.length);
				System.err.println("count=" + count);
				e.printStackTrace();
			}
		}
	}

	/** handlePacket */
	void handlePacket(int ptype) {
		// call the appropriate buffer handler.
		switch(ptype) {
		case PacketTypes.SP_MESSAGE :
			handleMessage();
			break;
		case PacketTypes.SP_PLAYER_INFO :
			handlePlyrInfo();
			break;
		case PacketTypes.SP_KILLS :
			handleKills();
			break;
		case PacketTypes.SP_PLAYER :
			handlePlayer();
			break;
		case PacketTypes.SP_TORP_INFO :
			handleTorpInfo();
			break;
		case PacketTypes.SP_TORP :
			handleTorp();
			break;
		case PacketTypes.SP_PHASER :
			handlePhaser();
			break;
		case PacketTypes.SP_PLASMA_INFO :
			handlePlasmaInfo();
			break;
		case PacketTypes.SP_PLASMA :
			handlePlasma();
			break;
		case PacketTypes.SP_WARNING :
			handleWarning();
			break;
		case PacketTypes.SP_MOTD :
			handleMotd();
			break;
		case PacketTypes.SP_YOU :
			handleSelf();
			break;
		case PacketTypes.SP_QUEUE :
			handleQueue();
			break;
		case PacketTypes.SP_STATUS :
			handleStatus();
			break;
		case PacketTypes.SP_PLANET :
			handlePlanet();
			break;
		case PacketTypes.SP_PICKOK :
			handlePickok();
			break;
		case PacketTypes.SP_LOGIN :
			handleLogin();
			break;
		case PacketTypes.SP_FLAGS :
			handleFlags();
			break;
		case PacketTypes.SP_MASK :
			handleMask();
			break;
		case PacketTypes.SP_PSTATUS :
			handlePStatus();
			break;
		case PacketTypes.SP_BADVERSION :
			handleBadVersion();
			break;
		case PacketTypes.SP_HOSTILE :
			handleHostile();
			break;
		case PacketTypes.SP_STATS :
			handleStats();
			break;
		case PacketTypes.SP_PL_LOGIN :
			handlePlyrLogin();
			break;
		case PacketTypes.SP_RESERVED :
			handleReserved();
			break;
		case PacketTypes.SP_PLANET_LOC :
			handlePlanetLoc();
			break;
		case PacketTypes.SP_UDP_REPLY :
			handleUdpReply();
			break;
		case PacketTypes.SP_SEQUENCE :
		case PacketTypes.SP_SC_SEQUENCE :
			handleSequence();
			break;
		case PacketTypes.SP_RSA_KEY :
			handleRSAKey();
			break;
		case PacketTypes.SP_MOTD_PIC :
			handleMotdPic();
			break;
		case PacketTypes.SP_SHIP_CAP :
			handleShipCap();
			break;
		case PacketTypes.SP_S_REPLY :
			handleShortReply();
			break;
		case PacketTypes.SP_S_MESSAGE :
			handleSMessage();
			break;
		case PacketTypes.SP_S_WARNING :
			handleSWarning();
			break;
		case PacketTypes.SP_S_YOU :
			handleSelfShort();
			break;
		case PacketTypes.SP_S_YOU_SS :
			handleSelfShip();
			break;
		case PacketTypes.SP_S_PLAYER :
			handleVPlayer();
			break;
		case PacketTypes.SP_PING :
			handlePing();
			break;
		case PacketTypes.SP_S_TORP :
		case PacketTypes.SP_S_8_TORP :
			handleVTorp();
			break;
		case PacketTypes.SP_S_TORP_INFO :
			handleVTorpInfo();
			break;
		case PacketTypes.SP_S_PLANET :
			handleVPlanet();
			break;	
		case PacketTypes.SP_S_PHASER :
			handleVPhaser();
			break;
		case PacketTypes.SP_S_KILLS :
			handleVKills();
			break;
		case PacketTypes.SP_S_STATS :
			handle_s_Stats();
			break;
		case PacketTypes.SP_FEATURE :
			handleFeature();
			break;
		case PacketTypes.SP_BITMAP :
			handleBitmap();
			break;						
		}
	}

	/** intFromPacket */
	final int intFromPacket(int offset) {
		return (buffer[offset] & 0xFF) << 24 | (buffer[offset + 1] & 0xFF) << 16 
			| (buffer[offset + 2] & 0xFF) << 8 | buffer[offset + 3] & 0xFF;
	}

	/** shortFromPacket */
	final int shortFromPacket(int offset) {	
		return (buffer[offset] & 0xFF) << 8 | buffer[offset + 1] & 0xFF;
	}

	/** handleMessage */
	void handleMessage() {
		view.dmessage.newMessage(BufferUtils.extractString(buffer, 4, 80), buffer[1] & 0xFF, buffer[3] & 0xFF, buffer[2] & 0xFF);
	}

	void handlePlyrInfo() {
		try {
			Player player = data.players[buffer[1]];
			player.ship = Ship.SHIPS[buffer[2]];
			player.team = Team.TEAM_REMAP[buffer[3]];
			char[] mapchars = { player.team.letter, Player.PLAYER_LETTER[player.no] };
			player.mapchars = new String(mapchars);
			if(player.me) {
				view.newShip();
			}
			// $$$ LOTS MISSING FROM socket.c here
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePlyrInfo(): bad player number, ship index, or team index.");
			System.err.println("\tplayer number=" + buffer[1]);
			System.err.println("\tship index=" + buffer[2]);
			System.err.println("\tteam index=" + buffer[3]);
		}
	}

	void handleKills() {
		try {
			Player player = data.players[buffer[1]];
			player.kills = (float)intFromPacket(4) / 100f;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleKills(): bad player number");
			System.err.println("\tplayer number=" + buffer[1]);
		}
	}

	void handlePlayer() {
		try {
			Player player = data.players[buffer[1]];

			int x = intFromPacket(4);
			int y = intFromPacket(8);

			if(player.me) {
				my_x = x;
				my_y = y;
			}

			// Rotate direction and coordinates
			player.dir = Rotate.rotateDir(buffer[2] & 0xFF, data.rotate);
			Point point = Rotate.rotateCoord(x, y, data.rotate,
			  Universe.HALF_PIXEL_SIZE, Universe.HALF_PIXEL_SIZE);

			player.x = point.x;
			player.y = point.y;

			player.speed = buffer[3] & 0xFF;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePlayer(): bad player number");
			System.err.println("\tplayer number=" + buffer[1]);
		}
	}
						
	void handleTorpInfo() {
		try {
			Torp torp = data.torps[shortFromPacket(4)];
			int status = buffer[2];
			if(status != torp.status) {
				if(status == Torp.EXPLODE && torp.status == Torp.FREE) {
					return;
				}
				if(status != Torp.FREE && torp.status == Torp.FREE) {
					torp.owner.ntorp++;
				}
				if(status == Torp.FREE && torp.status != Torp.FREE) {
					torp.owner.ntorp--;
				}				
				if(status == Torp.EXPLODE) {
					torp.fuse = view.local.getTorpCloudFrames();
				}
				torp.status = status;
			}
			torp.war = buffer[1];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleTorpInfo(): bad torp index");
			System.err.println("\ttorp index=" + shortFromPacket(4));
		}
	}

	void handleTorp() {
		try {
			Torp torp = data.torps[shortFromPacket(2)];

			// Rotate direction and coordinates
			torp.dir = Rotate.rotateDir(buffer[1] & 0xFF, data.rotate);
			Point point = Rotate.rotateCoord(intFromPacket(4), intFromPacket(8),
			  data.rotate, Universe.HALF_PIXEL_SIZE, Universe.HALF_PIXEL_SIZE);

			torp.x = point.x;
			torp.y = point.y;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleTorp(): bad torp index");
			System.err.println("\ttorp index=" + shortFromPacket(2));
		}
	}

	void handlePhaser() {
		try {
			Phaser phaser = data.phasers[buffer[1]];

			phaser.target = data.players[intFromPacket(12)];
			phaser.status = buffer[2];

			if(phaser.status != Phaser.FREE) {
				// normalized maxfuse
				phaser.maxfuse = (data.players[buffer[1]].ship.phaserfuse * comm.updates_per_second) / 10;
			}

			// Rotate coordinates
			phaser.dir = Rotate.rotateDir(buffer[3] & 0xFF, data.rotate);
			Point point = Rotate.rotateCoord(intFromPacket(4), intFromPacket(8),
			  data.rotate, Universe.HALF_PIXEL_SIZE, Universe.HALF_PIXEL_SIZE);

			phaser.x = point.x;
			phaser.y = point.y;

			phaser.fuse = 0;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePhaser(): bad phaser index or target player number");
			System.err.println("\tphaser index=" + buffer[1]);
			System.err.println("\ttarget player number=" + intFromPacket(12));
		}
	}

	void handlePlasmaInfo() {
		try {
			Plasma plasma = data.plasmas[shortFromPacket(4)];
			int status = buffer[2];
			if(status == Plasma.EXPLODE && plasma.status == Plasma.FREE) {
				return;
			}
			if(status != Plasma.FREE && plasma.status == Plasma.FREE) {
				plasma.owner.nplasmatorp++;
			}
			else if(status == Plasma.FREE && plasma.status != Plasma.FREE) {
				plasma.owner.nplasmatorp--;
			}
			plasma.war = buffer[1];
			plasma.status = status;
			if(status == Plasma.EXPLODE) {
				plasma.fuse = view.local.getPlasmaCloudFrames();
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePlasmaInfo(): bad plasma index or plasma owner index");
			System.err.println("\tplasma index=" + buffer[1]);
			System.err.println("\tplasma owner index not known because it uses plasma index");
		}		
	}
	
	void handlePlasma() {
		try {		
			Plasma plasma = data.plasmas[shortFromPacket(2)];

			// Rotate coordinates
			Point point = Rotate.rotateCoord(intFromPacket(4), intFromPacket(8),
			  data.rotate, Universe.HALF_PIXEL_SIZE, Universe.HALF_PIXEL_SIZE);

			plasma.x = point.x;
			plasma.y = point.y;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePlasma(): bad plasma index");
			System.err.println("\tplasma index=" + shortFromPacket(2));
		}
	}
	
	void handleWarning() {
		view.warning.setWarning(BufferUtils.extractString(buffer, 4, 80));
	}
	
	void handleMotd() {
		String line = BufferUtils.extractString(buffer, 4, 80);
		if(line.startsWith("\t@@@")) {
			motd_done = true;
		}
		else if(!motd_done) {
			view.addMotdLine(line);
		}
		else {
			view.server_info.addLine(line);
		}
	}
	
	/** handleSelf */
	void handleSelf() {
		try {
			int slot = (comm.ghost_slot == -1) ? buffer[1] : comm.ghost_slot;
			data.me = data.players[slot];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleSelf(): bad player number");	
			System.err.println("\tplayer number=" + buffer[1]);
			return;
		}
		data.me.hostile = buffer[2];
		data.me.swar = buffer[3];
		data.me.armies = buffer[4];
		data.me.flags = intFromPacket(8);
		data.me.damage = intFromPacket(12);
		data.me.shield = intFromPacket(16);
		data.me.fuel = intFromPacket(20);
		data.me.etemp = (short)shortFromPacket(24);
		data.me.wtemp = (short)shortFromPacket(26);
		data.me.whydead = (short)shortFromPacket(28);
		data.me.whodead = (short)shortFromPacket(30);
		data.me.me = true;

		if((data.me.flags & Player.PLOCK) != 0) {
			data.status.observer = ((data.me.flags & Player.OBSERV) != 0);
		}
		else {	
			data.status.observer = false;
		}
	}
	
	/** handleQueue */
	void handleQueue() {
		view.waitQueue(shortFromPacket(2));	
	}
	
	/** handleStatus */
	void handleStatus() {
		data.status.tourn = (buffer[1] != 0);
		data.status.armsbomb = intFromPacket(4);
		data.status.planets = intFromPacket(8);
		data.status.kills = intFromPacket(12);	
		data.status.losses = intFromPacket(16);
		data.status.time = intFromPacket(20);
		data.status.timeprod = intFromPacket(24);
	}
	
	/** handlePlanet */
	void handlePlanet() {
		try {
			Planet planet = data.planets[buffer[1]];
			planet.owner = Team.TEAM_REMAP[buffer[2]];
			planet.info = buffer[3];			
			planet.flags = shortFromPacket(4);
			planet.armies = intFromPacket(8);
			planet.flags |= Planet.REDRAW;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePlanet(): bad planet number");
			System.err.println("\tplanet number=" + buffer[1]);
		}
	}
	
	void handlePickok() {
		view.pickOK((buffer[1] != 0));
	}

	/** handleLogin */
	void handleLogin() {
		// check to see if this is a paradise server
		if(buffer[2] == 69 && buffer[3] == 42) {
      		System.err.println("Sorry, this client is incompatible with Paradise servers.");
      		System.err.println("You need to get a Paradise client (see rec.games.netrek FAQ)");			
			// inform the server that we are leaving
      		view.quit(0);
			return;
   		}
		boolean accept = (buffer[1] != 0);
		view.login.accept(accept);
		if(accept) {
			data.me.stats.flags = intFromPacket(4);
			/* WE STORE ALL OF THIS LOCALLY
			Defaults.name_mode = (flags / Stats.MAPMODE) & 0x01;
			Defaults.show_local = (flags / Stats.SHOWLOCAL) & 0x03;
			Defaults.show_galactic = (flags / Stats.SHOWGLOBAL) & 0x03;
			// We don't need the keymap, it is stored in the resource file
			String keymap = BufferUtils.extractString(buffer, 8, 96);
			*/
		}
	}

	void handleFlags() {
		try {
			Player player = data.players[buffer[1]];
			player.flags = intFromPacket(4);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleFlags(): bad player number");
			System.err.println("\tplayer number=" + buffer[1]);
		}
	}
	
	void handleMask() {
		view.entry.setTeamMask(buffer[1]);
	}

	void handlePStatus() {		
		try {
			Player player = data.players[buffer[1]];
			int status = buffer[2];
			if(player.status != status) {
				player.status = status;
				switch(status) {
				case Player.EXPLODE :
					player.explode = 0;
					break;
				case Player.DEAD :
					player.status = Player.EXPLODE;
					break;
	 			default :	    		
	    			player.kills = 0f;
	 			}
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePStatus(): bad player number");
			System.err.println("\tplayer number=" + buffer[1]);
		}
	}
	
	void handleBadVersion() {
		System.out.println("handleBadVersion not implemented");
	}
	
	void handleHostile() {
		try {
			Player player = data.players[buffer[1]];
			player.swar = buffer[2];
			player.hostile = buffer[3];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleHostile(): bad player number");
			System.err.println("\tplayer number=" + buffer[1]);
		}
	}
	
	void handleStats() {
		try {
			Stats stats = data.players[buffer[1]].stats;
			stats.tkills = intFromPacket(4);
			stats.tlosses = intFromPacket(8);
			stats.kills = intFromPacket(12);
			stats.losses = intFromPacket(16);
			stats.tticks = intFromPacket(20);
			stats.tplanets = intFromPacket(24);
			stats.tarmsbomb = intFromPacket(28);
			stats.sbkills = intFromPacket(32);
			stats.sblosses = intFromPacket(36);
			stats.armsbomb = intFromPacket(40);
			stats.planets = intFromPacket(44);
			if(data.players[buffer[1]].ship.type == Ship.SB && view.feature_list.features[FeatureList.SB_HOURS].value != 0) {
				stats.sbticks = intFromPacket(48);
			}
			else {
				stats.maxkills = (double)intFromPacket(48) / 100d;
			}
			stats.sbmaxkills = (double)intFromPacket(52) / 100d;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleStats(): bad player number");
			System.err.println("\tplayer number=" + buffer[1]);
		}
	}

	void handlePlyrLogin() {
		try {
			Player player = data.players[buffer[1]];
			Rank newrank = Rank.RANKS[buffer[2]];
			if(player.me && player.stats.rank != Rank.RANKS[0] && newrank != player.stats.rank) {
				data.status.promoted = true;
			}
			player.stats.rank = newrank;
			player.name = BufferUtils.extractString(buffer, 4, 16);
			player.monitor = BufferUtils.extractString(buffer, 20, 16);
			player.login = BufferUtils.extractString(buffer, 36, 16);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePlyrLogin(): bad player number or bad rank index");
			System.err.println("\tplayer number=" + buffer[1]);
			System.err.println("\trank index=" + buffer[2]);
		}
	}

	void handleReserved() {
		//We ignore reserved packets.  its related to RSA.  we get it from pickled - Darrell
		/*
		byte[] response;
		byte[] sreserved = new byte[16];
		System.arraycopy(buffer, 4, sreserved, 0, 16);	
		if(Defaults.use_rsa) { 
			// can use -o option for old blessing
			// client sends back a 'reserved' packet just saying RSA_VERSION
			// info theoretically, the server then sends off a rsa_key_spacket
			// for the client to then respond to

			view.warning.setWarning(RSA.VERSION);
			response = BufferUtils.getBytes(RSA.VERSION);
			System.out.println("Sending RSA reserved response");
		}
		else {
			response = Reserved.encryptReservedPacket(sreserved, comm.server, data.me.no);
		}
		comm.sendReservedResponse(sreserved, response);*/
	}

	void handlePlanetLoc() {
		try {
			Planet planet = data.planets[buffer[1]];

			// Rotate coordinates
			Point point = Rotate.rotateCoord(
						intFromPacket(4),
						intFromPacket(8),
						data.rotate,
						Universe.HALF_PIXEL_SIZE,
						Universe.HALF_PIXEL_SIZE);

			planet.x = point.x;
			planet.y = point.y;

			planet.name = BufferUtils.extractString(buffer, 12, 16);
			view.galactic.redraw_all_planets = true;

			// don't need this flag anymore
			//planet.flags |= Planet.REDRAW;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handlePlanetLoc(): bad planet number");
			System.err.println("\tplanet number=" + buffer[1]);
		}
	}
	
	void handleUdpReply() {
		if(Communications.UDP_DIAG)
			System.out.println("UDP: Received UDP reply " + buffer[1]);

		switch (buffer[1] & 0xFF) {
			case PacketTypes.SWITCH_TCP_OK:
				if (comm.comm_mode == PacketTypes.COMM_TCP) {
					if(Communications.UDP_DIAG) 
						System.out.println("UDP: Got SWITCH_TCP_OK while in TCP mode; ignoring");
				}
				else {
					comm.comm_mode = PacketTypes.COMM_TCP;
					comm.comm_status = PacketTypes.STAT_CONNECTED;
					view.warning.setWarning("Switched to TCP-only connection");
					comm.closeUdpConn();
					if(Communications.UDP_DIAG) 
						System.out.println("UDP: UDP port closed");
					if (view.udp_win.isVisible()) {
						view.udp_win.refresh();
					}
				}
				break;
			case PacketTypes.SWITCH_UDP_OK:
				if (comm.comm_mode == PacketTypes.COMM_UDP) {
					if(Communications.UDP_DIAG) 
						System.out.println("UDP: Got SWITCH_UDP_OK while in UDP mode; ignoring");
				}
				else {
					// the server is forcing UDP down our throat?
					if (comm.comm_mode_req != PacketTypes.COMM_UDP) {
						if(Communications.UDP_DIAG) 
							System.out.println("UDP: Got unsolicited SWITCH_UDP_OK; ignoring");
					}
					else {
						if(Communications.UDP_DIAG) 
							System.out.println("UDP: Connected to server's UDP port");

						comm.comm_status = PacketTypes.STAT_VERIFY_UDP;
						if (view.udp_win.isVisible()) {
							view.udp_win.refresh();
						}
						comm.connUdpConn(intFromPacket(4));
						comm.sendUdpVerify();
					}
				}
				break;
			case PacketTypes.SWITCH_DENIED:     
				if (intFromPacket(4) == 0) {
					if(Communications.UDP_DIAG) 
						System.out.println("UDP: Switch to UDP failed (different version)");
					view.warning.setWarning("UDP protocol request failed (bad version)");
				}
				else {
					if(Communications.UDP_DIAG) 
						System.out.println("UDP: Switch to UDP denied");
					view.warning.setWarning("UDP protocol request denied");
				}
				comm.comm_mode_req = comm.comm_mode;
				comm.comm_status = PacketTypes.STAT_CONNECTED;
				if (view.udp_win.isVisible()) {
					view.udp_win.refresh();
				}
				comm.closeUdpConn();
				break;
			case PacketTypes.SWITCH_VERIFY:
				// This is here because I noticed player stats weren't being 
				// updated unless I sent this request manually
				comm.sendUdpReq(PacketTypes.COMM_UPDATE);
				if(Communications.UDP_DIAG) 
					System.out.println("UDP: Received UDP verification");
				break;
			default:
				System.err.println("netrek: Got funny reply (" + buffer[1] + ") in UDP_REPLY packet");
				break;
		}
	}
	
	void handleSequence() {
	}
	
	void handleRSAKey() {
		System.out.println("RSA verification requested.");
		byte[] data = new byte[RSA.KEY_SIZE];
		System.arraycopy(buffer, 4, data, 0, data.length);
		comm.sendRSAResponse(data);
	}
	
	void handleMotdPic() {
		System.out.println("handleMotdPic not implemented");
	}
	
	void handleShipCap() {
		Ship ship = Ship.SHIPS[shortFromPacket(2)];
		ship.torpspeed = shortFromPacket(4);
		ship.phaserdamage = shortFromPacket(6);
		ship.maxspeed = intFromPacket(8);
		ship.maxfuel = intFromPacket(12);
		ship.maxshield = intFromPacket(16);
		ship.maxdamage = intFromPacket(20);
		ship.maxwpntemp = intFromPacket(24);
		ship.maxegntemp = intFromPacket(28);
		ship.width = shortFromPacket(30);
		ship.height = shortFromPacket(32);
		ship.maxarmies = shortFromPacket(34);
	}

	void handleShortReply() {
		int reply = buffer[1];
		switch(reply) {
		case PacketTypes.SPK_VOFF :
			if(comm.short_version == PacketTypes.SHORTVERSION && !comm.receive_short) {
				System.out.println("Using Short Packet Version 1.");
				comm.short_version = PacketTypes.OLDSHORTVERSION;
				comm.sendShortReq(PacketTypes.SPK_VON);
			}
			else {
				comm.receive_short = false;
				comm.sendUdpReq(PacketTypes.COMM_UPDATE);
				view.short_win.refresh();
			}
			break;
		case PacketTypes.SPK_VON:
			comm.receive_short = true;
			view.short_win.refresh();
			// I didn't know what spwinside and spgwidth where used for.
			//int spwinside = shortFromPacket(2);
			//int spgwidth = shortFromPacket(4);
			System.out.println("Receiving Short Packet Version " + comm.short_version);
			break;
		case PacketTypes.SPK_THRESHOLD:
			break;
		default:
			System.err.println("Unknown response packet value short-req: " + reply);
		}
	}
	
	/** handleSMessage */
	void handleSMessage() {
		char[] address = "   ->   ".toCharArray();

		int from = buffer[3] & 0xFF;
		if(from > data.players.length) {
			from = 255;
		}
		if(from == 255) {
			"GOD".getChars(0, 3, address, 0);
		}
		else {
			data.players[from].mapchars.getChars(0, 2, address, 1);
		}

		switch(buffer[1] & (TEAM | INDIV | ALL)) {
		case TEAM :
			data.me.team.abbrev.getChars(0, 3, address, 5);
			break;
		case INDIV :
			// I know that it's me -> xxx but i copied it straight ...
			data.me.mapchars.getChars(0, 2, address, 5);
			break;
		default :
			"ALL".getChars(0, 3, address, 5);
			break;
		}

		String message = new String(address) + BufferUtils.extractString(buffer, 5, 79);
		view.dmessage.newMessage(message, buffer[1] & 0xFF, buffer[3] & 0xFF, buffer[2] & 0xFF);
	}

	/** handleSWarning */
	void handleSWarning() {
		if(swarning == null) {
			swarning = new ShortPacketWarningHandler(view, data, buffer);
		}
		swarning.handleSWarning();
	}	
	
	/** handleSelfShort */
	void handleSelfShort() {
		Player me = null;
		int slot = (comm.ghost_slot == -1) ? buffer[1] : comm.ghost_slot;
		try {
			me = data.players[slot];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleSelf(): bad player number");	
			System.err.println("\tplayer number=" + slot);
			return;
		}
		data.me = me;
		me.hostile = buffer[2];
		me.swar = buffer[3];
		me.armies = buffer[4];
		me.whydead = buffer[5];
		me.whodead = buffer[6];
		me.flags = intFromPacket(8);

		if((me.flags & Player.PLOCK) != 0) {
			data.status.observer = ((me.flags & Player.OBSERV) != 0);
		}
		else {	
			data.status.observer = false;
		}
	}
	
	/** handleSelfShip */
	void handleSelfShip() {
		Player me = data.me;

		// Ignore this message until I know what player slot I am.
		if(me == null) {
			return;
		}

		me.damage = shortFromPacket(2);
		me.shield = shortFromPacket(4);
		me.fuel = shortFromPacket(6);
		me.etemp = (short)shortFromPacket(8);
		me.wtemp = (short)shortFromPacket(10);

		if(view.feature_list.features[FeatureList.SELF_8FLAGS].value != Feature.OFF) {
			me.flags = (me.flags & 0xFFFFFF00) | (buffer[1] & 0xFF);
		}
		else if(view.feature_list.features[FeatureList.SELF_8FLAGS2].value != Feature.OFF) {
			int new_flags = me.flags & ~(Player.SHIELD | Player.REPAIR | Player.CLOAK 
			  | Player.GREEN | Player.YELLOW | Player.RED | Player.TRACT | Player.PRESS);

			int pad1 = buffer[1] & 0xFF;
			
			new_flags |= ((pad1 & Player.SHIELD) | (pad1 & Player.REPAIR) 
			  | ((pad1 & (Player.CLOAK << 2)) >> 2) | ((pad1 & (Player.GREEN << 7)) >> 7) 
			  | ((pad1 & (Player.YELLOW << 7)) >> 7) | ((pad1 & (Player.RED << 7)) >> 7) 
			  | ((pad1 & (Player.TRACT << 15)) >> 15)  | ((pad1 & (Player.PRESS << 15)) >> 15));

			me.flags = new_flags;
		}

	}

	/** newFlags */
	void newFlags(int dat, int which) {
		int status;
		int new_flags_set;
		Player player;
		for(int pnum = which * 16; pnum < (which + 1) * 16 && pnum < data.players.length; ++pnum) {

			new_flags_set = dat & 0x03;
			dat >>>= 2;

			player = data.players[pnum];
			if(player.status == Player.FREE) {
				continue;
			}

			switch(new_flags_set) {
			case 0 :	// DEAD/EXPLODE
				status = Player.EXPLODE;
				player.flags &= ~Player.CLOAK;
				break;
			case 1 :	// ALIVE & CLOAK
				status = Player.ALIVE;
				player.flags |= Player.CLOAK;
				break;
			case 2 :	// ALIVE & SHIELD
				status = Player.ALIVE;
				player.flags |= Player.SHIELD;
				player.flags &= ~Player.CLOAK;
				break;
			case 3 :	// ALIVE & NO shields
				status = Player.ALIVE;
				player.flags &= ~(Player.SHIELD | Player.CLOAK);
				break;
			default:
				status = 0;
				break;
			}

			if (player.status == status) {
				continue;
			}

			if (status == Player.EXPLODE) {
				if (player.status == Player.ALIVE) {
					player.explode = 0;
					player.status = status;
				}
			} 
			else {
				if(player.me) {
					// Wait for Player.OUTFIT
					if (player.status == Player.OUTFIT || player.status == Player.FREE) {
						player.status = Player.ALIVE;
					}
				}
				else {
					player.status = status;				
				}
			}
		}
	}
	
	/** handleVPlayer */
	void handleVPlayer() {
		int x, y, player_index, save;
		Player player;
		int numofplayers = buffer[1] & 0x3F;

		int pos = 0;

		// $$$ CORRUPTED_PACKETS
		// should do something clever here - jmn if(pl_no < 0 || pl_no >= MAXPLAYER){ return; }

		// $$ MAXPLAYER > 32 WAS HERE
		if ((buffer[1] & 0x40) != 0) {								// Short Header
			if (comm.short_version == PacketTypes.SHORTVERSION) {				// flags S_P2
				if (buffer[2] == 2) {
					newFlags(intFromPacket(4), buffer[3] & 0xFF);
					newFlags(intFromPacket(8), 0);
					pos += 8;
				}
				else if (buffer[2] == 1) {
					newFlags(intFromPacket(4), buffer[3] & 0xFF);
					pos += 4;
				}
			}
			pos += 4;
			for(int p = 0; p < numofplayers; p++) {
				player_index = buffer[pos] & 0x1F;
				if(player_index >= data.players.length) {
					continue;
				}
				save = buffer[pos];
				pos++;
				player = data.players[player_index];

				// SPEED
				player.speed = buffer[pos] & 0x0F;
				if(player.me && view.feature_list.features[FeatureList.CLOAK_MAXWARP].value != Feature.OFF) {
					if(player.speed == 0xF) {
						player.flags |= Player.CLOAK;
					}
					else if((player.flags & Player.CLOAK) != 0) {
						player.flags &= ~Player.CLOAK;
					}
				}

				// realDIR
				player.dir = ((buffer[pos] & 0xFF) >>> 4) * 16;
				pos++;
				x = buffer[pos] & 0xFF;
				pos++;
				y = buffer[pos] & 0xFF;
				pos++;

				// The lower 8 Bits are saved
				// Now we must preprocess the coordinates
				if((save & 0x40) != 0) {
					x |= 0x100;
				}
				if((save & 0x80) != 0) {
					y |= 0x100;
				}
				// Now test if it's galactic or local coord
				if((save & 0x20) != 0) { 
					// It's galactic
					if (x == 501 || y == 501) {
						x = -500;
						y = -500;
					}
					player.x = x * (Universe.PIXEL_SIZE / SPWINSIDE);
					player.y = y * (Universe.PIXEL_SIZE / SPWINSIDE);
				}
				else { 
					// Local
					player.x = my_x + ((x - SPWINSIDE / 2) * Universe.SCALE);
					player.y = my_y + ((y - SPWINSIDE / 2) * Universe.SCALE);
				}

				Point point = Rotate.rotateCoord(
							player.x,
							player.y,
							data.rotate,
							Universe.HALF_PIXEL_SIZE,
							Universe.HALF_PIXEL_SIZE);
				
				player.x = point.x;
				player.y = point.y;

				player.dir = Rotate.rotateDir(player.dir, data.rotate);
			}
		}
		else { // Big Packet

			player = data.me;
			player.dir = buffer[2] & 0xFF;
			player.speed = buffer[3] & 0xFF;

			if(view.feature_list.features[FeatureList.CLOAK_MAXWARP].value != Feature.OFF) {
				if(player.speed == 0xF) {
					player.flags |= Player.CLOAK;
				}
				else if((player.flags & Player.CLOAK) != 0) {
					player.flags &= ~Player.CLOAK;
				}
			}

			if (comm.short_version == PacketTypes.SHORTVERSION) {	// S_P2
				player.x = Universe.SCALE * shortFromPacket(4);
				player.y = Universe.SCALE * shortFromPacket(6);
				newFlags(intFromPacket(8), 0);
			}
			else {	// OLDSHORTVERSION
				player.x = intFromPacket(4);
				player.y = intFromPacket(8);
			}

			my_x = player.x;
			my_y = player.y;

			Point point = Rotate.rotateCoord(
						player.x,
						player.y,
						data.rotate,
						Universe.HALF_PIXEL_SIZE,
						Universe.HALF_PIXEL_SIZE);
				
			player.x = point.x;
			player.y = point.y;

			player.dir = Rotate.rotateDir(player.dir, data.rotate);

			if (buffer[1] == 0) {
				return;
			}
			pos += 12;

			// Now the small packets
			for(int p = 0; p < numofplayers; ++p) {
				player_index = buffer[pos] & 0x1F;
				if (player_index >= data.players.length) {
					continue;
				}
				save = buffer[pos];
				pos++;
				player = data.players[player_index];
			
				// SPEED
				player.speed = buffer[pos] & 0x0F;
				if(player.me && view.feature_list.features[FeatureList.CLOAK_MAXWARP].value != 0) {
					if(player.speed == 0xF) {
						player.flags |= Player.CLOAK;
					}
					else if((player.flags & Player.CLOAK) != 0) {
						player.flags &= ~Player.CLOAK;
					}
				}

				// realDIR
				player.dir = ((buffer[pos] & 0xFF) >>> 4) * 16;
				pos++;
				x = buffer[pos] & 0xFF;
				pos++;
				y = buffer[pos] & 0xFF;
				pos++;

				// The lower 8 Bits are saved
				// Now we must preprocess the coordinates
				if((save & 0x40) != 0) {
					x |= 0x100;
				}
				if((save & 0x80) != 0) {
					y |= 0x100;
				}
				// Now test if it's galactic or local coord
				if((save & 0x20) != 0) { 
					// It's galactic
					if (x == 501 || y == 501) {
						x = -500;
						y = -500;
					}
					player.x = x * (Universe.PIXEL_SIZE / SPWINSIDE);
					player.y = y * (Universe.PIXEL_SIZE / SPWINSIDE);
				}
				else { 
					// Local
					player.x = my_x + (x - SPWINSIDE / 2) * Universe.SCALE;
					player.y = my_y + (y - SPWINSIDE / 2) * Universe.SCALE;
				}

				point = Rotate.rotateCoord(
							player.x,
							player.y,
							data.rotate,
							Universe.HALF_PIXEL_SIZE,
							Universe.HALF_PIXEL_SIZE);
				
				player.x = point.x;
				player.y = point.y;

				player.dir = Rotate.rotateDir(player.dir, data.rotate);
			} 
		}
	}
	
	/** handlePing */
	void handlePing() {
		comm.ping = true;			// we got a ping
		comm.sendServerPingResponse(buffer[1]);
		comm.ping_stats.lag = (short)shortFromPacket(2);
		comm.ping_stats.tloss_sc = buffer[4] & 0xFF;
		comm.ping_stats.tloss_cs = buffer[5] & 0xFF;
		comm.ping_stats.iloss_sc = buffer[6] & 0xFF;
		comm.ping_stats.iloss_cs = buffer[7] & 0xFF;
		comm.ping_stats.calculateLag();
		view.ping_panel.repaint();
	}

	/** handleVTorp */
	void handleVTorp() {
		int torp_index = 0;
		try {
			int bitset;						// bit=1 that torp is in packet
			int dx;
			int dy;
			int shiftvar;
			int torp_data_pos;

			if(buffer[0] == PacketTypes.SP_S_8_TORP) { // MAX packet
				bitset = 0xFF;
				torp_index = (buffer[1] & 0xFF) * 8;
				torp_data_pos = 2;
			}       
			else { // Normal packet
				bitset = buffer[1];
				torp_index = (buffer[2] & 0xFF) * 8;
				torp_data_pos = 3;
			}
			
			int shift = 0;
			Torp torp;

			for(int t = 0; t < 8; ++t, ++torp_index, bitset >>= 1) {
				torp = data.torps[torp_index];

				if((bitset & 0x01) != 0) {
					// extract the x coordinates from the bits
					dx = ((buffer[torp_data_pos] & 0xFF) >>> shift);
					shiftvar = (buffer[++torp_data_pos] & 0xFF) << (8 - shift);
					dx |= (shiftvar & 0x1FF);
					shift++;

					// extract the y coordinates from the bits
					dy = ((buffer[torp_data_pos] & 0xFF) >>> shift);
					shiftvar = (buffer[++torp_data_pos] & 0xFF) << (8 - shift);
					dy |= (shiftvar & 0x1FF);
					shift++;

					if (shift == 8) {
						shift = 0;
						++torp_data_pos;
					}

					// This is necessary because Torp.FREE/Torp.MOVE is now encoded in the bitset
					if (torp.status == Torp.FREE) {
						// guess
						torp.status = Torp.MOVE;
						torp.owner.ntorp++;
					}
					else if (torp.owner.me && torp.status == Torp.EXPLODE) {
						torp.status = Torp.MOVE;
					}

					// Check if torp is visible
					if (dx > SPWINSIDE || dy > SPWINSIDE) { // Not visible
						torp.x = torp.y = -100000;
					}
					else { // visible
						// Rotate coordinates
						Point point = Rotate.rotateCoord(
									my_x + ((dx - SPWINSIDE / 2) * Universe.SCALE),
									my_y + ((dy - SPWINSIDE / 2) * Universe.SCALE),
									data.rotate,
									Universe.HALF_PIXEL_SIZE,
									Universe.HALF_PIXEL_SIZE);

						torp.x = point.x;
						torp.y = point.y;
					}
				}
				else { // We got a Torp.FREE
					if(torp.status != Torp.FREE && torp.status != Torp.EXPLODE) {
						torp.owner.ntorp--;
						torp.status = Torp.FREE;
					}
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleVTorp(): bad torp index");
			System.err.println("\ttorp index=" + torp_index);
		}
	}
	
	/** handleVTorpInfo */
	void handleVTorpInfo() {
		int torp_index = 0;
		try {
			int dx;
			int dy;
			int shiftvar;
			int status;
			byte war;

			int bitset = buffer[1] & 0xFF;
			torp_index = (buffer[2] & 0xFF) * 8;
			int infobitset = buffer[3] & 0xFF;

			int torp_data_pos = 4;
			int info_data_pos = VTISIZE[NUMOFBITS[bitset]];

			int shift = 0;	// How many torps are extracted (for shifting)

			Torp torp;

			for(int t = 0; t < 8; ++t, ++torp_index, bitset >>= 1, infobitset >>= 1) {
				torp = data.torps[torp_index];

				if((bitset & 0x01) != 0) {
					// extract the x coordinates from the bits
					dx = ((buffer[torp_data_pos] & 0xFF) >>> shift);
					shiftvar = (buffer[++torp_data_pos] & 0xFF) << (8 - shift);
					dx |= (shiftvar & 0x1FF);
					shift++;

					// extract the y coordinates from the bits
					dy = ((buffer[torp_data_pos] & 0xFF) >>> shift);
					shiftvar = (buffer[++torp_data_pos] & 0xFF) << (8 - shift);
					dy |= (shiftvar & 0x1FF);
					shift++;

					if (shift == 8) {
						shift = 0;
						++torp_data_pos;
					}

					// Check for torp with no TorpInfo ( In case we missed a n updateAll)
					if ((infobitset & 0x01) == 0) {
						if(torp.status == Torp.FREE) {
							// guess
							torp.status = Torp.MOVE;
							torp.owner.ntorp++;
						} else if (torp.owner.me && torp.status == Torp.EXPLODE) {
							torp.status = Torp.MOVE;
						}
					}
					// Check if torp is visible
					if (dx > SPWINSIDE || dy > SPWINSIDE) { // Not visible
						torp.x = torp.y = -100000;
					}
					else { // visible
						Point point = Rotate.rotateCoord(
									my_x + ((dx - SPWINSIDE / 2) * Universe.SCALE),
									my_y + ((dy - SPWINSIDE / 2) * Universe.SCALE),
									data.rotate,
									Universe.HALF_PIXEL_SIZE,
									Universe.HALF_PIXEL_SIZE);
				
						torp.x = point.x;
						torp.y = point.y;
					}
				}
				else { // Got a TFREE ?
					if ((infobitset & 0x01) == 0) {	
						// No other TorpInfo for this Torp
						if(torp.status != Torp.FREE && torp.status != Torp.EXPLODE) {
							torp.owner.ntorp--;
							torp.status = Torp.FREE;
						}
					}
				}
				// Now the TorpInfo
				if((infobitset & 0x01) != 0) {
					war = (byte)(buffer[info_data_pos] & 0xF0);
					status = (buffer[info_data_pos] & 0xF0) >>> 4;
					info_data_pos++;

					if (status == Torp.EXPLODE && torp.status == Torp.FREE) {
						// FAT: redundant explosion; don't update p_ntorp
						continue;
					}
					if (torp.status == Torp.FREE && status != Torp.FREE) {
						torp.owner.ntorp++;
					}
					if (torp.status != Torp.FREE && status == Torp.FREE) {
						torp.owner.ntorp--;
					}
					torp.war = war;
					if(status != torp.status) {
						// FAT: prevent explosion reset
						torp.status = status;
						if (torp.status == Torp.EXPLODE) {
							torp.fuse = view.local.getTorpCloudFrames();
						}
					}
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleVTorpInfo(): bad torp index");
			System.err.println("\ttorp index=" + torp_index);
		}
	}
	
	void handleVPlanet() {
		int planet_index = 0;
		try {
			int numofplanets = buffer[1] & 0xFF;
			int owner;
			int info;
			int flags;
			int armies;

			boolean redraw;

			Planet planet;
			for(int pos = 2, p = 0; p < numofplanets; ++p, pos += 2) {
				planet_index = buffer[pos] & 0xFF;
				planet = data.planets[planet_index];
				redraw = false;

				// get the owner
				owner = buffer[++pos] & 0xFF;
				if (planet.owner.no != owner) {
					redraw = true;
				}
				try {
					planet.owner = Team.TEAM_REMAP[owner];
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.err.println("handleVPlanet(): planet owner out of bounds: " + owner);
					System.err.println("\tplanet number=" + planet_index);
					planet.owner = Team.TEAMS[Team.NOBODY];
				}

				// get the info
				info = buffer[++pos] & 0xFF;
				if (planet.info != info) {
					planet.info = info;
					if(planet.info == 0) {
						planet.owner = Team.TEAMS[Team.NOBODY];
					}
					redraw = true;
				}

				// get the armies
				armies = buffer[++pos] & 0xFF;
				if (planet.armies != armies
					// don't redraw when armies change unless it crosses the '4' * army limit. 
					// Keeps people from watching for planet 'flicker' when players are beaming
					&& ((planet.armies < 5 && armies > 4) || (planet.armies > 4 && armies < 5))) {
					redraw = true;
				}
				planet.armies = armies;

				// get the flags
				flags = shortFromPacket(++pos);
				if (planet.flags != flags) {
					planet.flags = flags;
					redraw = true;
				}

				if (redraw) {
					planet.flags |= Planet.REDRAW;
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleVPlanet(): bad planet number");
			System.err.println("\tplanet number=" + planet_index);
		}
	}
	
	void handleVPhaser() {
		try {
			int pnum = buffer[2] & 0x3F;
			int x = 0;
			int y = 0;
			int dir = 0;
			int target = 0;

			int status = buffer[1] & 0x0F;

			switch(status) {
			case Phaser.FREE:
				break;
			case Phaser.HIT:
				target =  buffer[3] & 0x3F;
				break;
			case Phaser.MISS:
				dir = buffer[3] & 0xFF;
				break;
			case Phaser.HIT2:
				x = Universe.SCALE * shortFromPacket(4);
				y = Universe.SCALE * shortFromPacket(6);
				target = buffer[3] & 0x3F;
				break;
			default:
				x = Universe.SCALE * shortFromPacket(4);
				y = Universe.SCALE * shortFromPacket(6);
				target = buffer[3] & 0x3F;
				dir = buffer[8] & 0xFF;
				break;
			}

			Phaser phaser = data.phasers[pnum];
			phaser.status = status;

			if (status != Phaser.FREE) {
				// normalized maxfuse
				phaser.maxfuse = (data.players[pnum].ship.phaserfuse * comm.updates_per_second) / 10;
			}

			phaser.target = data.players[target];
			phaser.fuse = 0;

			Point point = Rotate.rotateCoord(
						x,
						y,
						data.rotate,
						Universe.HALF_PIXEL_SIZE,
						Universe.HALF_PIXEL_SIZE);
				
			phaser.x = point.x;
			phaser.y = point.y;
			phaser.dir = Rotate.rotateDir(dir, data.rotate);

		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleVPhaser(): bad phaser index or target player number");
			System.err.println("\tphaser index=" + buffer[1]);
			System.err.println("\ttarget player number=" + intFromPacket(12));
		}
	}
	
	/** handleVKills */
	void handleVKills() {
		int player_index = 0;
		try {
			Player player;
			int data_pos = 2;
			int numofkills = buffer[1] & 0xFF;
			for(int p = 0; p < numofkills; ++p, data_pos += 2) {
				player_index = buffer[data_pos + 1] >>> 2;
				player = data.players[player_index];
				player.kills = (float)(buffer[data_pos] & 0xFF | ((buffer[data_pos + 1] & 0x03) << 8)) / 100f;
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handleVKills(): bad player number");
			System.err.println("\tplayer number=" + player_index);
		}
	}
	
	/** handle_s_Stats */
	void handle_s_Stats() {
		try {
			Stats stats = data.players[buffer[1]].stats;
			stats.tplanets = shortFromPacket(2);
			stats.tkills = shortFromPacket(4);
			stats.tlosses = shortFromPacket(6);
			stats.kills = shortFromPacket(8);
			stats.losses = shortFromPacket(10);
			stats.tticks = intFromPacket(12);
			stats.tarmsbomb = intFromPacket(16);
			if(data.players[buffer[1]].ship.type == Ship.SB && view.feature_list.features[FeatureList.SB_HOURS].value != Feature.OFF) {
				stats.sbticks = intFromPacket(20);
			}
			else {
				stats.maxkills = (double)intFromPacket(20) / 100d;
			}
			stats.sbkills = shortFromPacket(24);
			stats.sblosses = shortFromPacket(26);
			stats.armsbomb = shortFromPacket(28);
			stats.planets = shortFromPacket(30);
			stats.sbmaxkills = intFromPacket(32) / 100d;
			view.player_list.repaint();
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("handle_s_Stats(): bad player number");
			System.err.println("\tplayer number=" + buffer[1]);
		}
	}
	
	/** handleFeature */
	void handleFeature() {
		view.feature_list.checkFeature((char)buffer[1], (int)buffer[2], (int)buffer[3], intFromPacket(4), BufferUtils.extractString(buffer, 8, 80));
	}
	
	/** handleBitmap */
	void handleBitmap() {
		System.out.println("handleBitmap not implemented");
	}
}
