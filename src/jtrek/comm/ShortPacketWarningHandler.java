package jtrek.comm;

import jtrek.Copyright;
import jtrek.visual.*;
import jtrek.data.*;
import jtrek.config.*;
import jtrek.message.*;
import jtrek.util.*;

public class ShortPacketWarningHandler {
	final static byte TEXTE					= 0;
	final static byte PHASER_HIT_TEXT		= 1;
	final static byte BOMB_INEFFECTIVE		= 2;
	final static byte BOMB_TEXT				= 3;
	final static byte BEAMUP_TEXT			= 4;
	final static byte BEAMUP2_TEXT			= 5;
	final static byte BEAMUPSTARBASE_TEXT	= 6;
	final static byte BEAMDOWNSTARBASE_TEXT	= 7;
	final static byte BEAMDOWNPLANET_TEXT	= 8;
	final static byte SBREPORT				= 9;
	final static byte ONEARG_TEXT			= 10;
	final static byte BEAM_D_PLANET_TEXT	= 11;
	final static byte ARGUMENTS				= 12;
	final static byte BEAM_U_TEXT			= 13;
	final static byte LOCKPLANET_TEXT		= 14;
	final static byte LOCKPLAYER_TEXT		= 15;
	final static byte SBRANK_TEXT			= 16;
	final static byte SBDOCKREFUSE_TEXT		= 17;
	final static byte SBDOCKDENIED_TEXT		= 18;
	final static byte SBLOCKSTRANGER		= 19;
	final static byte SBLOCKMYTEAM			= 20;
	// Daemon messages
	final static byte DMKILL				= 21;
	final static byte KILLARGS				= 22;
	final static byte DMKILLP				= 23;
	final static byte DMBOMB				= 24;
	final static byte DMDEST				= 25;
	final static byte DMTAKE				= 26;
	final static byte DGHOSTKILL			= 27;
	// INL	messages
	final static byte INLDMKILLP			= 28;
	final static byte INLDMKILL				= 29;	// Because of shiptypes
	final static byte INLDRESUME			= 30;
	final static byte INLDTEXTE				= 31;
	// Variable warning stuff
	final static byte STEXTE				= 32;	// static text that the server needs to send to the client first
	final static byte SHORT_WARNING			= 33;	// like CP_S_MESSAGE
	final static byte STEXTE_STRING			= 34;
	final static byte KILLARGS2				= 35;
	final static short DINVALID				= 255;

	// DaemonMessages
	final static String[] DAEMON_TEXTS = {
		"Game is paused.  CONTINUE to continue.",
		"Game is no-longer paused!",
		"Game is paused. Captains CONTINUE to continue.",
		"Game will continue in 10 seconds",
		"Teams chosen.  Game will start in 1 minute.",
		"----------- Game will start in 1 minute -------------",
	};

	final static String[] W_TEXTS = {
		"Tractor beams haven't been invented yet.",
		"Weapons's Officer:  Cannot tractor while cloaked, sir!",
		"Weapon's Officer:  Vessel is out of range of our tractor beam.",
		"You must have one kill to throw a coup",
		"You must orbit your home planet to throw a coup",
		"You already own a planet!!!",
		"You must orbit your home planet to throw a coup",
		"Too many armies on planet to throw a coup",
		"Planet not yet ready for a coup",
		"I cannot allow that.  Pick another team",
		"Please confirm change of teams.  Select the new team again.",
		"That is an illegal ship type.  Try again.",
		"That ship hasn't been designed yet.  Try again.",
		"Your new starbase is still under construction",
		"Your team is not capable of defending such an expensive ship!",
		"Your team's stuggling economy cannot support such an expenditure!",
		"Your side already has a starbase!",
		"Plasmas haven't been invented yet.",
		"Weapon's Officer:  Captain, this ship is not armed with plasma torpedoes!",
		"Plasma torpedo launch tube has exceeded the maximum safe temperature!",
		"Our fire control system limits us to 1 live torpedo at a time captain!",
		"Our fire control system limits us to 1 live torpedo at a time captain!",
		"We don't have enough fuel to fire a plasma torpedo!",
		"We cannot fire while our vessel is undergoing repairs.",
		"We are unable to fire while in cloak, captain!",
		"Torpedo launch tubes have exceeded maximum safe temperature!",
		"Our computers limit us to having 8 live torpedos at a time captain!",
		"We don't have enough fuel to fire photon torpedos!",
		"We cannot fire while our vessel is in repair mode.",
		"We are unable to fire while in cloak, captain!",
		"We only have forward mounted cannons.",
		"Weapons Officer:  This ship is not armed with phasers, captain!",
		"Phasers have not recharged",         // 32
		"Not enough fuel for phaser",         // 33
		"Can't fire while repairing",         // 34
		"Weapons overheated",                 // 35
		"Cannot fire while cloaked",          // 36
		"Phaser missed!!!",                   // 37
		"You destroyed the plasma torpedo!",  // 38
		"Must be orbiting to bomb",           // 39
		"Can't bomb your own armies.  Have you been reading Catch-22 again?",
		"Must declare war first (no Pearl Harbor syndrome allowed here).",
		"Bomb out of T-mode?  Please verify your order to bomb.",
		"Hoser!",                             // 43
		"Must be orbiting or docked to beam up.",
		"Those aren't our men.",              // 45
		"Comm Officer: We're not authorized to beam foriegn troops on board!",
		"Must be orbiting or docked to beam down.",
		"Comm Officer: Starbase refuses permission to beam our troops over.",
		"Pausing ten seconds to re-program battle computers.",
		"You must orbit your HOME planet to apply for command reassignment!",
		"You must orbit your home planet to apply for command reassignment!",
		"Can only refit to starbase on your home planet.",
		"You must dock YOUR starbase to apply for command reassignment!",
		"Must orbit home planet or dock your starbase to apply for command reassignment!",
		"Central Command refuses to accept a ship in this condition!",
		"You must beam your armies down before moving to your new ship",
		"That ship hasn't been designed yet.",
		"Your side already has a starbase!",  // 58
		"Your team is not capable of defending such an expensive ship",
		"Your new starbase is still under construction",
		"Your team's stuggling economy cannot support such an expenditure!",
		"You are being transported to your new vessel .... ",
		"Engineering:  Energize. [ SFX: chimes ]",
		"Wait, you forgot your toothbrush!",  // 64
		"Nothing like turning in a used ship for a new one.",
		"First officer:  Oh no, not you again... we're doomed!",
		"First officer:  Uh, I'd better run diagnostics on the escape pods.",
		"Shipyard controller:  This time, *please* be more careful, okay?",
		"Weapons officer:  Not again!  This is absurd...",
		"Weapons officer:  ... the whole ship's computer is down?",
		"Weapons officer:  Just to twiddle a few bits of the ship's memory?",
		"Weapons officer:  Bah! [ bangs fist on inoperative console ]",
		"First Officer:  Easy, big guy... it's just one of those mysterious",
		"First Officer:  laws of the universe, like 'tires on the ether'.",
		"First Officer:  laws of the universe, like 'Klingon bitmaps are ugly'.",
		"First Officer:  laws of the universe, like 'all admirals have scummed'.",
		"First Officer:  laws of the universe, like 'Mucus Pig exists'.",
		"First Officer:  laws of the universe, like 'guests advance 5x faster'.",
		"Helmsman: Captain, the maximum safe speed for docking or orbiting is warp 2!",
		"Central Command regulations prohibits you from orbiting foreign planets",
		"Helmsman:  Sensors read no valid targets in range to dock or orbit sir!",
		"No more room on board for armies",   // 82
		"You notice everyone on the bridge is staring at you.",
		"Can't send in practice robot with other players in the game.",
		"Self Destruct has been canceled",    // 85
		"Be quiet",                           // 86
		"You are censured.  Message was not sent.",
		"You are ignoring that player.  Message was not sent.",
		"That player is censured.  Message was not sent.",
		"Self destruct initiated",            // 90
		"Scanners haven't been invented yet", // 91
		"WARNING: BROKEN mode is enabled",    // 92
		"Server can't do that UDP mode",      // 93
		"Server will send with TCP only",     // 94
		"Server will send with simple UDP",   // 95
		"Request for fat UDP DENIED (set to simple)",
		"Request for double UDP DENIED (set to simple)",
		"Update request DENIED (chill out!)", // 98
		"Player lock lost while player dead.",
		"Can only lock on own team.",         // 100
		"You can only warp to your own team's planets!",
		"Planet lock lost on change of ownership.",
		" Weapons officer: Finally! systems are back online!",
	};

	String[] s_texte = new String[256];

	int which_message;

	Player player;
	Planet planet;

	int arg1;
	int arg2;
	int arg3;
	int arg4;
	int karg3;
	int karg4;
	int karg5;

	byte[] buffer;
	NetrekFrame view;
	Universe data;

	String message;

	/** ShortPacketWarningHandler */
	ShortPacketWarningHandler(NetrekFrame view, Universe data, byte[] buffer) {
		this.view = view;
		this.data = data;
		this.buffer = buffer;
	}

	/** handleSWarning */
	void handleSWarning() {
		which_message = buffer[1] & 0xFF;
		arg1 = buffer[2] & 0xFF;
		arg2 = buffer[3] & 0xFF;

		switch(which_message) {
		case TEXTE :                       // damage used as tmp var
			int temp = arg1 | arg2 << 8;
			if (temp >= 0 && temp < W_TEXTS.length) {
				message = W_TEXTS[temp];
				view.warning.setWarning(message);
			}
			break;
		case PHASER_HIT_TEXT :
			player = data.players[arg1 & 0x3F];

			if((arg1 & 0x40) != 0) {
				arg2 |= 0x100;
			}
			if((arg1 & 0x80) != 0) {
				arg2 |= 0x200;
			}

			message = "Phaser burst hit " + player.getPlayerString() + " for " + arg2 + " points.";
			view.warning.setWarning(message);
			view.dmessage.newMessage(message, (MessageConstants.INDIV | MessageConstants.VALID | MessageConstants.PHASER), 255, 0);
			break;
		case BOMB_INEFFECTIVE :
			message = "Weapons Officer: Bombing is ineffective for " + arg1 + " armies.";
			view.warning.setWarning(message);
			break;
		case BOMB_TEXT :
			planet = data.planets[arg1];
			message = "Bombing " + planet.name + ".  Sensors read " + arg2 + " armies left.";
			view.warning.setWarning(message);
			break;
		case BEAMUP_TEXT :
			planet = data.planets[arg1];
			message = planet.name + ": Too few armies to beam up.";
			view.warning.setWarning(message);
			break;
		case BEAMUP2_TEXT :
			message = "Beaming up.  (" + arg1 + '/' + arg2 + ')';
			view.warning.setWarning(message);
			break;
		case BEAMUPSTARBASE_TEXT :
			player = data.players[arg1];
			message = "Starbase " + player.getPlayerString() + ": Too few armies to beam up.";
			view.warning.setWarning(message);
			break;
		case BEAMDOWNSTARBASE_TEXT :
			player = data.players[arg1];
			message = "No more armies to beam down to Starbase " + player.getPlayerString();
			view.warning.setWarning(message);
			break;
		case BEAMDOWNPLANET_TEXT :
			planet = data.planets[arg1];
			message = "No more armies to beam down to " + planet.name + '.';
			view.warning.setWarning(message);
			break;
		case SBREPORT :
			player = data.players[arg1];
			message = "Transporter Room:  Starbase " + player.getPlayerString() + " reports all troop bunkers are full!";
			view.warning.setWarning(message);
			break;
		case ONEARG_TEXT :
			switch(arg1) {
			case 0 :
				message = "Engineering:  Energizing transporters in " + arg2 + " seconds";
				break;
			case 1 :
				message = "Stand By ... Self Destruct in " + arg2 + " seconds";
				break;
			case 2 :
				message = "Helmsman:  Docking manuever completed Captain.  All moorings secured at port " + arg2 + '.';
				break;
			case 3 :
				message = "Not constructed yet. " + arg2 + " minutes required for completion";
				break;
			default :
				return;
			}
			view.warning.setWarning(message);
			break;
		case ARGUMENTS :
			arg3 = arg1;
			arg4 = arg2;
			break;
		case BEAM_D_PLANET_TEXT :
			planet = data.planets[arg1];
			message = "Beaming down.  (" + arg3 + '/' + arg4 + ") " + planet.name + " has " + arg2 + " armies left.";
			view.warning.setWarning(message);
			break;
		case BEAM_U_TEXT :
			player = data.players[arg1];
			message = "Transfering ground units.  (" + arg3 + '/' + arg4 + ") Starbase " 
				+ player.getPlayerString() + " has " + arg2 + " armies left.";
			view.warning.setWarning(message);
			break;
		case LOCKPLANET_TEXT :
			planet = data.planets[arg1];
			message = "Locking onto " + planet.name;
			view.warning.setWarning(message);
			break;
		case LOCKPLAYER_TEXT :
			player = data.players[arg1];
			message = "Locking onto " + player.getPlayerString();
			view.warning.setWarning(message);
			break;
		case SBRANK_TEXT :
			message = "You need a rank of " + Rank.RANKS[arg1].name + " or higher to command a starbase!";
			view.warning.setWarning(message);
			break;
		case SBDOCKREFUSE_TEXT :
			player = data.players[arg1];
			message = "Starbase " + player.getPlayerString() + " refusing us docking permission captain.";
			view.warning.setWarning(message);
			break;
		case SBDOCKDENIED_TEXT :
			player = data.players[arg1];
			message = "Starbase " + player.getPlayerString() 
				+ " Permission to dock denied, all ports currently occupied.";
			view.warning.setWarning(message);
			break;
		case SBLOCKSTRANGER :
			player = data.players[arg1];
			message = "Locking onto " + player.getPlayerString();
			view.warning.setWarning(message);
			break;
		case SBLOCKMYTEAM :
			player = data.players[arg1];
			message = "Locking onto " + player.getPlayerString() + " (docking is ";
			if((player.flags & Player.DOCKOK) != 0) {
				message += "enabled)";
			}
			else {
				message += "disabled)";
			}			
			view.warning.setWarning(message);
			break;
		case DMKILL:
		case INLDMKILL:
			int damage = (karg3 | (karg4 & 127) << 8);
			int armies = ((arg1 >> 6) | (arg2 & 192) >> 4);
			if((karg4 & 128) != 0) {
				armies |= 16;
			}
			view.dmessage.handleRCM(MessageConstants.ALL | MessageConstants.VALID | MessageConstants.KILL, 0, 1, data.players[arg1 & 0x3F], data.players[arg2 & 0x3F],
				armies, damage / 100, damage % 100, null, karg5);
			break;
		case KILLARGS :
			karg3 = arg1;
			karg4 = arg2;
			break;
		case KILLARGS2 :
			karg5 = arg1;
			if(karg5 < 0 || karg5 >= RCM.WHY_DEAD.length) {
				karg5 = RCM.WHY_DEAD.length - 1;
			}
			break;
		case DMKILLP : 
		case INLDMKILLP :
			player = data.players[arg1];
			planet = data.planets[arg2];
			view.dmessage.handleRCM(MessageConstants.ALL | MessageConstants.VALID | MessageConstants.KILL, 0, 2, player, player, 0, 0, 0, planet, karg5);
			break;
		case DMBOMB : 
			player = data.players[arg1];
			planet = data.planets[arg2];
			view.dmessage.handleRCM(MessageConstants.TEAM | MessageConstants.VALID | MessageConstants.BOMB, planet.owner.bit, 3, player, player, 0, arg3, 0, planet, karg5);
			break;
		case DMDEST :
			planet = data.planets[arg1];
			player = data.players[arg2];
			view.dmessage.handleRCM(MessageConstants.TEAM | MessageConstants.VALID | MessageConstants.DEST, planet.owner.bit, 4, player, player, 0, 0, 0, planet, karg5);
			break;
		case DMTAKE :
			planet = data.planets[arg1];
			player = data.players[arg2];			
			view.dmessage.handleRCM(MessageConstants.TEAM | MessageConstants.VALID | MessageConstants.TAKE, player.team.bit, 5, player, player, 0, 0, 0, planet, karg5);
			break;
		case DGHOSTKILL :
			damage = karg3 | ((karg4 & 0xFF) << 8);
			player = data.players[arg1];
			view.dmessage.handleRCM(MessageConstants.ALL | MessageConstants.VALID, 0, 6, player, player, 0, damage / 100, damage % 100, null, karg5);
			break;
		case INLDRESUME :
			message = " Game will resume in " + arg1 + " seconds";
			view.dmessage.newMessage(message, 
				(MessageConstants.ALL | MessageConstants.VALID),
				255, 0);
			break;
		case INLDTEXTE :
			if(arg1 < DAEMON_TEXTS.length) {
				message = DAEMON_TEXTS[arg1];
				view.dmessage.newMessage(message, 
					(MessageConstants.ALL | MessageConstants.VALID),
					255, 0);
			}
			break;
		case STEXTE :
			if(s_texte[arg1] != null) {
				message = s_texte[arg1];
			}
			view.warning.setWarning(message);
			break;
		case SHORT_WARNING :
			message = BufferUtils.extractString(buffer, 4, arg2 - 5);
			view.warning.setWarning(message);
			break;
		case STEXTE_STRING :
			s_texte[buffer[2] & 0xFF] = message = BufferUtils.extractString(buffer, 4, arg2 - 5);
			view.warning.setWarning(message);
			break;
		default :
			message = "Unknown short message!";
			view.warning.setWarning(message);
			System.err.println("Unknown short message: " + which_message);
			break;
		}		
	}
}
