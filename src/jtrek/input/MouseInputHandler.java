package jtrek.input;

import jtrek.Copyright;
import java.awt.event.*;
import java.awt.*;

public class MouseInputHandler extends MouseAdapter {
	KeyInputHandler key_input;

	public MouseInputHandler(KeyInputHandler key_input) {
		this.key_input = key_input;
	}

	/** mousePressed */
	public void mousePressed(MouseEvent e) {
		//char key = 1;
		char key = (char) e.getButton(); // BUTTON1 == 1, BUTTON2 == 2, BUTTON3 == 3
		if(e.isAltDown()) {
			key = 2;
		}
		else if(e.isMetaDown()) {
			key = 3;
		}
		key_input.handleKey(key);
	}
}
