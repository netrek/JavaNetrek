package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;

public class EntryClockPanel extends OffscreenPanel {
	final static int AUTOQUIT = 90; // Two Minutes
	final static int CLOCK_X = 50;
	final static int CLOCK_Y = 43;
	final static int CLOCK_SIZE = 70;

	NetrekFrame view;
	Image clock_image;

	int expire_time = 0;
	boolean timer_running = false;

	EntryClockPanel(String name, NetrekFrame view) {
		super(name);
		this.view = view;
	}

	/** setup */
	void setup() {
		super.setup();
		clock_image = ImageLoader.loadImage("/jtrek/images/intro/clock.gif");
		expire_time = Integer.MAX_VALUE;
	}

	/** resetTimer */
	synchronized void resetTimer() {
		expire_time = (int)(System.currentTimeMillis() / 1000) + AUTOQUIT;
		timer_running = true;
		repaint();
	}

	void stopTimer() {
		timer_running = false;
	}

	/** update */
	public synchronized void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(big_font);
		FontMetrics metrics = og.getFontMetrics();

		og.setColor(Color.white);
		og.drawImage(clock_image, 0, 0, this);

		int time = (int)(System.currentTimeMillis() / 1000);
		
		if (timer_running && time > expire_time) {
			System.out.println("Auto-Quit");
			view.quit(0);
			return;
		}

		double value = 2d * Math.PI * (double)-(expire_time - time) / (double)AUTOQUIT;
		int x = CLOCK_X - (int)(CLOCK_SIZE * Math.sin(value) / 2d);
		int y = CLOCK_Y - (int)(CLOCK_SIZE * Math.cos(value) / 2d);
		og.drawLine(CLOCK_X, CLOCK_Y, x, y);
		String stime = Integer.toString(expire_time - time);
		int swidth = metrics.stringWidth(stime);
 		x = CLOCK_X - swidth / 2;
		y = CLOCK_Y - metrics.getAscent();
		og.setColor(Color.black);
		og.fillRect(x - 2, y + metrics.getDescent() - 2, swidth + 4, metrics.getAscent() + 4);
		og.setColor(Color.white);
		og.drawString(stime, x, y + metrics.getHeight());
		g.drawImage(offscreen, BORDER, BORDER, null);
	}
	
	/** mouseClicked */
	public void mousePressed(MouseEvent e) {
		view.quit(0);
	}

	/** handleKey */
	public boolean handleKey(KeyEvent e) {
		view.quit(0);
		return true;
	}

}
