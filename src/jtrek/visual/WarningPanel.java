package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.config.*;

public class WarningPanel extends XFocusPanel {
	final static long MESSAGE_TIME = 8000;

	NetrekFrame view;
	String message = "";
 	long clear_time;

	WarningPanel(String name, NetrekFrame view) {
		super(name);
		this.view = view;
	}

	/** setWarning */
	public void setWarning(String message) {
		this.message = message;
		if(message.startsWith("Phaser burst")) {
			ScrollString line = new ScrollString(message, Color.lightGray);
			view.review_phaser.addLine(line);
			switch(Defaults.phaser_message) {
			case 1:
				view.review_all.addLine(line);
				break;
			case 2:
				view.review_team.addLine(line);
				break;
			case 3:
				view.review_your.addLine(line);
				break;
			case 4:
				view.review_kill.addLine(line);
				break;
			case 5:
				view.review.addLine(line);
				break;
			}
		}
		clear_time = System.currentTimeMillis() + MESSAGE_TIME;
		repaint();
	}

	/** clearWarning */
	public void clearWarning() {
		if(clear_time > 0) {
			clear_time = 0;	
			repaint();
		}
	}

	/** update */
	public void update(Graphics g) {
		g.translate(BORDER, BORDER);
		g.clearRect(0, 0, view_size.width, view_size.height);
		long time_remaining = clear_time - System.currentTimeMillis();
		if(time_remaining > 0) {
			repaint(time_remaining);
			g.setColor(Color.white);
			g.drawString(message, 4, g.getFontMetrics().getHeight());
		}
	}
}