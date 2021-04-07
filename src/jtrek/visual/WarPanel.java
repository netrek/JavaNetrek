package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.data.*;
import jtrek.util.*;

public class WarPanel extends MenuPanel implements ActionListener {
	Universe data;
	NetrekFrame view;
	Communications comm;

	/** PlayerListPanel */
	public WarPanel(String name, Universe data, NetrekFrame view, Communications comm) {
		super(name);
		this.data = data;
		this.view = view;
		this.comm = comm;

		menu_items = new MenuItem[6];
		for(int i = 0; i < 4; ++i) {
			menu_items[i] = new WarMenuItem(i, data, view, i + 1);
		}
		menu_items[4] = new ButtonMenuItem(4, "  SAVE");
		menu_items[5] = new ButtonMenuItem(5, "  Exit - no change");
		menu_items[4].addActionListener(this);
		menu_items[5].addActionListener(this);
		addMenuItems();
	}

	public void resetState() {
		for(int i = 0; i < 4; ++i) {
			((WarMenuItem)menu_items[i]).resetState();
		}
	}

	public void actionPerformed(ActionEvent e) {
		switch(e.getID()) {
		case 4:
			int new_hostile = 0;
			for(int i = 0; i < 4; ++i) {
				if(((WarMenuItem)menu_items[i]).getState() == WarMenuItem.HOSTILE) {
					new_hostile |= Team.TEAMS[i + 1].bit;
				}
			}
			comm.sendWarReq((byte)new_hostile);
			setVisible(false);
			break;
		case 5:
			setVisible(false);
			break;
		}
	}
}