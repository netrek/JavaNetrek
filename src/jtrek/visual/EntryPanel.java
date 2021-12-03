package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.comm.*;

public class EntryPanel extends BasePanel implements Copyright {
	final static int TITLE_HEIGHT = 50;

	Universe data;
	InfoPanel motd;
	TeamEntryPanel[] team_panels;
	EntryClockPanel clock_panel;
	int team_mask = 15;
	boolean resetting_stats = false;

	public EntryPanel(String name, NetrekFrame view, InfoPanel motd) {
		super(name);
		this.motd = motd;
		setLayout(null);
		add(motd);
		clock_panel = new EntryClockPanel("quit", view);
		add(clock_panel);
		team_panels = new TeamEntryPanel[4];
		for(int t = 0; t < 4; ++t) {
			team_panels[t] = new TeamEntryPanel(Team.TEAMS[t + 1].abbrev.toLowerCase(), view.data, view.comm, t + 1, clock_panel);
			add(team_panels[t]);
		}
	}
	
	/** setup */
	void setup() {
		super.setup();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		motd.setBounds(0, TITLE_HEIGHT, width, height - TITLE_HEIGHT - TeamEntryPanel.SIZE - BORDER);
		x = 4;
		int s = TeamEntryPanel.SIZE_PLUS_BORDERS;
		for(int t = 0; t < 4; ++t) {
			team_panels[t].setBounds(x, height - s, s, s);
			x += TeamEntryPanel.SIZE;
		}
		clock_panel.setBounds(x, height - s, s, s);		
	}

	public void setTeamMask(int mask) {
		team_mask = mask;
		for(int t = 0; t < 4; ++t) {
			boolean open = ((Team.TEAMS[t + 1].bit & mask) != 0);
			if(open != team_panels[t].isEnabled()) {
				team_panels[t].setEnabled(open);
				team_panels[t].repaint();
			}
		}
	}

	public void redraw() {
		clock_panel.repaint();
		for(int t = 0; t < 4; ++t) {
			team_panels[t].repaint();
		}
	}

	public void paint(Graphics g) {
		g.setFont(big_font);
		g.setColor(Color.white);
		int y = g.getFontMetrics().getHeight() + 30;
		g.drawString(VERSION + "               by " + AUTHORS[0], 20, y);
		g.setFont(variable_font);
		y += g.getFontMetrics().getHeight() + 3;
		g.drawString("Comments & bug reports to: " + SUPPORT, 20, y);
		y += g.getFontMetrics().getHeight();
		g.setColor(Color.gray);
		g.drawString("Please include the word \"JTrek\" somewhere in the subject!", 20, y);
		paintBorder(g);
	}

	public void setVisible(boolean visible) {
		clock_panel.resetTimer();
		super.setVisible(visible);
		if(visible) {
			motd.repaint();
		}
	}
	
	public void resetTimer() {
		if (clock_panel.timer_running) {
			clock_panel.resetTimer();
		}
	}

}
