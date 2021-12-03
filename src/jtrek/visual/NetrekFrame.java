package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.comm.*;
import jtrek.input.*;
import jtrek.config.*;
import jtrek.message.*;

public class NetrekFrame extends Frame {
	static Image ICON_IMAGE = ImageLoader.loadImage("/jtrek/images/misc/icon.gif");
	public BasePanel[] windows;

	Communications comm;
	Universe data;

	public LoginPanel login;
	public EntryPanel entry;
	public InfoPanel server_info;
	public LocalPanel local;
	public GalacticPanel galactic;
	public Dashboard dashboard;  // also known as tstat i think
	public WarningPanel warning;
	public MessagePanel message;
	public PlayerListPanel player_list;	
	public PlanetListPanel planet_list;
	public RankListPanel rank_list;
	public StatsPanel stats_panel; 
	public PingStatsPanel ping_panel; 
	public ScrollPanel review;
	public ScrollPanel review_all;
	public ScrollPanel review_team;
	public ScrollPanel review_your;
	public ScrollPanel review_kill;
	public ScrollPanel review_phaser;
	public InfoPanel attached_motd;	
	public InfoPanel detached_motd;
	public WarPanel war;
	public UDPOptionPanel udp_win;
	public ShortPacketsOptionPanel short_win;
	public OptionsPanel options;
	public SoundControlPanel sound_win;
	public HelpPanel help;
	public MacroPanel macro_win;
	public InformationPanel information;
	public WaitQueuePanel wait_queue = null;
	public SoundPlayer sound_player;

	public IncomingMessageHandler dmessage;
	public SendMessage smessage;
	public KeyInputHandler key_handler;
	public MouseInputHandler mouse_handler;
	public FeatureList feature_list;
	public Beeplite beeplite;
	public boolean quit = false;
	private NetrekFrame netrekFrame;

	public NetrekFrame(Communications comm, Universe data) {
		super("Netrek");
		this.data = data;
		this.comm = comm;
		setLayout(null);
		setBackground(Color.black);
		setIconImage(ICON_IMAGE);
		
		netrekFrame = this;
		
		// install the message handlers
		dmessage = new IncomingMessageHandler(this, data, comm);
		smessage = new SendMessage(this, data, comm);

		beeplite = new Beeplite();
		
		sound_player = new SoundPlayer();
		sound_player.all_sounds_on = Defaults.sound;
	
		// create all the windows
		login = new LoginPanel("login", this);
		information = new InformationPanel("information", this, data);
		war = new WarPanel("war", data, this, comm);
		udp_win = new UDPOptionPanel("UDP", comm);
		short_win = new ShortPacketsOptionPanel("network", comm);
		options = new OptionsPanel("option", this, data, comm);
		sound_win = new SoundControlPanel("sound", sound_player);
		attached_motd = new InfoPanel("motd2", this, data, comm);
		entry = new EntryPanel("entry", this, attached_motd);
		server_info = new InfoPanel("info", this, data, comm);
		local = new LocalPanel("local", this, data, information, beeplite);
		galactic = new GalacticPanel("map", this, data, information, beeplite);

		try {
			Class clazz = Class.forName(Defaults.dashboard_class);
			dashboard = (Dashboard)clazz.newInstance();
		}
		catch(ClassNotFoundException ce) {
			System.err.println("Couldn't find class: " + Defaults.dashboard_class + " for dashboard.");
			dashboard = new TextDashboard();
		}
		catch(Exception e) {
			System.err.println("Couldn't construct a dashboard out of class: " + Defaults.dashboard_class);
			System.err.println("Exception: " + e.getClass().getName());
			dashboard = new TextDashboard();
		}
		dashboard.setUniverse(data);

		try {
			Class clazz = Class.forName(Defaults.player_list_class);
			player_list = (PlayerListPanel)clazz.newInstance();
		}
		catch(ClassNotFoundException ce) {
			System.err.println("Couldn't find class: " + Defaults.player_list_class + " for player list.");
			player_list = new StandardPlayerList();
		}
		catch(Exception e) {
			System.err.println("Couldn't construct a player list out of class: " + Defaults.player_list_class);
			System.err.println("Exception: " + e.getClass().getName());
			player_list = new StandardPlayerList();
		}
		
		try {
			if(Defaults.sound_engine_class.length() > 0) {
				Class clazz = Class.forName(Defaults.sound_engine_class);
				SoundEngine sound_engine = (SoundEngine)clazz.newInstance();
				sound_player.setSoundEngine(sound_engine);
			}
		}
		catch(ClassNotFoundException ce) {
			System.err.println("Couldn't find class: " + Defaults.player_list_class + " for sound engine.");
			sound_player = new SoundPlayer();
		}
		catch(Exception e) {
			System.err.println("Couldn't construct a sound engine out of class: " + Defaults.player_list_class);
			System.err.println("Exception: " + e.getClass().getName());
			sound_player = new SoundPlayer();
		}
		
		player_list.setUniverse(data);

		warning = new WarningPanel("warn", this);
		message = new MessagePanel("message", this);
		review = new ScrollPanel("review");
		review_all = new ScrollPanel("review_all");
		review_team = new ScrollPanel("review_team");
		review_your = new ScrollPanel("review_your");
		review_kill = new ScrollPanel("review_kill");
		review_phaser = new ScrollPanel("review_phaser");
		planet_list = new PlanetListPanel("planet", data);
		rank_list = new RankListPanel("rank", data);
		stats_panel = new StatsPanel("stats", data);
		ping_panel = new PingStatsPanel("pingStats", comm.ping_stats);
		detached_motd = new InfoPanel("motd", this, data, comm);
		help = new HelpPanel("help");
		macro_win = new MacroPanel("macrow", this);

		windows = new BasePanel[26];
		windows[0] = login;
		windows[1] = entry;
		windows[2] = server_info;
		windows[3] = stats_panel;
		windows[4] = ping_panel; 
		windows[5] = dashboard;
		windows[6] = warning;
		windows[7] = message;
		windows[8] = review;
		windows[9] = review_all;
		windows[10] = review_team;
		windows[11] = review_your;
		windows[12] = review_kill;
		windows[13] = review_phaser;
		windows[14] = player_list;
		windows[15] = planet_list;
		windows[16] = galactic;
		windows[17] = local;
		windows[18] = rank_list;
		windows[19] = war;
		windows[20] = udp_win;
		windows[21] = short_win;
		windows[22] = options;
		windows[23] = sound_win;
		windows[24] = help;
		windows[25] = macro_win;

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				shutDown();
			}
		});
		
		// create the input handlers
		key_handler = new KeyInputHandler(this, data, comm);
		mouse_handler = new MouseInputHandler(key_handler);

		// add the keyboard handlers
		addKeyListener(key_handler);
		information.addKeyListener(key_handler);
		attached_motd.addKeyListener(key_handler);
		for(int w = windows.length; --w >= 0;) {
			windows[w].addKeyListener(key_handler);
		}
		for (int t = entry.team_panels.length; --t >= 0;) {
			entry.team_panels[t].addKeyListener(key_handler);
		}
		entry.clock_panel.addKeyListener(key_handler);

		// add the mouse handlers
		local.addMouseListener(mouse_handler);
		galactic.addMouseListener(mouse_handler);

		// add the first few lines to the info panel
		server_info.addLine("                     S E R V E R   D E F A U L T S");
		server_info.addLine("                     =============================");
		server_info.addLine("");

		feature_list = new FeatureList(comm);

		detached_motd.addKeyListener(key_handler);
		detached_motd.detachWindow(null).addKeyListener(key_handler);
		setupBasePanel(detached_motd);
		
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // This is only called when the user releases the mouse button.
                System.out.println("componentResized");
                netrekFrame.resizeWindows();
            }
        });

	}
	
	void resizeWindows() {
		int windowWidth = this.getWidth();
		int windowHeight = this.getHeight();
		
		int leftYPosition = 0;
		int rightYPosition = 0;
		int halfWidth = windowWidth / 2;
		
		int mapDimension = Math.min(halfWidth, windowHeight * 742 / 1008); //keep maps square
		
		// overlapping front windows
		
		entry.setBounds(0, 0, mapDimension, mapDimension);
		if (login != null) {
			login.setBounds(0, 0, mapDimension, mapDimension);
		}
		information.setBounds(0, 0, mapDimension, mapDimension);
		attached_motd.setBounds(halfWidth, 0, mapDimension, mapDimension);
		options.setBounds(halfWidth, 0, mapDimension, mapDimension);
		
		// left column
		
		local.setBounds(0,0,mapDimension, mapDimension); 
		leftYPosition += mapDimension;

		dashboard.setBounds(0, leftYPosition, halfWidth, 51);;
		leftYPosition += 51;
		
		player_list.setBounds(0, leftYPosition, halfWidth, windowHeight /4);
		leftYPosition += windowHeight / 4;

		// right column
		
		galactic.setBounds(halfWidth, 0, mapDimension, mapDimension);
		rightYPosition += mapDimension;
		
		warning.setBounds(halfWidth, rightYPosition, halfWidth, windowHeight / 28);
		rightYPosition += windowHeight / 28;
		
		message.setBounds(halfWidth, rightYPosition, windowWidth/2, windowHeight / 28);
		rightYPosition += windowHeight / 28;
		
		review.setBounds(halfWidth,rightYPosition, halfWidth, windowHeight / 4);
		rightYPosition += windowHeight / 4;
		
		// The reviews below do not seem to work
		// even with review above is commented out
		review_all.setBounds(halfWidth,rightYPosition, halfWidth, windowHeight / 8);
		rightYPosition += windowHeight / 8;
		
		review_team.setBounds(halfWidth,rightYPosition, halfWidth, windowHeight / 8);
		rightYPosition += windowHeight / 8;
		
		review_your.setBounds(halfWidth, rightYPosition, halfWidth, windowHeight / 8);
		rightYPosition += windowHeight / 8;
		
		review_kill.setBounds(halfWidth, rightYPosition, halfWidth, windowHeight / 8);
		rightYPosition += windowHeight / 8;
		
		review_phaser.setBounds(halfWidth, rightYPosition, halfWidth, windowHeight / 8);
		rightYPosition += windowHeight / 8;

		
	}
	
	void shutDown() {
		System.out.println("Please wait while I tell the server you are quitting.");
		if(data.me != null && data.me.status != Player.OUTFIT) {
			comm.sendQuitReq();
		}
		quit = true;
		setVisible(false);
	} 

	/** getWindow */
	Container getWindow(String name) {
		if(name.equals("netrek")) {
			return this;
		}
		for(int w = windows.length; --w >= 0;) {
			if(windows[w].name.equals(name)) {
				return windows[w];
			}
		}
		return null;
	}

	/** 
	 * waitQueue
	 */
	public void waitQueue(int wait_count) {
		if(wait_queue == null) {
			wait_queue = new WaitQueuePanel("queue", this);
			wait_queue.detachWindow(null).addKeyListener(key_handler);
			wait_queue.setWaitCount(wait_count);			
			wait_queue.setBounds(new Rectangle(200, 200, 200, 70));
			wait_queue.setVisible(true);
		}
		else {
			wait_queue.setWaitCount(wait_count);
		}
	}

	/**
	* slotFound 
	*/
	public void slotFound() {
		if(wait_queue != null) {
			wait_queue.doneWaiting();
			wait_queue = null;
		}
		// the peer has to be created before other windows are added or else the 
		// z-order of dialogs don't stay over this frame correctly
		addNotify();
		addWindows();
		//newWindowSetup();
		Insets insets = getInsets();
		Rectangle rect = Defaults.getWindowGeometry("netrek");
		rect.width += insets.left + insets.right;
		rect.height += insets.top + insets.bottom;
		setBounds(rect);
		setVisible(true);
		setupWindows();
		requestFocus();
	}
	
	/*public void newWindowSetup() {
		
		this.setBounds(0,0,1008,742);
		
		add(information);
		
		BasePanel login = (BasePanel) getWindow("login");
		login.setVisible(false);
		Container netrekWindow = getWindow("netrek");
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		netrekWindow.add(login,constraints);
		
		BasePanel local = (BasePanel) getWindow("local");
		local.setMinimumSize(new Dimension(508,508));
		local.setBounds(0, 0, 500, 500);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		netrekWindow.add(local,constraints);
		local.setVisible(true);


		BasePanel entry = (BasePanel) getWindow("entry");
		entry.setVisible(false);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		netrekWindow.add(entry,constraints);

	}
*/
	
	public void showLogin() {
		local.setVisible(true);
		galactic.setVisible(true);		
		
		login.setVisible(true);
		server_info.setVisible(true);
		entry.setVisible(false);
	}

	/** addWindows */ 
	void addWindows() {
		add(information);
		for(int w = windows.length; --w >= 0;) {
			addWindow(windows[w]);
		}
	}

	/** addWindow */
	void addWindow(BasePanel panel) {
		panel.setVisible(false);
		String parent_name = Defaults.getWindowParent(panel.name);
		if(parent_name.equalsIgnoreCase("root")) {
			panel.detachWindow(this).addKeyListener(key_handler);
		}
		else if(parent_name.equalsIgnoreCase("none")) {
			panel.detachWindow(null).addKeyListener(key_handler);
		}
		else {
			Container parent = getWindow(parent_name);
			if(parent == null) {
				System.err.println("Can't find window: " + parent_name + " to place window: " + panel.name + " inside of.  Mapping to netrek instead.");
				parent = this;
			}
			try {
				//GridBagConstraints constraints = getConstraints(panel);
				setSize(panel);
				//if (constraints != null) {
				//	parent.add(panel,constraints);
				//} else {
					parent.add(panel);
				//}
				panel.setVisible(Defaults.getWindowVisible(panel.name));
			}
			catch(IllegalArgumentException e) {
				System.err.println("Error adding window \"" + panel.getName() + "\" to parent, " + e.getMessage());
				panel.detachWindow(null).addKeyListener(key_handler);
			}
		}
	}
	
	/** setupWindows */
	void setupWindows() {
		information.setVisible(false);
		for(int w = 0; w < windows.length; ++w) {
			setupBasePanel(windows[w]);
		}
	}
	

	/** setupBasePanel */
	void setupBasePanel(BasePanel panel) {
		if(panel.getParent() != null) {
			Rectangle geometry = Defaults.getWindowGeometry(panel.name);
			if(panel.getParent() == this) {
				Insets i = getInsets();
				geometry.translate(i.left, i.top);
			}
			panel.setBounds(geometry);
		}
		panel.setVisible(Defaults.getWindowVisible(panel.name));
	}	

	/*private GridBagConstraints getConstraints(BasePanel panel) {
		GridBagConstraints constraints = new GridBagConstraints();
		if (panel.name.equals("local")) {
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			return constraints;
		} else if (panel.name.equals("map")) {
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.anchor = GridBagConstraints.NORTHEAST;
			return constraints;
		} else {
			return null;
		}
	}*/
	
	void setSize(BasePanel panel) {
		if(panel.getParent() != null) {
			Rectangle geometry = Defaults.getWindowGeometry(panel.name);
			if (geometry.x < 0) {
				geometry.x = 0;
			}
			if (geometry.y < 0) {
				geometry.y = 0;
			}
			panel.setBounds(geometry);
		}
	}
	/**
	 * New attempt to setup windows for netrek 0.9.7 to allow resizing
	 * @param panel
	 */
	/*void setupBasePanel2(BasePanel panel) {
		GridBagConstraints constraints = new GridBagConstraints();

		if (panel.name.equals("local")) {
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			panel.setSize(508, 508);
			panel.setVisible(true);
		} else if (panel.name.equals("map")) {
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.anchor = GridBagConstraints.NORTHEAST;
			panel.setSize(508, 508);
			panel.setVisible(true);
		} else {
			if(panel.getParent() != null) {
				Rectangle geometry = Defaults.getWindowGeometry(panel.name);
				if(panel.getParent() == this) {
					Insets i = getInsets();
					geometry.translate(i.left, i.top);
				}
				panel.setBounds(geometry);
			}
			panel.setVisible(Defaults.getWindowVisible(panel.name));
		}
	}*/

	/** loginComplete */
	public void loginComplete() {
		login.getParent().remove(login);
		login = null;
	}

	/** pickOK */
	public void pickOK(boolean value) {
		if(value) {
			entry.setVisible(false);
			key_handler.entryModeComplete();
			server_info.setVisible(false);
		}
	}

	/** setWarpCursor */
	public void setWarpCursor(boolean warp) {
		Cursor cursor = warp ? 
			Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR) :
			Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

		galactic.setCursor(cursor);
		dashboard.setCursor(cursor);
		warning.setCursor(cursor);
		review.setCursor(cursor);
		review_all.setCursor(cursor);
		review_team.setCursor(cursor);
		review_your.setCursor(cursor);
		review_kill.setCursor(cursor);
		review_phaser.setCursor(cursor);
		player_list.setCursor(cursor);
		planet_list.setCursor(cursor);
		rank_list.setCursor(cursor);
		stats_panel.setCursor(cursor);
		ping_panel.setCursor(cursor);

		local.setCursor(warp ?
			Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR) :
			Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	/** newShip */
	public void newShip() {
		local.newShip();
		galactic.redraw_all_planets = true;
	}

	/** gotoOutfit */
	public void gotoOutfit() {
		local.goneToOutfit();
		information.setVisible(false);
		entry.setVisible(true);
		key_handler.gotoEntryMode();
		server_info.showDeathInfo(true);
		server_info.setVisible(true);
	}

	/** getTarget */
	public Object getTarget(int target_type) {
		if(XFocusPanel.xfocus != null && XFocusPanel.xfocus instanceof BaseMapPanel) {
			return((BaseMapPanel)XFocusPanel.xfocus).getTarget(target_type);
		}
		return data.getTarget(target_type);
	}

	/** getCourse */
	public byte getCourse() {
		if(XFocusPanel.xfocus != null && XFocusPanel.xfocus instanceof BaseMapPanel) {
			return((BaseMapPanel)XFocusPanel.xfocus).getCourse();
		}
		return local.getCourse();
	}

	/** quit */
	public void quit(int error) {
		quit = true;
		setVisible(false);
		//System.out.println("local time/updates    = " + LocalPanel.time / LocalPanel.updates);
		//System.out.println("galactic time/updates = " + GalacticPanel.time / GalacticPanel.updates);
		//System.out.println("tstat time/updates    = " + TextDashboard.time / TextDashboard.updates);
		//System.out.println("player time/updates = " + PlayerListPanel.time / PlayerListPanel.updates);
	}

	boolean which = false;

	/** */
	public void redraw() {
		// run the keep info clock
		information.tick();

		// force the local to draw now
		local.update(local.getGraphics());
		dashboard.repaint();
		stats_panel.repaint();
		which = !which;
		if(which) {
			planet_list.repaint();
		}
		else {
			player_list.repaint();
		}
		galactic.update(galactic.getGraphics());
		getToolkit().sync();
	}

	/** addMotdLine */
	public void addMotdLine(String line) {
		attached_motd.addLine(line);
		detached_motd.addLine(line);
	}

	/** */
	public void showMotd() {
		detached_motd.setVisible(true);
		detached_motd.repaint();
	}

	/*
	public void setNewPanelParent(String panel_name, String parent_name) {
		BasePanel panel = null;
		for(int w = windows.length; --w >= 0;) {
			if(windows[w].name.equals(panel_name)) {
				panel = windows[w];
			}
		}
		boolean visible = panel.isVisible();
		stats_panel.removeSelf();
		if(parent_name.equalsIgnoreCase("root")) {
			panel.detachWindow(this).addKeyListener(key_handler);
		}
		else if(parent_name.equalsIgnoreCase("none")) {
			panel.detachWindow(null).addKeyListener(key_handler);
		}
		else {
			Container parent = getWindow(parent_name);
			if(parent == null) {
				System.err.println("Can't find window: " + parent_name + " to place window: " + panel.name + " inside of.  Mapping to netrek instead.");
				parent = this;
			}
			try {
				parent.add(panel);
			}
			catch(IllegalArgumentException e) {
				System.err.println("Error adding window \"" + panel.getName() + "\" to parent, " + e.getMessage());
				panel.detachWindow(null).addKeyListener(key_handler);
			}
		}
		panel.setVisible(visible);
	}

	public void setNewPanelVisible(String panel_name, boolean visible) {
		for(int w = windows.length; --w >= 0;) {
			if(windows[w].name.equals(panel_name)) {
				windows[w].setVisible(visible);
			}
		}
	}

	public void setNewPanelGeometry(String panel_name, Rectangle rect) {
		for(int w = windows.length; --w >= 0;) {
			if(windows[w].name.equals(panel_name)) {
				windows[w].setBounds(rect);
			}
		}
	}
	*/
}