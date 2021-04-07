package jtrek.message;

import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.data.*;
import jtrek.visual.*;

public class SendMessage {
	NetrekFrame view;
	Universe data;
	Communications comm;

	String last_message = "";

	/** Constructor */
	public SendMessage(NetrekFrame view, Universe data, Communications comm) {
		this.view = view;
		this.data = data;
		this.comm = comm;
	}

	/** postMessage */
	public void sendMessage(String message, char address) {
		int recip;
		int group;
		switch (address) {
		case 'T':
			recip = data.me.team.bit;
			group = MessageConstants.TEAM;
			break;
		case 'A':
			recip = 0;
			group = MessageConstants.ALL;
			break;
		case 'F':
			recip = Team.TEAMS[Team.FED].bit;
			group = MessageConstants.TEAM;
			break;
		case 'R':
			recip = Team.TEAMS[Team.ROM].bit;
			group = MessageConstants.TEAM;
			break;
		case 'K':
			recip = Team.TEAMS[Team.KLI].bit;
			group = MessageConstants.TEAM;
			break;
		case 'O':
			recip = Team.TEAMS[Team.ORI].bit;
			group = MessageConstants.TEAM;
			break;
		case 'G':
			recip = 255;
			group = MessageConstants.GOD;
			break;
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			group = MessageConstants.INDIV;
			recip = address - '0';
			break;
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			recip = address - 'a' + 10;
			group = MessageConstants.INDIV;
			break;
		default:
			view.warning.setWarning("Not legal recipient");
			return;
		}
		if(group == MessageConstants.INDIV && data.players[recip].status == Player.FREE) {
			view.warning.setWarning("That player left the game. message not sent.");
			return;
		}
		comm.sendMessage(message, group, recip);

		// it won't be echoed by the server, show it here
		if(group == MessageConstants.TEAM && recip != data.me.team.bit) {
			message = Team.TEAM_REMAP[recip].abbrev + ' ' + message;
		}
		else if(group == MessageConstants.INDIV && recip != data.me.no) {
			message = data.players[recip].mapchars + "  " + message;
		}
		else if(recip == 255) {
			message = "GOD " + message;
		}
		else {
			last_message = message;
			return;
		}	
		last_message = ' ' + data.me.mapchars + "->" + message;
		view.dmessage.newMessage(last_message, group, data.me.no, recip);
	}
}
