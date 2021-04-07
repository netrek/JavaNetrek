package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;

public class TextFieldMenuItem extends MenuItem {
	String pre_text;
	boolean numeric;
	char[] input;
	int length = 0;

	TextFieldMenuItem(int id, String pre_text, int length, boolean numeric) {
		super(id);
		this.pre_text = pre_text;
		input = new char[length];
		this.numeric = numeric;
	}

	public void setText(String s) {
		System.arraycopy(s.toCharArray(), 0, input, 0, Math.min(s.length(), input.length));
	}

	public String getText() {
		return new String(input, 0, length);	
	}

	public int getNumber() {
		if(length == 0) {
			return 0;
		}
		return Integer.parseInt(getText());
	}

	public void paint(Graphics g) {
		drawText(g, pre_text + new String(input, 0, length) + "_");
	}

	/** handleKey */
	public boolean handleKey(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_CANCEL:
			length = 0;
			break;
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_DELETE:
			if(--length < 0) {
				length = 0;
				return true;
			}
			break;
		default:
			char key = e.getKeyChar();
			if(key == KeyEvent.CHAR_UNDEFINED || length >= input.length
			  || (numeric && !Character.isDigit(key))) {
				return true;
			}
			input[length++] = (char)key;
			break;
		}
		repaint();
		return true;
	}	
}