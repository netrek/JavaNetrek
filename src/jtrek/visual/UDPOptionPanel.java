package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.comm.*;
import jtrek.config.*;
import jtrek.Copyright;

public class UDPOptionPanel extends MenuPanel implements ActionListener {
	Communications comm;

	/** UDPOptionPanel */
	public UDPOptionPanel(String name, Communications comm) {
		super(name);
		this.comm = comm;

		menu_items = new MenuItem[9];
		menu_items[0] = new ButtonMenuItem(0, "UDP Channel is CLOSED");
		menu_items[1] = new ButtonMenuItem(1, "> Status: Connected, yay us!");
		menu_items[2] = new ButtonMenuItem(2, "> UDP trans dropped: 0 (0% | 0%)");
		ChoiceMenuItem c;
		menu_items[3] = c = new ChoiceMenuItem(3);
		c.addItem("Sequence checking is ON");
		c.addItem("Sequence checking is OFF");
		menu_items[4] = c = new ChoiceMenuItem(4);
		c.addItem("Sending with TCP only");
		c.addItem("Sending with simple UDP");
		c.addItem("Sending with enforced UDP (state only)");
		c.addItem("Sending with enforced UDP (state & weap)");
		menu_items[5] = c = new ChoiceMenuItem(5);
		c.addItem("Receiving with TCP only");
		c.addItem("Receiving with simple UDP");
		c.addItem("Receiving with fat UDP");
		menu_items[6] = new ButtonMenuItem(6, "Force reset to TCP");
		menu_items[7] = new ButtonMenuItem(7, "Request full update");
		menu_items[8] = new ButtonMenuItem(8, "Done");
		for(int i = 0; i < menu_items.length; ++i) {
			menu_items[i].addActionListener(this);
		}
		addMenuItems();
	}

	public void setVisible(boolean visible) {
		if(visible) {
			refresh();
		}
		super.setVisible(visible);
	}

	public void refresh() {
		String s = null;
		switch(comm.getCommMode()) {
		case PacketTypes.COMM_TCP:
			s = "UDP Channel is CLOSED";
			break;
		case PacketTypes.COMM_UDP:
			s = "UDP Channel is OPEN";
			break;
		}
		((ButtonMenuItem)menu_items[0]).setLabel(s);
		s = "> Status: ";
		switch(comm.getCommStatus()) {
		case PacketTypes.STAT_CONNECTED:
			s = s.concat("Connected, yay us!");
			break;
		case PacketTypes.STAT_SWITCH_UDP:
			s = s.concat("Requesting switch to UDP");
			break;
		case PacketTypes.STAT_SWITCH_TCP:
			s = s.concat("Requesting switch to TCP");
			break;
		case PacketTypes.STAT_VERIFY_UDP:
			s = s.concat("Verifying UDP connection");
			break;
		default:
			System.err.println("jtrek: UDP error: bad commStatus (" + comm.getCommStatus() + ')');
			break;
		}
		((ButtonMenuItem)menu_items[1]).setLabel(s);

		int dropped = comm.getUDPDropped();
		int total = Math.max(comm.getUDPTotal(), 1);
		int recent = comm.getUDPRecentDropped();
		s = "> UDP trans dropped: " + dropped + " (" + (dropped * 100 / total) + "% | " + (recent * 100 / PacketTypes.UDP_RECENT_INTR) + "%)";
		((ButtonMenuItem)menu_items[2]).setLabel(s);

		((ChoiceMenuItem)menu_items[3]).setSelectedIndex(Defaults.udp_sequence_check ? 0 : 1);
		((ChoiceMenuItem)menu_items[4]).setSelectedIndex(Defaults.udp_client_send);
		((ChoiceMenuItem)menu_items[5]).setSelectedIndex(Defaults.udp_client_recv);

	}

	public void actionPerformed(ActionEvent e) {
		switch(e.getID()) {
		case 0:
			if(comm.getCommMode() == PacketTypes.COMM_TCP) {
				comm.sendUdpReq(PacketTypes.COMM_UDP);
			}
			else {
				comm.sendUdpReq(PacketTypes.COMM_TCP);
			}
			break;
		case 1:
		case 2:
			getToolkit().beep();
			break;
		case 3:
			Defaults.udp_sequence_check = ((ChoiceMenuItem)menu_items[3]).getSelectedIndex() == 0;
			break;
		case 4:
			Defaults.udp_client_send = ((ChoiceMenuItem)menu_items[4]).getSelectedIndex();
			break;
		case 5:
			Defaults.udp_client_recv = ((ChoiceMenuItem)menu_items[5]).getSelectedIndex();
			comm.sendUdpReq((byte)(PacketTypes.COMM_MODE + Defaults.udp_client_recv));
			break;
		case 6:
			// clobber UDP
			comm.forceResetToTCP();
			refresh();
			break;
		case 7:
			comm.sendUdpReq(PacketTypes.COMM_UPDATE);
			break;
		case 8:
			setVisible(false);
			break;
		}
	}
}