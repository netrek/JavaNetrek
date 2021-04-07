package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.config.*;
import jtrek.data.*;

public class OptionsPanel extends BasePanel implements ActionListener {
	final static int CONTROL = 0;
	final static int SHIP = 1;
	final static int PLANET = 2;
	final static int VISUALS = 3;
	final static int INFO = 4;

	Universe data; 
	NetrekFrame view;
	Communications comm;

	MenuPanel[] menus;
	int current_menu = 0;

	/** 
	 * OptionsPanel 
	 */
	public OptionsPanel(String name, NetrekFrame view, Universe data, Communications comm) {
		super(name);
		this.data = data;
		this.view = view;
		this.comm = comm;

		menus = new MenuPanel[5];

		// control menu
		MenuItem[] items = new MenuItem[10];
		items[0] = new SubMenuItem(0, "Control Menu");
		items[1] = new SubMenuItem(0, "Page 0 (click to change)");
		ChoiceMenuItem c;
		items[2] = c = new ChoiceMenuItem(2);
		c.addItem("Don't stay peaceful when reborn");
		c.addItem("Stay peaceful when reborn");
		items[3] = new TextFieldMenuItem(3, "New keymap entries: ", 12, false);
		items[4] = new NumericMenuItem(4, "", " updates per second", 1, 10);
		items[5] = c = new ChoiceMenuItem(5);
		c.addItem("Don't show sound control window");
		c.addItem("Show sound control window");
		items[6] = c = new ChoiceMenuItem(6);
		c.addItem("Don't show UDP control window");
		c.addItem("Show UDP control window");
		items[7] = c = new ChoiceMenuItem(7);
		c.addItem("Don't show ping stats window");
		c.addItem("Show ping stats window");
		items[8] = c = new ChoiceMenuItem(8);
		c.addItem("Don't show short packets window");
		c.addItem("Show short packets window");
		items[9] = new ButtonMenuItem(1, "Done");
		menus[0] = new MenuPanel("control menu", items);
		add(menus[0]);
		menus[0].addMenuItems();
		for(int i = 0; i < items.length; ++i) {
			items[i].addActionListener(this);
		}

		// ship menu
		items = new MenuItem[8];
		items[0] = new SubMenuItem(0, "Ship Menu");
		items[1] = new SubMenuItem(0, "Page 1 (click to change)");
		items[2] = c = new ChoiceMenuItem(102);
		c.addItem("Don't vary shields bitmap");
		c.addItem("Use vary shields bitmap");
		items[3] = c = new ChoiceMenuItem(103);
		c.addItem("Don't use warning shields");
		c.addItem("Use warning shields");
		items[4] = c = new ChoiceMenuItem(104);
		c.addItem("Don't vary hull");
		c.addItem("Vary hull");
		items[5] = c = new ChoiceMenuItem(105);
		c.addItem("Don't show tractor/pressor beams");
		c.addItem("Show tractor/pressor beams");
		items[6] = c = new ChoiceMenuItem(106);
		c.addItem("Don't show tractors after lock");
		c.addItem("Show tractors after lock");
		items[7] = new ButtonMenuItem(1, "Done");
		menus[1] = new MenuPanel("ship menu", items);
		add(menus[1]);
		menus[1].addMenuItems();
		for(int i = 0; i < items.length; ++i) {
			items[i].addActionListener(this);
		}

		// planet menu
		items = new MenuItem[8];
		items[0] = new SubMenuItem(0, "Planet Menu");
		items[1] = new SubMenuItem(0, "Page 2 (click to change)");
		items[2] = c = new ChoiceMenuItem(202);
		c.addItem("Show owner on local planets");
		c.addItem("Show resources on local planets");
		c.addItem("Show nothing on local planets");
		items[3] = c = new ChoiceMenuItem(203);
		c.addItem("Don't show planet names on local");
		c.addItem("Show short planet names on local");
		c.addItem("Show planet names on local");
		items[4] = c = new ChoiceMenuItem(204);
		c.addItem("Show owner on galactic map");
		c.addItem("Show resources on galactic map");
		c.addItem("Show nothing on galactic map");
		c.addItem("Show rabbit ears on galactic map");
		items[5] = c = new ChoiceMenuItem(205);
		c.addItem("Don't show lock icon");
		c.addItem("Show lock icon on galactic map only");
		c.addItem("Show lock icon on tactical map only");
		c.addItem("Show lock icon on both map windows");
		items[6] = c = new ChoiceMenuItem(206);
		c.addItem("Don't rotate galaxy");
		c.addItem("Rotate galaxy 90 degrees");
		c.addItem("Rotate galaxy 180 degrees");
		c.addItem("Rotate galaxy 270 degrees");
		items[7] = new ButtonMenuItem(1, "Done");
		menus[2] = new MenuPanel("planet menu", items);
		add(menus[2]);
		menus[2].addMenuItems();
		for(int i = 0; i < items.length; ++i) {
			items[i].addActionListener(this);
		}

		// visuals menu
		items = new MenuItem[6];
		items[0] = new SubMenuItem(0, "Visuals Menu");
		items[1] = new SubMenuItem(0, "Page 3 (click to change)");
		items[2] = new NumericMenuItem(302, "Keep info ", " upds (0=don't remove)", 0, 100);
		items[3] = c = new ChoiceMenuItem(303);
		c.addItem("Don't use RCD highlighting");
		c.addItem("Use RCD highlighting");
		items[4] = c = new ChoiceMenuItem(304);
		c.addItem("Don't log phaser hits");
		c.addItem("Log phasers on all window");
		c.addItem("Log phasers on team window");
		c.addItem("Log phasers on indiv window"); 
		c.addItem("Log phasers on kill window");
		c.addItem("Log phasers on review window");
		items[5] = new ButtonMenuItem(1, "Done");
		menus[3] = new MenuPanel("visuals menu", items);
		add(menus[3]);
		menus[3].addMenuItems();
		for(int i = 0; i < items.length; ++i) {
			items[i].addActionListener(this);
		}

		// info menu
		items = new MenuItem[11];
		items[0] = new SubMenuItem(0, "Info Menu");
		items[1] = new SubMenuItem(0, "Page 4 (click to change)");
		items[2] = c = new ChoiceMenuItem(402);
		c.addItem("Don't show \"all\" message window");
		c.addItem("Show \"all\" message window");
		items[3] = c = new ChoiceMenuItem(403);
		c.addItem("Don't show \"team\" message window");
		c.addItem("Show \"team\" message window");
		items[4] = c = new ChoiceMenuItem(404);
		c.addItem("Don't show \"your\" message window");
		c.addItem("Show \"your\" message window");
		items[5] = c = new ChoiceMenuItem(405);
		c.addItem("Don't show \"kill\" message window");
		c.addItem("Show \"kill\" message window");
		items[6] = c = new ChoiceMenuItem(406);
		c.addItem("Don't show \"total\" message window");
		c.addItem("Show \"total\" message window");
		items[7] = c = new ChoiceMenuItem(407);
		c.addItem("Don't show phaser log window");
		c.addItem("Show phaser log window");
		items[8] = c = new ChoiceMenuItem(408);
		c.addItem("Don't show statistic window");
		c.addItem("Show statistic window");
		items[9] = c = new ChoiceMenuItem(409);
		c.addItem("Don't show help window");
		c.addItem("Show help window");
		items[10] = new ButtonMenuItem(1, "Done");
		menus[4] = new MenuPanel("info menu", items);
		add(menus[4]);
		menus[4].addMenuItems();
		for(int i = 0; i < items.length; ++i) {
			items[i].addActionListener(this);
		}

		menus[0].setVisible(true);
		menus[1].setVisible(false);
		menus[2].setVisible(false);
		menus[3].setVisible(false);
		menus[4].setVisible(false);
	}

	public void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		for(int i = 0; i < menus.length; ++i) {
			menus[i].addKeyListener(l);	
		}
	}

	public void setVisible(boolean visible) {
		if(visible) {
			refresh();
		}
		super.setVisible(visible);
	}
 
	public void refresh() {
		// control menu
		((ChoiceMenuItem)menus[0].menu_items[2]).setSelectedIndex(Defaults.keep_peace ? 1 : 0);
		((NumericMenuItem)menus[0].menu_items[4]).setValue(comm.getUpdatesPerSecond());
		((ChoiceMenuItem)menus[0].menu_items[5]).setSelectedIndex(view.sound_win.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[0].menu_items[6]).setSelectedIndex(view.udp_win.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[0].menu_items[7]).setSelectedIndex(view.ping_panel.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[0].menu_items[8]).setSelectedIndex(view.short_win.isVisible() ? 1 : 0);

		((ChoiceMenuItem)menus[1].menu_items[2]).setSelectedIndex(Defaults.vary_shields ? 1 : 0);
		((ChoiceMenuItem)menus[1].menu_items[3]).setSelectedIndex(Defaults.warn_shields ? 1 : 0);
		((ChoiceMenuItem)menus[1].menu_items[4]).setSelectedIndex(Defaults.vary_hull ? 1 : 0);
		((ChoiceMenuItem)menus[1].menu_items[5]).setSelectedIndex(Defaults.show_tractor_pressor ? 1 : 0);
		((ChoiceMenuItem)menus[1].menu_items[6]).setSelectedIndex(Defaults.continuous_tractor ? 1 : 0);
		((ChoiceMenuItem)menus[2].menu_items[2]).setSelectedIndex(Defaults.show_local);
		((ChoiceMenuItem)menus[2].menu_items[3]).setSelectedIndex(Defaults.name_mode);
		((ChoiceMenuItem)menus[2].menu_items[4]).setSelectedIndex(Defaults.show_galactic);
		((ChoiceMenuItem)menus[2].menu_items[5]).setSelectedIndex(Defaults.show_lock);
		((ChoiceMenuItem)menus[2].menu_items[6]).setSelectedIndex((data.rotate >= 0 ? data.rotate : 256 - data.rotate) / 64);
		((NumericMenuItem)menus[3].menu_items[2]).setValue(Defaults.keep_info);
		((ChoiceMenuItem)menus[3].menu_items[3]).setSelectedIndex(Defaults.use_lite ? 1 : 0);
		((ChoiceMenuItem)menus[4].menu_items[2]).setSelectedIndex(view.review_all.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[4].menu_items[3]).setSelectedIndex(view.review_team.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[4].menu_items[4]).setSelectedIndex(view.review_your.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[4].menu_items[5]).setSelectedIndex(view.review_kill.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[4].menu_items[6]).setSelectedIndex(view.review.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[4].menu_items[7]).setSelectedIndex(view.review_phaser.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[4].menu_items[8]).setSelectedIndex(view.stats_panel.isVisible() ? 1 : 0);
		((ChoiceMenuItem)menus[4].menu_items[9]).setSelectedIndex(view.help.isVisible() ? 1 : 0);
	}

	/** setBounds */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		for(int i = 0; i < menus.length; ++i) {
			menus[i].setBounds(0, 0, width, height);
		}
	}

	boolean checkBooleanChoice(int menu, int item) {
		return ((ChoiceMenuItem)menus[menu].menu_items[item]).getSelectedIndex() != 0;
	}

	public void actionPerformed(ActionEvent e) {
		switch(e.getID()) {
		case 0:
			menus[current_menu].setVisible(false);
			if(e.getActionCommand().charAt(0) == '+') {
				if(++current_menu >= menus.length) {
					current_menu = 0;
				}
			}
			else {
				if(--current_menu < 0) {
					current_menu = menus.length - 1;
				}
			}
			menus[current_menu].setVisible(true);
			break;
		case 1:
			optionsDone();
			break;
		case 2:
			Defaults.keep_peace = checkBooleanChoice(0, 2);
			break;
		case 5:
			view.sound_win.setVisible(checkBooleanChoice(0, 5));
			break;
		case 6:
			view.udp_win.setVisible(checkBooleanChoice(0, 6));
			break;
		case 7:
			view.ping_panel.setVisible(checkBooleanChoice(0, 7));
			break;
		case 8:
			view.short_win.setVisible(checkBooleanChoice(0, 8));
			break;
		case 102:
			Defaults.vary_shields = checkBooleanChoice(1, 2);
			break;
		case 103:
			Defaults.warn_shields = checkBooleanChoice(1, 3);
			break;
		case 104:
			Defaults.vary_hull = checkBooleanChoice(1, 4);
			break;
		case 105:
			Defaults.show_tractor_pressor = checkBooleanChoice(1, 5);
			break;
		case 106:
			Defaults.continuous_tractor = checkBooleanChoice(1, 6);
			break;
		case 202:
			Defaults.show_local = ((ChoiceMenuItem)menus[2].menu_items[2]).getSelectedIndex();
			break;
		case 203:
			Defaults.name_mode = ((ChoiceMenuItem)menus[2].menu_items[3]).getSelectedIndex();
			break;
		case 204:
			Defaults.show_galactic = ((ChoiceMenuItem)menus[2].menu_items[4]).getSelectedIndex();
			view.galactic.redraw_all_planets = true;
			break;
		case 205:
			Defaults.show_lock = ((ChoiceMenuItem)menus[2].menu_items[5]).getSelectedIndex();
			break;
		case 206:
			data.rotateGalaxy(((ChoiceMenuItem)menus[2].menu_items[6]).getSelectedIndex() * 64);
			view.galactic.redraw_all_planets = true;
			break;
		case 302:
			Defaults.keep_info = ((NumericMenuItem)menus[3].menu_items[2]).getValue();
			break;
		case 303:
			Defaults.use_lite = checkBooleanChoice(3, 3);
			break;
		case 304:
			Defaults.phaser_message = ((ChoiceMenuItem)menus[3].menu_items[4]).getSelectedIndex();
			break;
		case 402:
			view.review_all.setVisible(checkBooleanChoice(4, 2));
			break;
		case 403:
			view.review_team.setVisible(checkBooleanChoice(4, 3));
			break;
		case 404:
			view.review_your.setVisible(checkBooleanChoice(4, 4));
			break;
		case 405:
			view.review_kill.setVisible(checkBooleanChoice(4, 5));
			break;
		case 406:
			view.review.setVisible(checkBooleanChoice(4, 6));
			break;
		case 407:
			view.review_phaser.setVisible(checkBooleanChoice(4, 7));
			break;
		case 408:
			view.stats_panel.setVisible(checkBooleanChoice(4, 8));
			break;
		case 409:
			view.help.setVisible(checkBooleanChoice(4, 9));
			break;
		}
	}

	void optionsDone() {
		setVisible(false);

		// update keymap
		String keymap = ((TextFieldMenuItem)menus[0].menu_items[3]).getText();
		if((keymap.length() % 2) != 0) {
			// trim the odd character off the end
			keymap = keymap.substring(0, keymap.length() - 1);
		}
		if(keymap.length() > 0) {
			Keymap.mapKeys(keymap);
		}

		// update updates per second
		comm.sendUpdatePacket(((NumericMenuItem)menus[0].menu_items[4]).getValue());

		// send other options to the server
		comm.sendOptionsPacket();
	}
}
