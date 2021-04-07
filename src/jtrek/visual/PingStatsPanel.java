package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.comm.*;

public class PingStatsPanel extends BasePanel {
	final static int BAR_X = 100;
	final static int BAR_WIDTH = 140;
	final static int TEXT_X = 250;

	final static String[] LABEL = {
		"round trip time",
		"average r.t. time",
		"lag (st. dev.)",
		"inc. pack loss in",
		"inc. pack loss out",
		"tot. pack loss in",
		"tot. pack loss out"
	};

	final static int[] MAX = {
		500, 500, 100, 50, 50, 50, 50
	};

	final static int[] MAX_GREEN = {
		100, 100, 20, 10, 10, 5, 5
	};

	final static int[] MAX_YELLOW = { 
		200, 200, 50, 20, 20, 10, 10,
	};

	PingStats stats;
	Image offscreen;

	/** PlayerListPanel */
	public PingStatsPanel(String name, PingStats stats) {
		super(name);
		this.stats = stats;
	}

	/** setup */
	void setup() {
		super.setup();
		offscreen = createImage(view_size.width - 5, getFontMetrics(variable_font).getHeight());
	}

	
	/** paint */
	public void paint(Graphics g) {
		update(g);
		paintBorder(g);
	}

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(variable_font);
		FontMetrics metrics = og.getFontMetrics();

		int line_height = metrics.getHeight();
		int bar_height = line_height - 3;
		int y = 8;
		for(int p = 0; p < LABEL.length; ++p) {

			og.setColor(Color.black);
			og.fillRect(0, 0, view_size.width - 5, line_height);

			// draw the label
			og.setColor(Color.white);
			og.drawString(LABEL[p], BAR_X - 5 - metrics.stringWidth(LABEL[p]), metrics.getAscent());

			// get the value, and make sure it is within bounds
			int value = getPingValue(p);
			if(value < 0) {
				value = 0;
			}
			else if(value > MAX[p]) {
				value = MAX[p];
			}

			// draw the bar
			int bar_width = (BAR_WIDTH * value) / MAX[p];
			if(value > MAX_YELLOW[p]) {
				og.setColor(Color.red);
			}
			else if(value > MAX_GREEN[p]) {
				og.setColor(Color.yellow);
			}
			else {
				og.setColor(Color.green);
			}
			og.fillRect(BAR_X, 1, bar_width, bar_height);
			og.setColor(Color.white);
			og.drawRect(BAR_X, 1, BAR_WIDTH, bar_height);

			// draw the text
			og.drawString("(" + value + ')', TEXT_X, metrics.getAscent());

			g.drawImage(offscreen, 5, y, null);

			y += line_height;
		}
	}

	/** getPingValue */
	int getPingValue(int index) {
		switch(index) {
		case 0 :
			return stats.lag;
		case 1 :
			return stats.av;
		case 2 :
			return stats.sd;
		case 3 :
			return stats.iloss_sc;
		case 4 :
			return stats.iloss_cs;
		case 5 :
			return stats.tloss_sc;
		default :
			return stats.tloss_cs;
		}
	}
}