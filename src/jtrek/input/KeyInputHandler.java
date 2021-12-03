package jtrek.input;

import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.data.*;
import jtrek.visual.*;
import jtrek.config.*;
import jtrek.message.*;
import jtrek.util.Rotate;
import java.awt.event.*;
import java.awt.*;

public class KeyInputHandler extends KeyAdapter {
	final static int NORMAL_MODE	= 0;
	final static int MESSAGE_MODE	= 1;
	final static int MACRO_MODE		= 2;
	final static int REFIT_MODE		= 3;
	final static int ENTRY_MODE		= 4;

	final static int getKey(KeyEvent e) {
		int key_char = e.getKeyChar();
		if(e.isControlDown()) {
			if(key_char <= 26) {
				if(e.isShiftDown()) {
					return key_char + 192;
				}
				return key_char + 224;
			}
			return key_char + 128;
		}
		return key_char;
	}

	Universe data;
	Communications comm;
	NetrekFrame view;
	MacroHandler macro_handler;
	int mode = NORMAL_MODE;

	// the last time detonate enemy torps was requested
	long last_det_time = 0;
 
	/** Constructor */
	public KeyInputHandler(NetrekFrame view, Universe data, Communications comm) {
		this.view = view;
		this.data = data;
		this.comm = comm;
		macro_handler = new MacroHandler(view, data, comm);
	}
	
	public void gotoEntryMode() {		
		if (mode == MESSAGE_MODE) {
			view.setWarpCursor(false);
		}
		mode = ENTRY_MODE;
	}
	
	public void entryModeComplete() {
		mode = NORMAL_MODE;
	}

	/** keyPressed */
	public void keyPressed(KeyEvent e) {
		boolean defined = (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED);
		switch(mode) {
		case MESSAGE_MODE:
			if(defined) {
				view.message.handleKey(e);
				if(!view.message.isActive()) {
					mode = NORMAL_MODE;
					view.setWarpCursor(false);
				}
			}			
			break;
		case MACRO_MODE:
			if(defined) {
				if(e.getKeyChar() == '?') {
					view.macro_win.setVisible(true);
					mode = NORMAL_MODE;
				}
				else {
					mode = macro_handler.handleMacro((char)getKey(e)) ? NORMAL_MODE : MACRO_MODE;
				}
			}
			break;
		case REFIT_MODE:
			if(defined) {
				mode = NORMAL_MODE;
				handleRefit(e.getKeyChar());				
			}
			break;
		case ENTRY_MODE:
			view.entry.resetTimer();
			// fall through
		default :
			if(XFocusPanel.xfocus != null && XFocusPanel.xfocus.handleKey(e)) {
				return;
			}
			if(defined) {
				handleKey(getKey(e));
			}
		}
	}

	/** handleKey */
	public void handleKey(int key) {
		Player player;
		int target_type = 0;
		key = Keymap.keymap[key];
		switch(key) {
		case ' ' :
			// ' ' = clear special windows
			view.information.setVisible(false);
			view.planet_list.setVisible(false);
			break;
		case '!':
			comm.sendSpeedReq(11);
			break;
		case '#':			
			// half speed
			comm.sendSpeedReq(data.me.ship.maxspeed / 2);
			break;
		case '$':
			// turn off tractor/pressor.
			comm.sendTractorReq(false, data.me.no);
			break;
		case '%':
			// maximum speed
			comm.sendSpeedReq(99);
			break;
		case '&':
			Defaults.loadProperties();
			break;
			
		case 'r':
			mode = REFIT_MODE;
			view.warning.setWarning("s=scout, d=destroyer, c=cruiser, b=battleship, a=assault, g=galaxy, o=starbase");
			break;	
		case 'X':
			mode = MACRO_MODE;
			view.warning.setWarning("In Macro Mode");
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
			comm.sendSpeedReq((int)(key - '0'));
			break;
		case ')':
			comm.sendSpeedReq(10);
			break;
		case '@':
			comm.sendSpeedReq(12);		
			break;
		case '>':
			// increase speed
			comm.sendSpeedReq(data.me.speed + 1);
			break;
		case '<':
			// decrease speed
			comm.sendSpeedReq(data.me.speed - 1);
			break;	
		case 'c':                         
			// c = cloak
			comm.sendCloakReq((data.me.flags & Player.CLOAK) == 0);
			break;
		case '{':
			comm.sendCloakReq(true);
			break;
		case '}':
			comm.sendCloakReq(false);
			break;
		case 'd' :                         
			// d = detonate other torps
			long time = System.currentTimeMillis();
			if (time - last_det_time >= 100) {
				last_det_time = time;
				comm.sendDetonateReq();
			}
			break;
		case 'D' :                         
			// D = detonate my torps
			comm.detonateMyTorps();
			break;
		case '[' :
			// ] = turn shields off
			if ((data.me.flags & Player.SHIELD) != 0) {
				comm.sendShieldReq(false);
			}
			break;
		case ']' :
			// ] = turn shields on
			if ((data.me.flags & Player.SHIELD) == 0) {
				comm.sendShieldReq(true);
			}
			break;
		case 's' :
		case 'u' :                         
			// s, u = toggle shields
			comm.sendShieldReq(((data.me.flags & Player.SHIELD) == 0));	
			break;
		case 'b':                         
			// b = bomb planet
			if((data.me.flags & Player.BOMB) == 0) {
				comm.sendBombReq(true);   					
			}
			break;
		case 'z':                         
			// z = beam up
			if((data.me.flags & Player.BEAMUP) == 0) {
				comm.sendBeamReq(true);
			}
			break;
		case 'x':                        
			// x = beam down
			if((data.me.flags & Player.BEAMDOWN) == 0) {
				comm.sendBeamReq(false);
			}
			break;
		case 'R':                         
			// R = Go into repair mode
			comm.sendRepairReq(true);
			break;
		case 'e': 
			// toggle docking permission
			comm.sendDockingReq(((data.me.flags & Player.DOCKOK) == 0));
			break;
		case 'o': 
			// o = dock at nearby starbase or orbit nearest planet			
			comm.sendOrbitReq(true);
			break;
		case 'm' : 
			// m = message mode
			mode = MESSAGE_MODE;
			view.sound_player.playSound(SoundPlayer.MESSAGE_SOUND);
			view.setWarpCursor(true);
			view.message.activeWarp();
			break;
		case 'M' :
			view.showMotd();
			break;
		case 'O' :
			// O = options Window
			view.options.setVisible(!view.options.isVisible());
			break;
		case 'q':
			// q = fastquit
			if(Defaults.fast_quit_ok) {
				view.quit = true;
			}
			// fall through
		case 'Q':	
			// Q = quit
			view.sound_player.playSound(SoundPlayer.SELF_DESTRUCT_SOUND);
			comm.sendQuitReq();
			break;
		case 'V':
			// change local display view type
			if(++Defaults.show_local > 3) {
				Defaults.show_local = 0;
			}
			if(view.options.isVisible()) {
				view.options.refresh();
			}
			break;
		case 'B':
			// change galactic display view type
			if(++Defaults.show_galactic > 3) {
				Defaults.show_galactic = 0;
			}
			view.galactic.redraw_all_planets = true;
			if(view.options.isVisible()) {
				view.options.refresh();
			}
			break;
		case '?':
			// ? = Redisplay all message windows
			view.review.setVisible(true);
			view.review_all.setVisible(true);
			view.review_team.setVisible(true);
			view.review_your.setVisible(true);
			view.review_kill.setVisible(true);
			view.review_phaser.setVisible(true);
			break;		
		case 'C':                         
			// C = coups
			comm.sendCoupReq();
			break;
		case '*' :
			// send in a practice robot/transwarp to base
			comm.sendPractrReq();
			break;
		case 'E':                         
			// E = send emergency call
			macro_handler.sendDistress(DistressConstants.GENERIC);
			//comm.rcd(generic, data);		
			break;
		case 'F':                         
			// F = send armies carried report
			macro_handler.sendDistress(DistressConstants.CARRYING);
			break;
		case 'L':                         
			// L = Player list
			view.player_list.setVisible(!view.player_list.isVisible());
			break;
		case 'P':
			// P = Planet list
			view.planet_list.setVisible(!view.planet_list.isVisible());					
			break;
		case 'U':
			// U = Rank list			
			view.rank_list.setVisible(!view.rank_list.isVisible());
			break;
		case 'S':                         
			// S = toggle stat mode			
			view.stats_panel.setVisible(!view.stats_panel.isVisible());
			break;
		case 'N':                         
			// N = Toggle Name mode
			if(++Defaults.name_mode > 2) {
				Defaults.name_mode = 0;
			}
			break;
		case 'h':
			// h = Map help window
			view.help.setBounds(100, 100, 700, 400);
			view.help.setVisible(!view.help.isVisible());		
			break;
		case 'w':
			// w = map war stuff
			view.war.resetState();
			view.war.setVisible(!view.war.isVisible());
			break;
		case '`':
			// short: pop up short control window
			view.short_win.setVisible(!view.short_win.isVisible());
			break;
		case '+':
			// UDP: pop up UDP control window
			view.udp_win.setVisible(!view.udp_win.isVisible());
			break;
		case '=':
			// UDP: request for full update
			comm.sendUdpReq(PacketTypes.COMM_UPDATE);
			break;	
		case '-':
			// turn on short packets
			comm.sendShortReq(PacketTypes.SPK_SALL);
			break;		
		case '/':
			// Net stat window
			view.warning.setWarning("Net stat window not yet implemented.");
			//netstat = !netstat;			
			break;
		case '\\':
			view.warning.setWarning("Not implemented.");
			// alternate player list			
			break;
		case '|':
			// sort player list
			view.warning.setWarning("Not implemented.");
			break;
		case ':':
			// Re-read the values from rc file -- only nifty values are affected
			view.warning.setWarning("Not implemented.");
			break;
		case ',':
			// map ping stats
			view.ping_panel.setVisible(!view.ping_panel.isVisible());
			break;
		case '~' :
			// show the sound control window
			view.sound_win.setVisible(!view.sound_win.isVisible());
			break;
		// ALL OF THE MOUSE DEPENDENT INPUTS
		case 'k' : 
			// k = set course		
			comm.sendDirReq(Rotate.unrotateDir(view.getCourse(), data.rotate));
			data.me.flags &= ~(Player.PLOCK | Player.PLLOCK);
			break;
		case 'p' :                         
			// p = fire phasers
			comm.sendPhaserReq(Rotate.unrotateDir(view.getCourse(), data.rotate));
			break;
		case 't' :                         
			// t = launch torps			
			comm.sendTorpReq(Rotate.unrotateDir(view.getCourse(), data.rotate));
			break;
		case 'f' :
			// f = launch plasma torpedos		
			comm.sendPlasmaReq(Rotate.unrotateDir(view.getCourse(), data.rotate));
			break;
		case 'y' :
		case 'T' :
			if ((data.me.flags & (Player.TRACT | Player.PRESS)) != 0) {
				comm.sendTractorReq(false, data.me.no);
			}
			else {
				player = (Player)view.getTarget(Universe.TARG_PLAYER);
				data.me.tractor = player.no;
				if (key == 'T') {
					comm.sendTractorReq(true, player.no);
				}
				else {
					comm.sendRepressReq(true, player.no);
				}
			}
			break;
		case '_' :
			// alternative tractor/pressor.	
			if ((data.me.flags & (Player.TRACT | Player.PRESS)) != 0) {
				comm.sendTractorReq(false, data.me.no);
			}
			player = (Player)view.getTarget(Universe.TARG_PLAYER);
			data.me.tractor = player.no;
			comm.sendTractorReq(true, player.no);
			break;
		case '^' :
			// alternative tractor/pressor.
			if ((data.me.flags & (Player.TRACT | Player.PRESS)) != 0) {
				comm.sendRepressReq(false, data.me.no);
			}		
			player = (Player)view.getTarget(Universe.TARG_PLAYER);
			data.me.tractor = player.no;
			comm.sendRepressReq(true, player.no);
			break;
		case ';' :
			// ; = lock onto planet or base
			target_type |= Universe.TARG_BASE;
		// fall through
		case 'l' :                         
			// l = lock onto
			target_type |= (Universe.TARG_PLAYER | Universe.TARG_PLANET);
			Object target = view.getTarget(target_type);
			if (target instanceof Player) {
				// It's a player
				int number = ((Player)target).no;
				comm.sendPlaylockReq(number);
				data.me.playerl = number;
			}
			else {
				// It's a planet
				int number = ((Planet)target).no;
				comm.sendPlanlockReq(number);
				data.me.planet = number;
			}
			break;
		case 'I' :
		case 'i' :
			// i = get normal information
			// I = get extended information
			if (view.information.isVisible()) {
				view.information.setVisible(false);
			}
			else if(XFocusPanel.xfocus instanceof BaseMapPanel) {
				((BaseMapPanel)XFocusPanel.xfocus).getInfo(key == 'I');
			}
			break;
		case '"':
		case '\'':
		case '(':
		case '.':
		case 'A':
		case 'G':
		case 'H':
		case 'J':
		case 'K':
		case 'W':
		case 'Y':
		case 'Z':
		case 'a':
		case 'g':
		case 'j':
		case 'n':
		case 'v':
		default :
			mode = macro_handler.handleSingleMacro((char)key) ? NORMAL_MODE : MACRO_MODE;
			break;
		}
	}

	/** handleRefit */
	void handleRefit(int key) {
		byte ship_type = Ship.CA;
		switch(key) {
		case 's' : case 'S' :
			ship_type = Ship.SC;			
			break;
		case 'd' : case 'D' :
			ship_type = Ship.DD;
			break;
		case 'c' : case 'C' :
			ship_type = Ship.CA;
			break;
		case 'b' : case 'B' :
			ship_type = Ship.BB;
			break;
		case 'g' : case 'G' :
			ship_type = Ship.GA;
			break;
		case 'o' : case 'O' :
			ship_type = Ship.SB;
			break;
		case 'a' : case 'A' :
			ship_type = Ship.AS;
			break;
		default :
			return;
		}
		comm.sendRefitReq(ship_type);
	}
}
