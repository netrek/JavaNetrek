package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.config.Defaults;

public class ScrollPanel extends BasePanel {
	int LINE_DELTA = 256;

	ScrollString[] lines = new ScrollString[LINE_DELTA];

	int extent = 0;
	int position = 0;
	int visible;
	
	int line_height;
	
	boolean bottom = true;

	ScrollPanel(String name) {
		super(name);
	}

	/** setup */
	void setup() {
		super.setup();
		setFont(fixed_font);
		line_height = getFontMetrics(fixed_font).getHeight() - 1;
		visible = (view_size.height - 2) / line_height;
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.isAltDown()) {
					scrollBottom();
				}
				else if(e.isMetaDown()) {
					scrollDown();
				}
				else {
					scrollUp();
				}
			}	
		});
	}

	/** addLine */
	public void addLine(ScrollString line) {
		boolean scroll = false;
		if(Defaults.max_scroll_lines > 0 && extent >= Defaults.max_scroll_lines) {
			System.arraycopy(lines, 1, lines, 0, extent - 1);
			lines[extent - 1] = line;
			if(bottom && extent > visible) {
				scroll = true;
			}
			else if(!bottom && position > 0) {
				--position;
			}
		}
		else {
			if(extent >= lines.length) {
				ScrollString[] new_lines = new ScrollString[lines.length + LINE_DELTA];
				System.arraycopy(lines, 0, new_lines, 0, lines.length);
				lines = new_lines;
			}
			lines[extent++] = line;
			if(bottom) {
				if(isVisible() && extent > visible) {
					scroll = true;
					++position;
				}
			}
		}

		if(bottom && isVisible()) {
			Graphics g = getGraphics();
			g.translate(BORDER, BORDER);
			g.setClip(0, 0, view_size.width, view_size.height);
			if(scroll) {
				g.copyArea(0, 0, view_size.width, view_size.height, 0, -line_height);
			}
			int y = (extent > visible ? visible : extent) * line_height + 1;
			g.setColor(Color.black);
			g.fillRect(0, y - g.getFontMetrics().getAscent(), view_size.width, line_height);
			g.setColor(line.color);
			g.drawString(line.string, 2, y);
			g.dispose();
		}
	}

	/** scrollUp */
	void scrollUp() {
		if(position == 0) {
			return;
		}
		position -= visible;
		if(position < 0) {
			position = 0;
		}
		bottom = position + visible >= extent;
		repaint();
	}

	/** scrollDown */
	void scrollDown() {
		if(extent <= visible) {
			return;
		}
		int maximum = extent - visible;
		if(position == maximum) {
			return;
		}
		position += visible;
		if(position > maximum) {
			position = maximum;
		}
		bottom = position + visible >= extent;
		repaint();
	}

	/** scrollBottom */
	void scrollBottom() {
		if(extent <= visible) {
			return;
		}
		int maximum = extent - visible;	
		if(position != maximum) {
			position = maximum;
			repaint();
		}
		bottom = true;
	}

	/** update */
	public void update(Graphics g) {
		g.translate(BORDER, BORDER);
		g.setColor(Color.black);
		g.fillRect(0, 0, view_size.width, view_size.height);
		for(int y = 1 + line_height, l = position; l < extent && y < view_size.height; ++l, y += line_height) {
			g.setColor(lines[l].color);
			g.drawString(lines[l].string, 2, y);
		}
	}
}