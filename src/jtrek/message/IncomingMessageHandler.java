package jtrek.message;

import java.awt.*;
import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.config.*;
import jtrek.data.*;
import jtrek.visual.*;

public class IncomingMessageHandler implements MessageConstants {
	final static int NEW_TAKE = TEAM + TAKE + VALID;
	final static int NEW_DESTROY = TEAM + DEST + VALID;
	final static int NEW_KILL = ALL + KILL + VALID;
	final static int NEW_BOMB = TEAM + BOMB + VALID;
	final static int NEW_TEAM = TEAM + VALID;
	final static int NEW_CONQ = ALL + CONQ + VALID;

	NetrekFrame view;
	Universe data;
	MacroHandler macro_handler;
	RCDHandler rcd_handler;

	public IncomingMessageHandler(NetrekFrame view, Universe data, Communications comm) {
		this.view = view;
		this.data = data;
		macro_handler = new MacroHandler(view, data, comm);
		rcd_handler = new RCDHandler(view, data, macro_handler);
	}
 
	/** handleRCM */
	public void handleRCM(int flags, int to, int type, Player sender, Player target_player, int armies, 
		int damage, int shields, Planet target_planet, int wtemp) {
		Distress distress = new Distress(sender, target_player, armies, 
			damage, shields, target_planet, wtemp);
		String message = macro_handler.parseMacro(distress, Defaults.rcms[type].macro);
		newMessage(message, flags, 255, to);
	}

	/** newMessage */
	public void newMessage(String message, int flags, int from, int to) {	
		Color color = Color.lightGray;
		if(from >= 0 && from < data.players.length) {
			color = data.players[from].getColor();
		}

		// new message check was here.  new message is always on
		if(flags == (CONFIG + INDIV + VALID) && from == 255) {
			view.feature_list.checkFeature(message);
			return;
		}

		// A new type distress/macro call came in. parse it appropriately
		if (flags == (TEAM | DISTR | VALID)) {
			Distress distress = new Distress(data, message, from);
			message = macro_handler.parseMacro(distress);
			if(message.length() < 0) {
				return;
			}
			if(Defaults.use_lite) {
				rcd_handler.makeRCD(distress);
			}
			flags ^= DISTR;
		}

		ScrollString ss = new ScrollString(message, color);

		if(flags == NEW_CONQ) {
			// output conquer stuff to stdout in addition to message window
			System.out.println(message);
			if(message.indexOf("kill") != -1) {
				System.out.println("NOTE: The server here does not properly set message flags");
				System.out.println("You should probably pester the server god to update....");
			}
		}

		if(flags == NEW_TEAM || flags == NEW_TAKE || flags == NEW_DESTROY) {
			view.review_team.addLine(ss);
			if(!Defaults.report_team_in_review) {
				return;
			}
		}
		else if(flags == NEW_KILL || flags == NEW_BOMB) {
			if(!Defaults.report_kills) {
				return;
			}			
			view.review_kill.addLine(ss);
			if(!Defaults.report_kills_in_review) {
				return;
			}
		}
		else if((flags & INDIV) != 0) {
			view.review_your.addLine(ss);
			if(!Defaults.report_ind_in_review) {
				return;
			}		
		}
		else {
			view.review_all.addLine(ss);
			if(!Defaults.report_all_in_review) {
				return;
			}
		}
		view.review.addLine(ss);
	}
}