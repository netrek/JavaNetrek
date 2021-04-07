package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;

public class ButtonMenuItem extends MenuItem {
	String label;

	ButtonMenuItem(int id, String label) {
		super(id);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		repaint();
	}

	public void paint(Graphics g) {
		drawText(g, label);
	}

	/** 
	* mousePressed 
	*/
	public void mousePressed(MouseEvent e) {
		createActionEvent(label);
	}

}
