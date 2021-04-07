package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;
import jtrek.data.*;
 
public class WarMenuItem extends MenuItem {
	final static int PEACE = 0;
	final static int HOSTILE = 1;
	final static int WAR = 2;

	final static String[] STATE_STRINGS = {
		" - Peace",
		" - Hostile",
		" - War",
	};

	Universe data;
	NetrekFrame view;
	int team_number;
	int state = 0;

	WarMenuItem(int id, Universe data, NetrekFrame view, int team_number) {
		super(id);
		this.data = data;
		this.view = view;
		this.team_number = team_number;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		repaint();
	}

	public void resetState() {
		if((data.me.swar & Team.TEAMS[team_number].bit) != 0) {
			state = WAR;			
		}
		else if((data.me.hostile & Team.TEAMS[team_number].bit) != 0) {
			state = HOSTILE;
		}
		else {
			state = PEACE;
		}
	}

	public void paint(Graphics g) {
		drawText(g, Team.TEAMS[team_number].abbrev + STATE_STRINGS[state]);
	}

	public void drawText(Graphics g, String text) {
		drawClearBox(g);
		switch(state) {
		case PEACE:
			g.setColor(Color.green);
			break;
		case HOSTILE:
			g.setColor(Color.yellow);
			break;
		case WAR:
			g.setColor(Color.red);
			break;
		}
		g.drawString(text, 6, 14);
	}

	/** 
	* mouseClicked 
	*/
	public void mousePressed(MouseEvent e) {
		if((data.me.swar & Team.TEAMS[team_number].bit) != 0) {
			view.warning.setWarning("You are already at war. Status cannot be changed.");
		}
		else if(data.me.team.no == team_number) {
			view.warning.setWarning("You can't declare war on your own team, fool.");
		}
		else {
			if(++state > 1) {
				state = 0;
			}
			repaint();
		}
	}
	
}