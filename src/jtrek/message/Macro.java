package jtrek.message;

import java.util.*;
import jtrek.Copyright;
import jtrek.config.*;

public class Macro {
	public final static char ME			= 0;
	public final static char PLAYER		= 1;
	public final static char TEAM		= 2;
	public final static char FRIEND		= 3;
	public final static char ENEMY		= 4;

	public final static int NBTM		= 0;
	public final static int NEWM		= 1;
	public final static int NEWMSPEC	= 2;
	public final static int NEWMOUSE	= 3;

	public int type;
	public char key;
	public char who;
	public String macro;

	/** Constructor */
	public Macro(String name, String macro) throws Exception {
		this.macro = macro;

		int bpos  = 0;
		char[] buffer = name.substring(4).toCharArray();

		// determine what key is supposed to be pressed to activate the macro
		if(buffer[0] == '?') {
			throw new Exception("Defaults: Cannot use '?' for a macro");
		}
		else if(buffer.length >= 2 && buffer[0] == '^' && buffer[1] != '.') {
			key = (char)(buffer[1] + 128);
			bpos += 2;
		}
		else {
			key = buffer[0];
			bpos += 1;
		}

		// find who the macro is supposed to goto, and what type it is
		if(buffer.length > (bpos + 1) && buffer[bpos] == '.') {
			++bpos;
			if(buffer.length > (bpos + 1) && buffer[bpos] == '%') {
				switch(buffer[bpos + 1]) {
				case 'u' :
				case 'U' :
				case 'p' :
					who = PLAYER;
					break;
				case 't':
				case 'z':
				case 'Z':
					who = TEAM;
					break;
				case 'g':
					who = FRIEND;
					break;
				case 'h':
					who = ENEMY;
					break;
				default:
					who = ME;
					break;
				}
				type = NEWMOUSE;
			}			
			else {
				who = buffer[bpos];
				type = NEWMSPEC;
			}
		}
		else {
			who = '\0';
			type = NEWM;
		}
	}

	public String getKey() {
		StringBuffer buffer = new StringBuffer();
		if(key > 128) {
			buffer.append('^');
			buffer.append((char)(key - 128));
		}
		else {
			buffer.append(key);
		}
		if(type != NEWM) {
			buffer.append('.');
			if(type == NEWMSPEC) {
				buffer.append(who);
			}
			else if (type == NEWMOUSE) {
				buffer.append('%');
				char c;
				switch(who) {
				case PLAYER :
					c = 'u';
					break;
				case TEAM :
					c = 't';
					break;
				case FRIEND :
					c = 'g';
					break;
				case ENEMY :
					c = 'h';
					break;
				default:
					c = 'i';
				}
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
}
