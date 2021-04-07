package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.Copyright;

public class WaitQueuePanel extends BasePanel {
	public final static int WIDTH = 192;
	public final static int HEIGHT = 70;
	final static int TITLE_HEIGHT = 17;
	final static int SECTION_WIDTH = 64;
	final static int SECTION_HEIGHT = 47;

	final static String TITLE_STR = "Netrek: Game is full";
	final static String QUIT_STR = "Quit";
	final static String WAIT_STR = "Wait Q";
	final static String MOTD_STR = "MOTD";

	/** Constructor */
	NetrekFrame view;

	/** the number of players in the wait queue before the user */
	int wait_count;

	/**
	 * Constructor
	 */
	public WaitQueuePanel(String name, NetrekFrame view) {
		super(name);
		this.view = view;
		this.wait_count = wait_count;				
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				handleMousePressed(e.getX(), e.getY());
			}
		});
	}

	/**
	 * update
	 */
	public void update(Graphics g) {
		g.translate(BORDER, BORDER);
		g.clearRect(0, 0, view_size.width, view_size.height);
		g.setColor(Color.white);
		g.drawLine(0, TITLE_HEIGHT, WIDTH, TITLE_HEIGHT);
		g.drawRect(SECTION_WIDTH, TITLE_HEIGHT, SECTION_WIDTH, SECTION_HEIGHT);
		FontMetrics metrics = g.getFontMetrics();
		int line_height = metrics.getHeight();
		g.drawString(TITLE_STR, (WIDTH - metrics.stringWidth(TITLE_STR)) / 2, line_height);
		g.drawString(QUIT_STR, (SECTION_WIDTH - metrics.stringWidth(QUIT_STR)) / 2, line_height + 30);
		g.drawString(WAIT_STR, (SECTION_WIDTH - metrics.stringWidth(WAIT_STR)) / 2 + SECTION_WIDTH, line_height + 24);
		String wait_str = Integer.toString(wait_count);
		g.drawString(wait_str, (SECTION_WIDTH - metrics.stringWidth(wait_str)) / 2 + SECTION_WIDTH, line_height * 2 + 24);
		g.drawString(WAIT_STR, (SECTION_WIDTH - metrics.stringWidth(WAIT_STR)) / 2 + SECTION_WIDTH, line_height + 24);
		g.drawString(MOTD_STR, (SECTION_WIDTH - metrics.stringWidth(MOTD_STR)) / 2 + SECTION_WIDTH * 2, line_height + 30);
	}

	/** 
	 * setWaitCount
	 */
	public void setWaitCount(int wait_count) {
		this.wait_count = wait_count;
		((Frame)window).setTitle("Wait Q: " + wait_count);
		repaint();
	}

	/** 
	 * doneWaiting 
	 */
	public void doneWaiting() {
		((Window)getParent()).dispose();
	}

	/** 
	 * windowClosing 
	 */
	public void windowClosing(WindowEvent e) {
		window.dispose();
		view.quit(0);
	}
	
	/**
	 * handleMousePressed
	 */
	void handleMousePressed(int x, int y) {
		if(y > TITLE_HEIGHT) {
			if(x < SECTION_WIDTH) {
				System.out.println("OK");
				view.quit(0);
			}
			else if(x >= SECTION_WIDTH * 2) {
				view.showMotd();
			}
		}
	}
}