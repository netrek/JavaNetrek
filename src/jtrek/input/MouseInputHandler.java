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
		char key = 1;
		if(e.isAltDown()) {
			key = 2;
		}
		else if(e.isMetaDown()) {
			key = 3;
		}
		key_input.handleKey(key);
	}
}
