package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;

public abstract class XFocusPanel extends BasePanel implements MouseListener {

	public static XFocusPanel xfocus = null;
	
	XFocusPanel() {
		super();
	}

	XFocusPanel(String name) {
		super(name);
	}

	/** setup */
	void setup() {
		super.setup();
		addMouseListener(this);
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
		xfocus = this;
		requestFocus();
	}

	public void mouseExited(MouseEvent e) {
		xfocus = null;
	}

	/** handleKey */
	public boolean handleKey(KeyEvent e) {
		return false;
	}
}