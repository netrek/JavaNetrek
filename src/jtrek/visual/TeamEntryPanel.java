package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.comm.*;

public class TeamEntryPanel extends OffscreenPanel {
	final static int SIZE = 99;
	final static int SIZE_PLUS_BORDERS = SIZE + TWO_BORDERS;
	final static String DIRECTORY = "/jtrek/images/intro/";
	final static String NO_ENTRY_NAME = "noentry.gif";
	final static String[] IMAGE_NAMES = {
		"fedshld.gif",
		"romshld.gif",
		"klishld.gif",
		"orishld.gif"
	};

	public static char default_ship = 'c';

	static Image noentry = null;
	Image image;

	Communications comm;

	Universe data;
	int team_number;

	EntryClockPanel clock;

	public TeamEntryPanel(String name, Universe data, Communications comm, int team_number, EntryClockPanel clock) {
		super(name);
		this.clock = clock;
		this.team_number = team_number;
		this.comm = comm;
		this.data = data;
	}

	/** setup */
	void setup() {
		super.setup();
		Toolkit kit = getToolkit();
		image = ImageLoader.loadImage(DIRECTORY + IMAGE_NAMES[team_number - 1]);
		if(noentry == null) {
			noentry = ImageLoader.loadImage(DIRECTORY + NO_ENTRY_NAME);
		}
	}

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(large_font);
		og.drawImage(image, 0, 0, this);
		og.setColor(Team.TEAMS[team_number].getColor());
		og.drawString(Integer.toString(countLivingPlayers()), 14, 80);
		if(!isEnabled()) {
			og.drawImage(noentry, 0, 0, this);
		}
		g.drawImage(offscreen, BORDER, BORDER, null);
	}

	/** countLivingPlayers */
	int countLivingPlayers() {
		Player player;
		int count = 0;
		for(int p = 0; p < data.players.length; ++p) {
			player = data.players[p];
			if(player.status == Player.ALIVE && player.team.no == team_number) {
				++count;
			}
		}
		return count;
	}

	/** mouseClicked */
	public void mousePressed(MouseEvent e) {
		selectShip(default_ship);
	}

	/** handleKey */
	public boolean handleKey(KeyEvent e) {
		selectShip(e.getKeyChar());
		return true;
	}

	/** selectShip */
	void selectShip(char ship_char) {
		byte ship_type;
		switch(ship_char) {
		case 's' : 
		case 'S' :
			ship_type = Ship.SC;
			break;
		case 'd' :
		case 'D' :
			ship_type = Ship.DD;
			break;
		case 'c' :
		case 'C' :
			ship_type = Ship.CA;
			break;
		case 'b' :
		case 'B' :
			ship_type = Ship.BB;
			break;
		case 'a' :
		case 'A' :
			ship_type = Ship.AS;
			break;
		case 'g' :
		case 'G' :
			ship_type = Ship.GA;
			break;
		case 'o' :
		case 'O' :
			ship_type = Ship.SB;
			break;
		case 'x' :
		case 'X' :
			ship_type = Ship.AT;
			break;
		default :
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		clock.stopTimer();
		comm.sendTeamReq((byte)(team_number - 1), ship_type);
	}
}