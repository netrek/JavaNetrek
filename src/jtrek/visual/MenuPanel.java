package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.data.*;
import jtrek.util.*;

public class MenuPanel extends BasePanel {
	protected MenuItem[] menu_items = null;

	public MenuPanel(String name) {
		super(name);
	}

	public MenuPanel(String name, MenuItem[] menu_items) {
		super(name);
		this.menu_items = menu_items;
	}

	protected void addMenuItems() {
		for(int i = 0; i < menu_items.length; ++i) {
			add(menu_items[i]);
		}
	}

	public void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		for(int i = 0; i < menu_items.length; ++i) {
			menu_items[i].addKeyListener(l);	
		}
	}

	/** setBounds */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		y = BORDER / 2;
		for(int i = 0; i < menu_items.length; ++i) {
			menu_items[i].setBounds(BORDER / 2, y, width - BORDER, 22);
			y += 20;
		}
	}
}