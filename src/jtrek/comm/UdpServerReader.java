package jtrek.comm;

import java.io.*;
import java.net.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.visual.*;
import jtrek.config.*;

class UdpServerReader extends ServerReader {

	DatagramSocket socket;
	DatagramPacket packet;

	int udp_dropped = 0;
	int udp_recent_dropped = 0;
	int udp_total = 0;
	int recent_count = 0;
	int recent_dropped = 0;
	int sequence = 0;

	public UdpServerReader(Communications comm, Universe data, NetrekFrame view, DatagramSocket socket) {
		super(comm, data, view);
		this.socket = socket;
		buffer = new byte[1300];		// server code says UDP packets should be under 1300, the default is 960
		packet = new DatagramPacket(buffer, buffer.length);
	}
	
	/** doRead */
	void doRead() throws IOException {
		// reset the length of the packet to the max length
		packet.setLength(buffer.length);
		try {
			socket.receive(packet);
			count = packet.getLength();
		}
		catch(InterruptedIOException e) {
			if(Communications.UDP_DIAG)
				System.out.println("UDP: Timed out waiting for UDP response from server");
			view.warning.setWarning("Timed out waiting for UDP response from server");
			comm.sendUdpReq(COMM_TCP);
			comm.comm_mode = COMM_TCP;
			if (view.udp_win.isVisible()) {
				view.udp_win.refresh();
			}
			comm.closeUdpConn();
			count = 0;
		}
	}

	/** handlePacket */
	void handlePacket(int ptype) {
		if(Communications.UDP_DIAG)
			System.out.println("UDP: received packet type: " + ptype);
		++comm.packets_received;
		super.handlePacket(ptype);
	}

	void handleSequence() {
		udp_total++;
		recent_count++;

		// update percent display every 256 updates (~50 seconds usually)
		if ((udp_total & 0xFF) == 0 && view.udp_win.isVisible()) {
			view.udp_win.refresh();
		}

		int new_sequence = shortFromPacket(2);

		if (sequence > 65000 && new_sequence < 1000) {
			// we rolled, set new_sequence = 65536 + sequence and accept it
			sequence = ((sequence + 65536) & 0xFFFF0000) | new_sequence;
		}
		else {
			// adjust new_sequence and do compare
			new_sequence |= (sequence & 0xFFFF0000);
			if (!Defaults.udp_sequence_check) {
				// put this here so that turning seq check on and off doesn't
				// make us think we lost a whole bunch of packets.
				sequence = new_sequence;
				return;
			}
			if (new_sequence > sequence) {
				// accept
				if (new_sequence != sequence + 1) {
					int diff = (new_sequence - sequence) - 1;
					udp_dropped += diff;
					udp_total += diff;			// want TOTAL packets
					recent_dropped += diff;
					recent_count += diff;
					if (view.udp_win.isVisible()) {
						view.udp_win.refresh();
					}
					if(Communications.UDP_DIAG) 
						System.out.println("sequence=" + sequence + ", new_sequence=" + new_sequence + ", we lost some");
				}
				sequence = new_sequence;
				// S_P2
				if (comm.short_version == SHORTVERSION && comm.receive_short) {
					data.me.flags = (data.me.flags & 0xFFFF00FF) | (buffer[1] & 0xFF) << 8;
				}
			}
			else {
				// reject
				if (buffer[0] == SP_SC_SEQUENCE) {
					if(Communications.UDP_DIAG) 
						System.out.println("(ignoring repeat " + new_sequence + ')');
				}
				else {
					if(Communications.UDP_DIAG) 
						System.out.println("sequence=" + sequence + ", new_sequence=" + new_sequence + ", ignoring transmission");
				}
				// the remaining packets will be dropped and we shouldn't count the SP_SEQUENCE packet either
				comm.packets_received--;
				count = 4;
				if(Communications.UDP_DIAG)
					System.out.println("UDP: rejected out of sequence");
			}
		}
		if (recent_count > UDP_RECENT_INTR) {
			// once a minute (at 5 upd/sec), report on how many were dropped
			// during the last UDP_RECENT_INTR updates 
			udp_recent_dropped = recent_dropped;
			recent_count = recent_dropped = 0;
			if (view.udp_win.isVisible()) {
				view.udp_win.refresh();
			}
		}
	}
}

