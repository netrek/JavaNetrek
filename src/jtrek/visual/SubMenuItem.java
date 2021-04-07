package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;

public class SubMenuItem extends ButtonMenuItem {

	SubMenuItem(int id, String label) {
		super(id, label);
	}

	/** 
	* mousePressed 
	*/
	public void mousePressed(MouseEvent e) {
		if(e.isAltDown() || e.isMetaDown()) {
			createActionEvent("-");
		}
		else {
			createActionEvent("+");
		}
	}
}