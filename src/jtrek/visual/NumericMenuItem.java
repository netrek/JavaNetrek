package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;

public class NumericMenuItem extends MenuItem {
	String pre_text;
	String post_text;
	int min;
	int max;
	int value;

	NumericMenuItem(int id, String pre_text, String post_text, int min, int max) {
		super(id);
		this.pre_text = pre_text;
		this.post_text = post_text;
		this.min = min;
		this.max = max;
		value = min;
	}

	int getValue() {
		return value;
	}

	void setValue(int value) {
		this.value = value;
		repaint();
	}

	public void paint(Graphics g) {
		drawText(g, pre_text + value + post_text);
	}

	/** 
	* mouseClicked 
	*/
	public void mousePressed(MouseEvent e) {
		if(e.isAltDown() || e.isMetaDown()) {
			if(--value < min) {
				value = max;
			}
		}
		else {
			if(++value > max) {
				value = min;
			}
		}
		createActionEvent(Integer.toString(value));
		repaint();
	}
}