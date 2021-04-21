package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.comm.MetaServerEntry;
import jtrek.util.BufferUtils;

public class MetaServerPanel extends MenuPanel implements ActionListener, WindowListener {
	int item_selected = 0;
	
	public MetaServerPanel(String name, MetaServerEntry[] entries) {
		super(name);
		menu_items = new MenuItem[entries.length + 1];
		char[] buffer = new char[70];
		for (int i = 0; i < entries.length; ++i) {
			MetaServerEntry entry = entries[i];
			BufferUtils.fillWithSpaces(buffer, 0, 70);
			
			String s = entry.address;			
			int length = Math.min(s.length(), 40);
			s.getChars(0, length, buffer, 0);
			
			s = MetaServerEntry.STATUS_STRINGS[entry.status];
			length = Math.min(s.length(), 14);
			s.getChars(0, length, buffer, 41);
			
			if (entry.status <= MetaServerEntry.NULL) {
				if (entry.status == MetaServerEntry.OPEN || entry.status == MetaServerEntry.WAIT) {
					s = Integer.toString(entry.players);
					s.getChars(0, s.length(), buffer, 42 + length);
				}
				switch (entry.server_type) {
				case 'P':
					s = "Paradise";
					break;
				case 'B':
					s = "Bronco";
					break;
				case 'C':
					s = "Chaos";
					break;
				case 'I':
					s = "INL";
					break;
				case 'S':
					s = "Sturgeon";
					break;
				case 'H':
					s = "Hockey";
					break;
				case 'E':
					s = "Empire";
					break;
				case 'F':
					s = "Dogfight";
					break;
				default:
					s = "Unknown";
					break;
				}
				s.getChars(0, s.length(), buffer, 60);
			}			
			menu_items[i] = new ButtonMenuItem(i, new String(buffer));
			menu_items[i].addActionListener(this);
		}
		menu_items[entries.length] = new ButtonMenuItem(-1, "Quit");
		menu_items[entries.length].addActionListener(this);		
		addMenuItems();		
    }
	
	public void windowActivated(WindowEvent e) {
	}
	
	public void windowClosed(WindowEvent e) {
	}
	
	public synchronized void windowClosing(WindowEvent e) {
		item_selected = -1;
		notifyAll();
	}
	
	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}
	
	public void windowIconified(WindowEvent e) {
	}
		
	public void windowOpened(WindowEvent e) {
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		item_selected = e.getID();
		notifyAll();
	}
	
	public synchronized int waitForResult() {
		try {
			wait();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return item_selected;
	}
}
