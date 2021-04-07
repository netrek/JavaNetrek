package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.config.*;
import jtrek.util.*;

public class ShortPacketsOptionPanel extends MenuPanel implements ActionListener {
	Communications comm;

	/** UDPOptionPanel */
	public ShortPacketsOptionPanel(String name, Communications comm) {
		super(name);
		this.comm = comm;

		menu_items = new MenuItem[3];
		menu_items[0] = new ButtonMenuItem(0, "Receive variable and short packets");
		menu_items[1] = new TextFieldMenuItem(1, "Receive threshold: ", 4, true);
		menu_items[2] = new ButtonMenuItem(2, "Done");
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
		if(comm.isReceiveShort()) {
			s = "Receive variable and short packets";
		}
		else {
			s = "Don't receive variable and short packets";
		}
		((ButtonMenuItem)menu_items[0]).setLabel(s);
	}

	public void actionPerformed(ActionEvent e) {
		switch(e.getID()) {
		case 0:
			comm.sendShortReq(comm.isReceiveShort() ? PacketTypes.SPK_VOFF : PacketTypes.SPK_VON);
			break;
		case 2:
			int number = ((TextFieldMenuItem)menu_items[1]).getNumber();
			if(number > 0) {
				comm.sendThreshold(number);
			}
			setVisible(false);
			break;
		}
	}
}