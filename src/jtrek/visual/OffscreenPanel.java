package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;

abstract class OffscreenPanel extends XFocusPanel {
	//----------------------
	//-- instance members --
	//----------------------

	/** Offscreen image and Graphics object */
	Image offscreen;

	OffscreenPanel(String name) {
		super(name);
	}

	/** setup */
	void setup() {
		super.setup();
		offscreen = createImage(view_size.width, view_size.height);
		setFont(fixed_font);
	}

	/** paint */
	public void paint(Graphics g) {
		g.drawImage(offscreen, BORDER, BORDER, null);
		paintBorder(g);
	}

	/** clearOffscreen */
	void clearOffscreen(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, view_size.width, view_size.height);
		g.setColor(Color.white);
	}
}