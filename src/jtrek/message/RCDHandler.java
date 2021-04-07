package jtrek.message;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.visual.*;
import jtrek.config.*;

public class RCDHandler implements MessageConstants {
	NetrekFrame view;
	Universe data;
	MacroHandler macro_handler;

	/**
	* RCDHandler
	*/
	public RCDHandler(NetrekFrame view, Universe data, MacroHandler macro_handler) {
		this.view = view;
		this.data = data;
		this.macro_handler = macro_handler;
	}

	/**
	* makeRCD
	*/
	public void makeRCD(Distress distress) {
		if(Defaults.rcds[distress.distress_type].lite == null) {
			return;
		}
		String message = macro_handler.parseMacro(distress, Defaults.rcds[distress.distress_type].lite);
		if(message.length() > 0) {
			makeLite(distress, message);
		}
	}

	/**
	* makeLite
	*/
	void makeLite(Distress distress, String message) {
		int flags = view.feature_list.features[FeatureList.BEEPLITE].arg1;
		char[] buffer = message.toCharArray();

		// first step is to substitute variables
		int bpos = 0;
		while(bpos < buffer.length - 1) {
			if(buffer[bpos] == '/') {
				char c = buffer[++bpos];
				switch(c) {
				case 'P':					// push player id into buf
				case 'G':					// push friendly player id into buf
				case 'H':					// push enemy target player id into buf
				case 'p':					// push player id into buf
				case 'g':					// push friendly player id into buf
				case 'h':					// push enemy target player id into buf
				case 'U':					// highlites enemy nearest pointer
				case 'u':					// highlites enemy nearest pointer
					Player player;
					switch(c) {
					case 'p':
						player = distress.target_player;
						break;
					case 'g':
						player = distress.target_friend;
						break;
					case 'h':
						player = distress.target_enemy;
						break;
					case 'P':
						player = distress.close_player;
						break;
					case 'G':
						player = distress.close_friend;
						break;
					default:
						player = distress.close_enemy;
						break;
					}
					view.beeplite.setFlag(flags & (Beeplite.PLAYERS_MAP | Beeplite.PLAYERS_LOCAL));
					view.beeplite.litePlayer(player);
					break;
				case 'B':						// highlites planet nearest sender
				case 'b':
					if ((flags & Beeplite.PLANETS) != 0)
						view.beeplite.litePlanet(distress.close_planet);
					break;
				case 'L':						// highlites planet nearest pointer
				case 'l':
					if ((flags & Beeplite.PLANETS) != 0)
						view.beeplite.litePlanet(distress.target_planet);
					break;
				case 'c':						// highlites sender
				case 'I':
				case 'i':
					view.beeplite.setFlag(flags & (Beeplite.PLAYERS_MAP | Beeplite.PLAYERS_LOCAL));
					view.beeplite.litePlayer(distress.sender);
					break;
				case 'M':						// highlites me
				case 'm':
					if ((flags & Beeplite.SELF) != 0) {
						view.beeplite.setFlag(Beeplite.PLAYERS_MAP | Beeplite.PLAYERS_LOCAL);
						view.beeplite.litePlayer(data.me);
					}
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
					if ((flags & Beeplite.SOUNDS) != 0) {
						view.sound_player.playSound(SoundPlayer.MESSAGE_SOUND + c - '0');
					}
					break;
				// TTS was here
				default:
					// try to continue bad macro character is skipped entirely,
					// the message will be parsed without whatever argument has
					// occurred. - jn
					view.warning.setWarning("Bad Macro character in distress!");
					System.err.println("Unrecognizable special character '/" + c + "' in distress: " + message);
					--bpos;
					break;
				}
			}
			++bpos;
		}
	}
}
