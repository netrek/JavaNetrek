package jtrek.comm;

import java.net.*;

import java.io.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.rsa.RSA;
import jtrek.visual.*;
import jtrek.config.*;
import jtrek.util.BufferUtils;

public class Communications {
	final static boolean UDP_DIAG = false;
	final static int MAX_PORT_RETRY = 10;

	final static byte SHORTVERSION = 11;				// other number blocks, like UDP Version
	final static byte OLDSHORTVERSION = 10;				// S_P2

	/** bigEndianInteger */
	static void bigEndianInteger(int value, byte[] dst, int dst_begin) {
		dst[dst_begin] = (byte)((value >>> 24) & 0xFF);
		dst[++dst_begin] = (byte)((value >>> 16) & 0xFF);
		dst[++dst_begin] = (byte)((value >>> 8) & 0xFF);
		dst[++dst_begin] = (byte)(value & 0xFF);
	}

	/** a long packet */
	byte[] buffer = new byte[104];

	Universe data;
	NetrekFrame view;
	OutputStream out;

	// communications status stuff
	int comm_mode = 0;
	int comm_mode_req;
	int comm_status;
	
	// TCP stuff
	Socket tcp_socket = null;
	TcpServerReader tcp_reader = null;

	// UDP stuff
	UdpServerReader udp_reader = null;
	DatagramSocket udp_socket = null;
	DatagramPacket udp_packet = null;
	int packets_sent = 0;
	int packets_received = 0;
	boolean ping = false;
	int base_udp_local_port;
	int udp_local_port;

	// force state and weapons
	int fSpeed = -1;
	int fShield = -1;
	int fOrbit = -1;
	int fRepair = -1;
	int fBeamup = -1;
	int fBeamdown = -1;
	int fCloak = -1;
	int fBomb = -1;
	int fDockperm = -1;
	int fPhaser = -1;
	int fPlasma = -1;
	int fPlayLock = -1; 
	int fPlanLock = -1;
	int fTractor = -1;
	int fRepress = -1;
	
	InetAddress server = null;

	public int next_port = 0;

	int updates_per_second = 5;
	byte short_version = SHORTVERSION;
	boolean receive_short = false;
	public PingStats ping_stats = new PingStats();
	
	// slot to connect to
	int ghost_slot = -1;

	public Communications(Universe data, int base_udp_local_port) {																	  
		this.data = data;		
		this.base_udp_local_port = base_udp_local_port;
	}

	public int getCommMode() {
		return comm_mode;
	}

	public int getCommStatus() {
		return comm_status;
	}

	public int getUpdatesPerSecond() {
		return updates_per_second;
	}

	public boolean isReceiveShort() {
		return receive_short;
	}

	public int getUDPDropped() {
		return (udp_reader != null) ? udp_reader.udp_dropped : 0;
	}

	public int getUDPRecentDropped() {
		return (udp_reader != null) ? udp_reader.udp_recent_dropped : 0;
	}

	public int getUDPTotal() {
		return (udp_reader != null) ? udp_reader.udp_total : 0;
	}

	public void cleanUp() {
		if (tcp_socket != null) {
			sendByeReq();
			try {
				tcp_socket.close();
			}
			catch(Exception e) {
				// no big deal, we are leaving anyways
			}
			tcp_socket = null;
		}
		if (udp_socket != null) {
			try {
				udp_socket.close();
			} 
			catch(Exception e) {
				// no big deal, we are leaving anyways
			}
			udp_socket = null;
		}
	}

	public void setView(NetrekFrame view) {
		this.view = view;
	}
	
	public void setGhostStartSlot(int ghost_slot) {
		this.ghost_slot = ghost_slot;
	}

	public void readFromServerNoBlock() {
		// read from TCP connection
		try {
			if(tcp_reader.in.available() > 0) {
				tcp_reader.readFromServer();
			}
			else {
				Thread.sleep(200);
			}
		}
		catch(Exception e) {				
			e.printStackTrace();
			view.quit(-1);
		}
	}

	public void readFromServer() {
		// read from UDP connection
		if(udp_reader != null && comm_status != PacketTypes.STAT_SWITCH_TCP && Defaults.udp_client_recv != 0) {
			try {
				udp_reader.readFromServer();
			}
			catch(IOException e) {
				view.warning.setWarning("UDP link severed");
				printUdpInfo();
				sendUdpReq(PacketTypes.COMM_TCP);
				comm_mode = PacketTypes.COMM_TCP;
				if (view.udp_win.isVisible()) {
					view.udp_win.refresh();
				}
				closeUdpConn();
			}

			if (comm_status == PacketTypes.STAT_VERIFY_UDP) {
				view.warning.setWarning("UDP connection established");
				udp_reader.sequence = 0;  // reset sequence #s
				resetForce();

				if (Communications.UDP_DIAG)
					printUdpInfo();

				if(Communications.UDP_DIAG)
					System.out.println("UDP: connection established.");

				comm_mode = PacketTypes.COMM_UDP;
				comm_status = PacketTypes.STAT_CONNECTED;
					
				if (Defaults.udp_client_recv != PacketTypes.MODE_SIMPLE) {
					sendUdpReq((byte)(PacketTypes.COMM_MODE + Defaults.udp_client_recv));
				}

				if (view.udp_win.isVisible()) {
					view.udp_win.refresh();
				}
			}

			checkForce();
		}

		// read from TCP connection
		try {
			if(tcp_reader.in.available() > 0 || udp_reader == null || Defaults.udp_client_recv == 0) {
				tcp_reader.readFromServer();
			}
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
			System.err.println("Whoops!  We've been ghostbusted!");
			System.err.println("Pray for a miracle!");
			if (comm_mode == PacketTypes.COMM_UDP) {
				closeUdpConn();
			}
			comm_mode = comm_mode_req = PacketTypes.COMM_TCP;
			if (view.udp_win.isVisible()) {
				view.udp_win.refresh();
			}
			if(connectToServer()) {
				System.err.println("Yea!  We've been resurrected!");
			}
			else {
				System.err.println("Sorry,  We could not be resurrected!");
				view.quit(-1);
			}
		}
		catch(Exception e) {				
			e.printStackTrace();
			view.quit(-1);
		}
	}

	/** setSpeed */
	public final void sendSpeedReq(int speed) {
		sendShortPacket(PacketTypes.CP_SPEED, (byte)speed);		
	}

	/** sendDockingReq */
	public final void sendDockingReq(boolean on) {
		sendShortPacket(PacketTypes.CP_DOCKPERM, on);
	}

	/** sendCloakReq */
	public final void sendCloakReq(boolean on) {
		sendShortPacket(PacketTypes.CP_CLOAK, on);
	}

	/** sendRefitReq */
	public final void sendRefitReq(byte ship) {
		sendShortPacket(PacketTypes.CP_REFIT, ship);
	}

	/** sendDirReq */
	public final void sendDirReq(byte dir) {
		sendShortPacket(PacketTypes.CP_DIRECTION, dir);
	}

	/** sendPhaserReq */
	public final void sendPhaserReq(byte dir) {
		sendShortPacket(PacketTypes.CP_PHASER, dir);
	}

	/** sendTorpReq */
	public final void sendTorpReq(byte dir) {
		sendShortPacket(PacketTypes.CP_TORP, dir);
	}

	/** sendPlasmaReq */
	public final void sendPlasmaReq(byte dir) {
		sendShortPacket(PacketTypes.CP_PLASMA, dir);
	}

	/** sendShieldReq */
	public final void sendShieldReq(boolean on) {
		sendShortPacket(PacketTypes.CP_SHIELD, on);
	}

	/** sendBombReq */
	public final void sendBombReq(boolean bomb) {
		sendShortPacket(PacketTypes.CP_BOMB, bomb);
	}

	/** sendBeamReq */
	public final void sendBeamReq(boolean up) {
		sendShortPacket(PacketTypes.CP_BEAM, (byte)(up ? 1 : 2));
	}

	/** sendRepairReq */
	public final void sendRepairReq(boolean on) {
		sendShortPacket(PacketTypes.CP_REPAIR, on);
	}

	/** sendOrbitReq */
	public final void sendOrbitReq(boolean orbit) {
		sendShortPacket(PacketTypes.CP_ORBIT, orbit);
	}

	/** sendQuitReq */
	public final void sendQuitReq() {
		sendShortPacket(PacketTypes.CP_QUIT);
	}

	/** sendCoupReq */
	public final void sendCoupReq() {
		sendShortPacket(PacketTypes.CP_COUP);
	}

	/** sendByeReq */
	public final void sendByeReq() {
		sendShortPacket(PacketTypes.CP_BYE);
	}

	/** sendPractrReq */
	public final void sendPractrReq() {
		sendShortPacket(PacketTypes.CP_PRACTR);
	}

	/** sendDetonateReq */
	public final void sendDetonateReq() {
		sendShortPacket(PacketTypes.CP_DET_TORPS);
	}

	/** sendPlaylockReq */
	public final void sendPlaylockReq(int player) {
		sendShortPacket(PacketTypes.CP_PLAYLOCK, (byte)player);
	}

	/** sendPlanlockReq */
	public final void sendPlanlockReq(int planet) {
		sendShortPacket(PacketTypes.CP_PLANLOCK, (byte)planet);
	}

	/** sendResetStatsReq */ 
	public final void sendResetStatsReq(byte verify) {
	 	sendShortPacket(PacketTypes.CP_RESETSTATS, verify);
	}

	/** sendWarReq */
	public final void sendWarReq(byte mask) {
		sendShortPacket(PacketTypes.CP_WAR, mask);
	}

	/** sendTractorReq */
	public final void sendTractorReq(boolean on, int target) {
		buffer[0] = PacketTypes.CP_TRACTOR;
		buffer[1] = (byte)(on ? 1 : 0);
		buffer[2] = (byte)target;
		sendServerPacket(4);
		if(on) {
			// stop force
			fRepress = 0;
			fTractor = target | 0x40;
		} 
		else {
			fTractor = 0;
		}
	}

	/** sendRepressReq */
	public final void sendRepressReq(boolean on, int target) {
		buffer[0] = PacketTypes.CP_REPRESS;
		buffer[1] = (byte)(on ? 1 : 0);
		buffer[2] = (byte)target;
		sendServerPacket(4);
		if(on) {
			// stop force
			fTractor = 0;
			fRepress = target | 0x40;
		} 
		else {
			fRepress = 0;
		}
	}

	/** sendTeamReq */
	public final void sendTeamReq(byte team, byte ship) {
		buffer[0] = PacketTypes.CP_OUTFIT;
		buffer[1] = team;
		buffer[2] = ship;
		sendServerPacket(4);
	}

	/** sendDetMineReq */
	public final void sendDetMineReq(short torp) {
		buffer[0] = PacketTypes.CP_DET_MYTORP;
		buffer[2] = (byte)((torp >>> 8) & 0xFF);
		buffer[3] = (byte)(torp & 0xFF);
		sendServerPacket(4);
	}

	/** insertString */
	void insertString(String string, int offset, int max_length) {
		byte[] bytes = BufferUtils.getBytes(string);
		int length = (bytes.length < max_length) ? bytes.length : max_length;
		System.arraycopy(bytes, 0, buffer, offset, length);
		buffer[offset + length] = 0;
	}

	/** sendFeature */
	public void sendFeature(String name, char feature_type, int value, int arg1, int arg2) {
		buffer[0]= PacketTypes.CP_FEATURE; 
		buffer[1] = (byte)feature_type;
		buffer[2] = (byte)arg1;
		buffer[3] = (byte)arg2;
		bigEndianInteger(value, buffer, 4);
		insertString(name, 8, 79);
		sendServerPacket(88);
	}

	/** sendLoginReq */
	public void sendLoginReq(String name, String pass, String login, boolean query) {
		buffer[0]= PacketTypes.CP_LOGIN; 
		buffer[1] = (byte)(query ? 1 : 0);
		insertString(name, 4, 15);
		insertString(pass, 20, 15);
		insertString(login, 36, 15);
		sendServerPacket(52);
	}

	/** sendReservedResponse */
	void sendReservedResponse(byte[] sreserved, byte[] response) {
		buffer[0] = PacketTypes.CP_RESERVED;
		
		System.arraycopy(sreserved, 0, buffer, 4, sreserved.length);
		System.arraycopy(response, 0, buffer, 20, response.length);
		buffer[response.length + 20] = 0;
		sendServerPacket(36);
	}

	/** sendRSAResponse */
	void sendRSAResponse(byte[] data) {
		buffer[0] = PacketTypes.CP_RSA_KEY;
		// query the socket to determine the remote host (ATM)
		System.arraycopy(server.getAddress(), 0, data, 0, 4);		
		int port = tcp_socket.getPort();
		data[4] = (byte)((port >>> 8) & 0xFF);
		data[5] = (byte)(port & 0xFF);

		byte[] response = RSA.generateRSAResponse(data);
		System.arraycopy(response, 0, buffer, 4, response.length);
		sendServerPacket(4 + response.length);
	}

	/** setPingOn */
	public void setPingOn(boolean start) {
		buffer[0] = PacketTypes.CP_PING_RESPONSE;
		buffer[2] = (byte)(start ? 1 : 0);
		sendServerPacket(12);
	}

	/** sendServerPingResponse */
	void sendServerPingResponse(byte number)	{ 
		buffer[0] = PacketTypes.CP_PING_RESPONSE;
		buffer[1] = number;
		buffer[2] = (byte)(ping ? 1 : 0);
		bigEndianInteger(packets_sent + (comm_mode == PacketTypes.COMM_UDP && Defaults.udp_client_send != 0 ? 1 : 0), buffer, 4);
		bigEndianInteger(packets_received, buffer, 8);
		sendServerPacket(12);
	}

	/** sendMessage */
	public void sendMessage(String message, int group, int indiv) {
		buffer[0] = PacketTypes.CP_MESSAGE;
		buffer[1] = (byte)group;
		buffer[2] = (byte)indiv;
		insertString(message, 4, 79);
		sendServerPacket(84);
	}

	/** sendUpdatePacket */
	public void sendUpdatePacket(int updates_per_second) {
		if(this.updates_per_second == updates_per_second) {
			return;
		}
		this.updates_per_second = updates_per_second;
		buffer[0]= PacketTypes.CP_UPDATES;
		bigEndianInteger(1000000 / updates_per_second, buffer, 4);
		sendServerPacket(8);
	}
 
	/** sendShortReq */
	public void sendShortReq(byte state) {
		buffer[0] = PacketTypes.CP_S_REQ;
		buffer[1] = state;
		buffer[2] = short_version;
		switch (state) {
		case PacketTypes.SPK_VON :
			view.warning.setWarning("Sending short packet request");
			break;
		case PacketTypes.SPK_VOFF :
			view.warning.setWarning("Sending old packet request");
			break;
		}
		if(receive_short && (state == PacketTypes.SPK_SALL || state == PacketTypes.SPK_ALL)) {
			// Let the client do the work, and not the network :-)
			for(int t = 0; t < data.torps.length; ++t) {
				data.torps[t].status = Torp.FREE;
			}

			for(int p = 0; p < data.plasmas.length; ++p) {
				data.plasmas[p].status = Plasma.FREE;
			}

			for(int p = 0; p < data.phasers.length; ++p) {
				data.phasers[p].status = Phaser.FREE;
			}

			for(int p = 0; p < data.players.length; ++p) {
				data.players[p].ntorp = 0;
				data.players[p].nplasmatorp = 0;
			}

			view.warning.setWarning("Sent request for small update");
		}
		sendServerPacket(4);
	}

	/** sendThreshold */
	public void sendThreshold(int threshold) {
		buffer[0] = PacketTypes.CP_S_THRS;
		buffer[2] = (byte)((threshold >>> 8) & 0xFF);
		buffer[3] = (byte)(threshold & 0xFF);
		sendServerPacket(4);
	}

	/** sendShortPacket */
	public final void sendShortPacket(byte type) {
		sendShortPacket(type, (byte)0);
	}

	/** sendShortPacket */
	public final void sendShortPacket(byte type, boolean state) {
		sendShortPacket(type, (byte)(state ? 1 : 0));
	}

	/** sendShortPacket */
	public void sendShortPacket(byte type, byte state) {
		buffer[0] = type;
		buffer[1] = state;
		sendServerPacket(4);

		// if we're sending in UDP mode, be prepared to force it
		if (comm_mode == PacketTypes.COMM_UDP && Defaults.udp_client_send >= 2) {
			switch (type) {
			case PacketTypes.CP_SPEED:
				fSpeed = state | 0x100;
				break;
			case PacketTypes.CP_SHIELD:
				fShield = state | 0xA00;
				break;
			case PacketTypes.CP_ORBIT:
				fOrbit = state | 0xA00;
				break;
			case PacketTypes.CP_REPAIR:
				fRepair = state | 0xA00;
				break;
			case PacketTypes.CP_CLOAK:
				fCloak = state | 0xA00;
				break;
			case PacketTypes.CP_BOMB:
				fBomb = state | 0xA00;
				break;
			case PacketTypes.CP_DOCKPERM:
				fDockperm = state | 0xA00;
				break;
			case PacketTypes.CP_PLAYLOCK:
				fPlayLock = state | 0xA00;
				break;
			case PacketTypes.CP_PLANLOCK:
				fPlanLock = state | 0xA00;
				break;
			case PacketTypes.CP_BEAM:
				if (state == 1)
					fBeamup = 1 | 0x500;
				else
					fBeamdown = 2 | 0x500;
				break;
			}

			// force weapons too?
			if (Defaults.udp_client_send >= 3) {
				switch (type) {
				case PacketTypes.CP_PHASER:
					fPhaser = state | 0x100;
					break;
				case PacketTypes.CP_PLASMA:
					fPlasma = state | 0x100;
					break;
				}
			}
		}
	}

	/** sendShortPacket */
	public void sendShortPacketNoForce(byte type, int state) {
		buffer[0] = type;
		buffer[1] = (byte)state;
		sendServerPacket(4);
	}

	/** sendServerPacket */
	void sendServerPacket(int size) {
		if(comm_mode == PacketTypes.COMM_UDP && Defaults.udp_client_send != 0) {
			// UDP stuff
			switch (buffer[0]) {
				case PacketTypes.CP_SPEED:
				case PacketTypes.CP_DIRECTION:
				case PacketTypes.CP_PHASER:
				case PacketTypes.CP_PLASMA:
				case PacketTypes.CP_TORP:
				case PacketTypes.CP_QUIT:
				case PacketTypes.CP_PRACTR:
				case PacketTypes.CP_SHIELD:
				case PacketTypes.CP_REPAIR:
				case PacketTypes.CP_ORBIT:
				case PacketTypes.CP_PLANLOCK:
				case PacketTypes.CP_PLAYLOCK:
				case PacketTypes.CP_BOMB:
				case PacketTypes.CP_BEAM:
				case PacketTypes.CP_CLOAK:
				case PacketTypes.CP_DET_TORPS:
				case PacketTypes.CP_DET_MYTORP:
				case PacketTypes.CP_REFIT:
				case PacketTypes.CP_TRACTOR:
				case PacketTypes.CP_REPRESS:
				case PacketTypes.CP_COUP:
				case PacketTypes.CP_DOCKPERM:
				case PacketTypes.CP_PING_RESPONSE:
					// non-critical stuff, use UDP
					++packets_sent;
					if(UDP_DIAG) 
						System.out.println("UDP: Sent " + buffer[0] + " on UDP port.");
					udp_packet.setLength(size);

					try {
						udp_socket.send(udp_packet);
					}
					catch(IOException io) {
						if(UDP_DIAG) 
							System.out.println("UDP: send failed.  Closing UDP connection");
						view.warning.setWarning("UDP link severed");
						comm_mode_req = comm_mode;
						comm_status = PacketTypes.STAT_CONNECTED;
						if (view.udp_win.isVisible()) {
							view.udp_win.refresh();
						}
						closeUdpConn();
					}
					return;
			}
		}

		try {
			if(out != null) {
				out.write(buffer, 0, size);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/** reset all the "force command" variables */
	void resetForce() {
		fSpeed = fShield = fOrbit = fRepair = fBeamup = fBeamdown = fCloak 
		  = fBomb = fDockperm = fPhaser = fPlasma = fPlayLock = fPlanLock 
		  = fTractor = fRepress = -1;
	}

	/** 
	* If something we want to happen hasn't yet, send it again.
	* The low byte is the request, the high byte is a max count.  When the max
	* count reaches zero, the client stops trying.	Checking is done with a
	* macro for speed & clarity. 
	*/
	int forceCheckFlags(int flag, int force, byte type) {
		if (force > 0) {
			if ((data.me.flags & flag) != (force & 0xFF)) {
				sendShortPacketNoForce(type, (force & 0xFF));
				if(UDP_DIAG)
					System.out.println("UDP: Forced " + type + ':' + (force & 0xFF));
				force -= 0x100;
				if (force < 0x100) {
					force = -1;  // give up
				}
			} 
			else {
				force = -1;
			}
		}
		return force;
	}

	/** forceCheckValue */
	int forceCheckValue(int value, int force, byte type) {
		if (force > 0) {
			if (value != (force & 0xFF)) {
				sendShortPacketNoForce(type, (force & 0xFF));
				if(UDP_DIAG)
					System.out.println("UDP: Forced " + type + ':' + (force & 0xFF));
				force -= 0x100;
				if (force < 0x100) {
					force = -1;	// give up
				}
			} 
			else {
				force = -1; 
			}
		}
		return force;
	}
 
	/** forceCheckTract */
	int forceCheckTract(int flag, int force, byte type) {
		if (force > 0) {
			if (((data.me.flags & flag) != 0) ^ ((force & 0xFF) != 0)) {
				int state = ((force & 0xFF) >= 0x40) ? 1 : 0;
				int pnum = (force & 0xFF) & (~0x40);
				buffer[0] = type;
				buffer[1] = (byte)state;
				buffer[2] = (byte)pnum;
				sendServerPacket(4);
				if(UDP_DIAG)
					System.out.println("UDP: Forced " + type + ':' + state + '/' + pnum);
				force -= 0x100;
				if (force < 0x100) {
					force = -1;	// give up
				}
			}
			else {
				force = -1;
			}
		}
		return force;
	}
	
	/** checkForce */
	void checkForce() {
		// speed almost always repeats because it takes a while to accelerate/decelerate
		// to the desired value
		fSpeed = forceCheckValue(data.me.speed, fSpeed, PacketTypes.CP_SPEED);
		fShield = forceCheckFlags(Player.SHIELD, fShield, PacketTypes.CP_SHIELD);
		fOrbit = forceCheckFlags(Player.ORBIT, fOrbit, PacketTypes.CP_ORBIT);
		fRepair = forceCheckFlags(Player.REPAIR, fRepair, PacketTypes.CP_REPAIR);
		fBeamup = forceCheckFlags(Player.BEAMUP, fBeamup, PacketTypes.CP_BEAM);
		fBeamdown = forceCheckFlags(Player.BEAMDOWN, fBeamdown, PacketTypes.CP_BEAM);
		fCloak = forceCheckFlags(Player.CLOAK, fCloak, PacketTypes.CP_CLOAK);
		fBomb = forceCheckFlags(Player.BOMB, fBomb, PacketTypes.CP_BOMB);
		fDockperm = forceCheckFlags(Player.DOCKOK, fDockperm, PacketTypes.CP_DOCKPERM);
		fPhaser = forceCheckValue(data.phasers[data.me.no].status, fPhaser, PacketTypes.CP_PHASER);
		fPlasma = forceCheckValue(data.plasmas[data.me.no].status, fPlasma, PacketTypes.CP_PLASMA);
		fPlayLock = forceCheckFlags(Player.PLOCK, fPlayLock, PacketTypes.CP_PLAYLOCK);
		fPlanLock = forceCheckFlags(Player.PLLOCK, fPlanLock, PacketTypes.CP_PLANLOCK);
		fTractor = forceCheckTract(Player.TRACT, fTractor, PacketTypes.CP_TRACTOR);
		fRepress = forceCheckTract(Player.PRESS, fRepress, PacketTypes.CP_REPRESS);
	}

	/** detonateMyTorps */
	public void detonateMyTorps() {
		Torp torp;
		int offset = (data.me.no * 8);
		for (int t = 0; t < 8; ++t) {
			torp = data.torps[t + offset];
			if (torp.status == Torp.MOVE || torp.status == Torp.STRAIGHT) {
				sendDetMineReq((byte)torp.no);
				if (receive_short) {
					// Let the server det for me
					break;
				}
			}
		}
	}

	/** callServer */
	public boolean callServer(String host, int port) {
		try {
			server = InetAddress.getByName(host);
		}
		catch(UnknownHostException e) {
			System.err.println("Who is " + host + '?');
			return false;
		}
		System.out.println("Calling " + host + " on port " + port + ".");
		try {
			tcp_socket = new Socket(server, port);
			System.out.println("Got connection");
			out = tcp_socket.getOutputStream();
			pickSocket(port);
			tcp_reader = new TcpServerReader(this, data, view, tcp_socket.getInputStream());
		}
		catch(IOException e1) {
			if(e1 instanceof BindException) {
				System.err.println("I can't create a socket");
			}
			else if(e1 instanceof ConnectException) {
				System.err.println("Server not listening!");
			}
			else if(e1 instanceof NoRouteToHostException) {
				System.err.println("Connection timed out");
			}
			else {
				System.err.println("Error connecting to server.");	
			}
			return false;
		}
		sendFeature("FEATURE_PACKETS", 'S', 1, 0, 0);
		return true;
	}

	/** connectToServer */
	public boolean connectToServer(String host, int port) {
		try {
			server = InetAddress.getByName(host);
		}
		catch(UnknownHostException e) {
			System.err.println("Who is " + host + '?');
			return false;
		}
		return connectToServer(port);
	}

	/** connectToServer */
	boolean connectToServer() {
		return connectToServer(next_port);
	}

	/** connectToServer */
	public boolean connectToServer(int port) {
		System.out.println("Waiting for connection (port " + port + ").");
		out = null;
		ServerSocket server_socket = null;

		try {
			if (tcp_socket != null) {
				tcp_socket.close();
				tcp_socket = null;
			}

			try {
				server_socket = new ServerSocket(port, 1);
			}
			catch(IOException e) {
				if(e instanceof BindException) {
					System.err.println("I can't create a socket");
				}
				else if(e instanceof ConnectException) {
					System.err.println("Server not listening!");
				}
				else if(e instanceof NoRouteToHostException) {
					System.err.println("Connection timed out");
				}
				else {
					System.err.println("Error connecting to server.");	
				}
				return false;
			}

			server_socket.setSoTimeout(240000);
			tcp_socket = server_socket.accept();
			
			if(server == null) {
				server = tcp_socket.getInetAddress();
			}
			
			out = tcp_socket.getOutputStream();
			pickSocket(port);

			if(tcp_reader == null) {
				tcp_reader = new TcpServerReader(this, data, view, tcp_socket.getInputStream());
			}
			else {
				tcp_reader.count = 0;
				tcp_reader.in = tcp_socket.getInputStream();
			}
			System.out.println("Connection from server " + server.getHostName() + " port " + port);
		}
		catch(InterruptedIOException e) {
			System.err.println("Well, I think the server died!");
			return false;
		}
		catch(IOException e) {
			if(e instanceof BindException) {
				System.err.println("I can't create a socket");
			}
			else if(e instanceof ConnectException) {
				System.err.println("Server not listening!");
			}
			else if(e instanceof NoRouteToHostException) {
				System.err.println("Connection timed out");
			}
			else {
				System.err.println("Error connecting to server.");	
			}
			return false;
		}
		finally {
			if(server_socket != null) {
				try {
					server_socket.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	/** sendOptionsPacket */
	public void sendOptionsPacket() {
		buffer[0] = PacketTypes.CP_OPTIONS;

		int flags = 
			(Stats.MAPMODE				// always on
			+ Stats.NAMEMODE * Defaults.name_mode
			+ Stats.SHOWSHIELDS			// always on
			+ Stats.KEEPPEACE * (Defaults.keep_peace ? 1 : 0)
			+ Stats.SHOWLOCAL * Defaults.show_local
			+ Stats.SHOWGLOBAL * Defaults.show_galactic);

		bigEndianInteger(flags, buffer, 4);

		// insert a blank string as the keymap
		insertString("", 8, 96);
		sendServerPacket(104);
	}

	/** pickSocket */
	void pickSocket(int old_port) {
		next_port = (int)(Math.random() * 32767);
		while (next_port < 2048 || next_port == old_port) {
			next_port = ((next_port + 10687) & 32767);
		}
		buffer[0] = PacketTypes.CP_SOCKET;
		buffer[1] = PacketTypes.SOCKVERSION;
		buffer[2] = PacketTypes.UDPVERSION;
		bigEndianInteger(next_port, buffer, 4);
		sendServerPacket(8);
	}

	/** sendUdpReq */
	public void sendUdpReq(byte req) {
		buffer[0] = PacketTypes.CP_UDP_REQ;
		buffer[1] = (byte)(req & 0xFF);

		if (req >= PacketTypes.COMM_MODE) {
			buffer[1] = PacketTypes.COMM_MODE;
			buffer[2] = (byte)((req - PacketTypes.COMM_MODE) & 0xFF);
			sendServerPacket(8);
			return;
		}

		if (req == PacketTypes.COMM_UPDATE) {
			if (receive_short) {
				// Let the client do the work, and not the network
				data.resetWeaponInfo();
			}

			sendServerPacket(8);
			view.warning.setWarning("Sent request for full update");
			return;
		}

		if (req == comm_mode_req) {
			view.warning.setWarning("Request is in progress, do not disturb");
			return;
		}

		if (req == PacketTypes.COMM_UDP) {
			// open UDP port
			if(openUdpConn()) {
				if(UDP_DIAG) 
					System.out.println("UDP: Bound to local port " + udp_local_port);
			}
			else {
				if(UDP_DIAG) 
					System.out.println("UDP: Bind to local port " + udp_local_port + " failed");
				comm_mode_req = PacketTypes.COMM_TCP;
				comm_status = PacketTypes.STAT_CONNECTED;
				if (view.udp_win.isVisible()) {
					view.udp_win.refresh();
				}
				view.warning.setWarning("Unable to establish UDP connection");
				return;
			}
		}
		// send the request
		buffer[0] = PacketTypes.CP_UDP_REQ;
		buffer[1] = req;
		buffer[2] = PacketTypes.CONNMODE_PORT; // we get addr from packet
		bigEndianInteger(udp_local_port, buffer, 4);
		sendServerPacket(8);

		// update internal state stuff
		comm_mode_req = req;
		if (req == PacketTypes.COMM_TCP) {
			comm_status = PacketTypes.STAT_SWITCH_TCP;
		}
		else {
			comm_status = PacketTypes.STAT_SWITCH_UDP;
		}

		if(UDP_DIAG) 
			System.out.println("UDP: Sent request for " + (req == PacketTypes.COMM_TCP ? "TCP" : "UDP") + " mode");

		if (view.udp_win.isVisible()) {
			view.udp_win.refresh();
		}
	}

	/** openUdpConn */
	boolean openUdpConn() {

		// if base_udp_local_port is defined, we want to start from that
		if(base_udp_local_port > 0)  {
			udp_local_port = base_udp_local_port;
			if(UDP_DIAG) 
				System.out.println("UDP: using base port " + base_udp_local_port);
		}
		else {
			udp_local_port = (int)(Math.random() * 32767);
		}

		for(int attempts = 0; attempts < MAX_PORT_RETRY; ++attempts) {
			while(udp_local_port < 2048) {
				udp_local_port = ((udp_local_port + 10687) & 32767);
			}
			try {
				udp_socket = new DatagramSocket(udp_local_port);
				udp_socket.setSoTimeout(5000);
				if(UDP_DIAG) 
					System.out.println("UDP: Local port is " + udp_local_port);
				return true;
			}
			catch(SocketException e) {
				// bind() failed, so find another port.  If we're tunneling through a
				// router-based firewall, we just increment; otherwise we try to mix it
				// up a little.  The check for ports < 2048 is done above.
				if(base_udp_local_port > 0) {
					++udp_local_port;
				}
				else {
					udp_local_port = ((udp_local_port + 10687) & 32767);
				}
			}
			catch(Exception e) {
				// catch other exception types, like security exceptions!
				e.printStackTrace();
				break;
			}
		}
		if(UDP_DIAG) 
			System.out.println("UDP: Unable to find a local port to bind to");
		udp_socket = null;
		return false;
	}
 
	/** printUdpInfo */
	void printUdpInfo() {
		if(!UDP_DIAG || udp_socket == null) {
			return;
		}
		InetAddress i = udp_socket.getLocalAddress();
		System.out.println("UDP: LOCAL: addr=" + i.getHostAddress() + ", port=" + udp_socket.getLocalPort());
		System.out.println("UDP: PEER : addr=" + udp_packet.getAddress().getHostAddress() + ", port=" +  udp_packet.getPort());
	}

	/** closeUdpConn */
	void closeUdpConn() {
		if(UDP_DIAG) 
			System.out.println("UDP: Closing UDP socket");
		udp_reader = null;
		if(udp_socket != null) {
			udp_socket.close();
			udp_socket = null;
		}
		udp_packet = null;
	}

	public void forceResetToTCP() {
		if(UDP_DIAG) 
			System.out.println("UDP: FORCE RESET REQUESTED");
		sendUdpReq(PacketTypes.COMM_TCP);
		comm_mode = comm_mode_req = PacketTypes.COMM_TCP;
		comm_status = PacketTypes.STAT_CONNECTED;
		Defaults.udp_client_send = Defaults.udp_client_recv = 1;
		Defaults.udp_sequence_check = true;
		closeUdpConn();
	}

	/** connUdpConn */
	void connUdpConn(int port) {
		udp_reader = new UdpServerReader(this, data, view, udp_socket);
		udp_packet = new DatagramPacket(buffer, buffer.length, server, port);
		printUdpInfo();
	}

	/** sendUdpVerify */
	void sendUdpVerify() {
		buffer[0] = PacketTypes.CP_UDP_REQ;
		buffer[1] = PacketTypes.COMM_VERIFY;
		buffer[2] = (byte)0;
		bigEndianInteger(0, buffer, 4);
		udp_packet.setLength(8);
		try {
			udp_socket.send(udp_packet);
		}
		catch(IOException e) {
			if(UDP_DIAG) 
				System.out.println("UDP: send failed.  Closing UDP connection");
			view.warning.setWarning("UDP link severed");
			comm_mode_req = comm_mode;
			comm_status = PacketTypes.STAT_CONNECTED;
			if (view.udp_win.isVisible()) {
				view.udp_win.refresh();
			}
			closeUdpConn();
		}
	}
}
