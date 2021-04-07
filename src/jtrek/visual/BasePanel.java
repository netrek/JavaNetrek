package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;
import jtrek.config.*;

public abstract class BasePanel extends java.awt.Panel implements WindowListener {
	//-------------------
	//-- class members --
	//-------------------

	final static int BORDER = 4;
	final static int TWO_BORDERS = 8;

	static Font fixed_font = new Font("Monospaced", Font.PLAIN, 10);
	static Font bold_fixed_font = new Font("Monospaced", Font.BOLD, 10);
	static Font variable_font = new Font("Sans-serif", Font.PLAIN, 10);
	static Font large_font = new Font("Serif", Font.BOLD, 35);
	static Font big_font = new Font("Sans-serif", Font.BOLD, 12);

	//----------------------
	//-- instance members --
	//----------------------

	/** name */
	String name;

	/** BorderColor */
	Color border_color = Color.white;

	/** window */
	Window window = null;

	/** */
	Dimension view_size = new Dimension();

	/** */
	BasePanel() {
		this("");
	}

	/** */
	BasePanel(String name) {
		this.name = name;
		setLayout(null);
		setBackground(Color.black);
		setForeground(Color.white);
		setFont(fixed_font);
	}

	public String getName() {
		return name;
	}

	/** setBounds */
	public void setBounds(int x, int y, int width, int height) {
		if(window != null) {
			Insets i = window.getInsets();
			window.setBounds(x, y, width + i.left + i.right - BORDER, height + i.top + i.bottom - BORDER);
			super.setBounds(i.left - BORDER / 2, i.top - BORDER / 2, width, height);
		}
		else {
			super.setBounds(x, y, width, height);
		}
		view_size.width = width - TWO_BORDERS;
		view_size.height = height - TWO_BORDERS;
		setup();
	}

	/** setup */
	void setup() {}

	/** setVisible */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(window != null) {
			window.setVisible(visible);
		}
	}

	/** paintBorder */
	void paintBorder(Graphics g) {
		Dimension d = getSize();
		g.setColor(border_color);
		g.fillRect(0, 0, d.width, BORDER);
		g.fillRect(0, 0, BORDER, d.height);
		g.fillRect(d.width - BORDER, 0, BORDER, d.height);
		g.fillRect(0, d.height - BORDER, d.width, BORDER);
	}

	/** paint */
	public void paint(Graphics g) {	
		paintBorder(g);
		update(g);
	}

	/** update */
	public void update(Graphics g) {
		// don't do anything!!!
	}

	/** detachWindow */
	public Window detachWindow(NetrekFrame parent) {
		// dialogs don't work on the sparc, so always create a frame on that platform.
		if(parent == null || System.getProperty("os.arch").equals("sparc")) {
			window = new Frame(Defaults.getWindowTitle(name));
			((Frame)window).setResizable(false);
			((Frame)window).setIconImage(NetrekFrame.ICON_IMAGE);
		}
		else {
			window = new Dialog(parent, Defaults.getWindowTitle(name), false);
			((Dialog)window).setResizable(false);
		}
		window.addWindowListener(this);
		window.setLayout(null);
		window.add(this);
		window.addNotify();
		return window;
	}

	void removeSelf() {
		Container parent = getParent();
		if(parent != null) {
			parent.remove(this);
			if(parent == window) {
				window.dispose();
				window = null;
			}
		}
	}
	
	public boolean isDetached() {
		return getParent() == window;	
	}

	/** windowClosing */
	public void windowClosing(WindowEvent e) {
		setVisible(false);
		getParent().setVisible(false);
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}
}