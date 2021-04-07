package jtrek.message;

import java.util.*;
import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.config.*;
import jtrek.data.*;
import jtrek.util.*;
import jtrek.visual.*;

public class MacroHandler implements MessageConstants {
	final static int MAX_MACRO_LENGTH = 85;

	NetrekFrame view;
	Universe data;
	Communications comm;
	
	Macro need_recip = null;

	char[] buffer = new char[10 * MAX_MACRO_LENGTH];
	int line_length = 0;

	/** MacroHandler */
	public MacroHandler(NetrekFrame view, Universe data, Communications comm) {
		this.view = view;
		this.data = data;
		this.comm = comm;
	}

	/** handleMacro */
	public boolean handleMacro(char key) {
		if(need_recip != null) {
			sendMacro(need_recip, key);
			need_recip = null;
			return true;
		}
		Object macro = Defaults.macros.get(new Character(key));
		if(macro == null) {
			for(int r = 0; r < Defaults.rcds.length; ++r) {
				if(key == Defaults.rcds[r].key) {
					view.warning.clearWarning();
					sendDistress(r);
					return true;
				}
			}
			if(key > 128) {
				view.warning.setWarning("Macro 'control-" + ((char)(key - 128)) + "' is not defined.");
			}
			else {
				view.warning.setWarning("Macro '" + key + "' is not defined.");
			}
			return true;
		}
		return executeMacro((Macro)macro);
	}

	/** handleSingleMacro */
	public boolean handleSingleMacro(char key) {
		Object macro = Defaults.single_macros.get(new Character(key));
		if(macro == null) {
			return true; 
		}
		return executeMacro((Macro)macro);
	}


	/** executeMacro */
	protected boolean executeMacro(Macro macro) {
		if(macro.type != Macro.NBTM && view.feature_list.features[FeatureList.NEW_MACRO].value != Feature.ON) {
			view.warning.setWarning("NEWMACROs not allowed at this server!!");
			return true;
		}
		if(macro.type == Macro.NEWM) {
			need_recip = macro;
			view.warning.setWarning("Who shall I send the macro too?");
			return false;
		}
		sendMacro(macro);
		return true;
	}

	/** sendMacro */
	void sendMacro(Macro macro) {
		sendMacro(macro, macro.who);
	}

	/** sendMacro */
	void sendMacro(Macro macro, char who) {
		switch(macro.type) {
		case Macro.NBTM:
		case Macro.NEWMSPEC:
		case Macro.NEWM:
			break;
		case Macro.NEWMOUSE:
			int target_type ;
			switch(macro.who) {
			case Macro.FRIEND:
			case Macro.ENEMY:
			case Macro.PLAYER:
				target_type = Universe.TARG_PLAYER | Universe.TARG_CLOAK;
				if(macro.who == Macro.ENEMY) {
					target_type |= Universe.TARG_ENEMY;
				}
				else if(macro.who == Macro.FRIEND) {
					target_type |= Universe.TARG_FRIEND;
				}
				Player player = (Player)view.getTarget(target_type);
				who = player.getLetter();
				break;
			case Macro.TEAM:
				Object target = view.getTarget(Universe.TARG_PLAYER | Universe.TARG_PLANET);
				if(target instanceof Planet) {
					who = ((Planet)target).owner.letter;
				}
				else {
					who = ((Player)target).team.letter;
				}
				break;
			default:
				who = data.me.getLetter();
				break;
			}
			break;
		}

		String message = (macro.type != Macro.NBTM) ? 
			parseMacro(new Distress(view, data, 0), macro.macro) : 
			macro.macro;

		StringTokenizer tok = new StringTokenizer(message, "\n");
		while(tok.hasMoreTokens()) {			
			view.smessage.sendMessage(tok.nextToken(), who);
		}
		view.warning.clearWarning();
	}

	/** sendDistress */
	public void sendDistress(int type) {
		Distress distress = new Distress(view, data, type);

		//String message = parseMacro(distress, Defaults.rcds[type].macro);
		comm.sendMessage(distress.toMessage(), TEAM | DISTR, data.me.team.bit);
	}

	/** parseMacro */
	public String parseMacro(Distress distress) {
		return parseMacro(distress, Defaults.rcds[distress.distress_type].macro);
	}

	/** parseMacro */
	String parseMacro(Distress distress, String macro) {
		if(macro.length() < 1) {
			return "";
		}
		
		line_length = 0;
		prepareInsert(macro.length());
		System.arraycopy(macro.toCharArray(), 0, buffer, 0, macro.length());	
		// first step is to substitute variables
		parseVariables(distress);

		// second step is to evaluate tests
		parseTests();

		// third step is to include conditional text
		parseConditionals();

		// fourth step is to remove all the rest of the control characters
		parseRemaining();

		return new String(buffer, 0, line_length);
	}

	/** parseVariables */
	void parseVariables(Distress distress) {
		String string;
		int bpos = 0;
		while(bpos < line_length - 1) {
			if(buffer[bpos] == '%') {
				switch(buffer[bpos + 1]) {
				case ' ':
					string = " ";
					break;
				case 'O':                 // push a 3 character team name into buf
					string = distress.sender.team.abbrev;
					break;
				case 'o':                 // push a 3 character team name into buf
					string = distress.sender.team.abbrev.toLowerCase();
					break;
				case 'a':                 // push army number into buf
					string = String.valueOf(distress.armies);
					break;
				case 'd':                 // push damage into buf	
					string = String.valueOf(distress.damage);
					break;
				case 's':                 // push shields into buf
					string = String.valueOf(distress.shields);
					break;
				case 'f':                 // push fuel into buf 
					string = String.valueOf(distress.fuel_percentage);
					break;
				case 'w':                 // push wtemp into buf
					string = String.valueOf(distress.wtemp);
					break;
				case 'e':                 // push etemp into buf
					string = String.valueOf(distress.etemp);
					break;
				case 'P':                 // push player id into buf
					string = String.valueOf(distress.close_player.getLetter());
					break;
				case 'G':                 // push friendly player id into buf
					string = String.valueOf(distress.close_friend.getLetter());
					break;
				case 'H':                 // push enemy target player id into buf
					string = String.valueOf(distress.close_enemy.getLetter());
					break;
				case 'p':                 // push player id into buf
					string = String.valueOf(distress.target_player.getLetter());
					break;
				case 'g':                 // push friendly player id into buf
					string = String.valueOf(distress.target_friend.getLetter());
					break;
				case 'h':                 // push enemy target player id into buf
					string = String.valueOf(distress.target_enemy.getLetter());
					break;
				case 'n':                 // push planet armies into buf
					string = String.valueOf(distress.target_planet.armies);				
					break;
				case 'B':
					string = distress.close_planet.name.toUpperCase();
					if(string.length() > 3) {
						string = string.substring(0, 3);
					}
					break;
				case 'b':                 // push planet into buf
					string = distress.close_planet.name;
					if(string.length() > 3) {
						string = string.substring(0, 3);
					}
					break;
				case 'L':
					string = distress.target_planet.name.toUpperCase();
					if(string.length() > 3) {
						string = string.substring(0, 3);
					}
					break;
				case 'l':                 // push planet into buf
					string = distress.target_planet.name;
					if(string.length() > 3) {
						string = string.substring(0, 3);
					}
					break;
				case 'N':				// push planet into buf
					string = distress.target_planet.name;
					break;
				case 'Z':                 // push a 3 character team name into buf
					string = distress.target_planet.owner.abbrev.toUpperCase();
					if(string.length() > 3) {
						string = string.substring(0, 3);
					}
					break;
				case 'z':                 // push a 3 character team name into buf
					string = distress.target_planet.owner.abbrev;
					if(string.length() > 3) {
						string = string.substring(0, 3);
					}
					break;
				case 't':				// push a team character into buf
					string = "" + distress.target_planet.owner.letter;					
					break;
				case 'T':				// push my team into buf
					string = "" + distress.sender.team.letter;					
					break;
				case 'r':				// push target team letter into buf
					string = "" + distress.target_player.team.letter;
					break;
				case 'c':				// push my id char into buf
					string = distress.sender.mapchars.substring(1);				
					break;
				case 'W':				// push WTEMP flag into buf
					if(distress.distress_type == DistressConstants.RCM) {
						string = RCM.WHY_DEAD[distress.wtemp];
					}
					else {
						string = distress.wtemp_flag ? "1" : "0";
					}
					break;
				case 'E':				// push ETEMP flag into buf
					string = distress.etemp_flag ? "1" : "0";	
					break;
				case 'K':
					string = BufferUtils.floatTo32String((float)distress.target_enemy.calculateWins() / 100f);
					break;
				case 'k':
					if(distress.distress_type == DistressConstants.RCM) {
						string = String.valueOf(distress.damage) + '.' + String.valueOf(distress.shields);
					}
					else {
						string = BufferUtils.floatTo32String((float)distress.sender.calculateWins() / 100f);
					}
					break;
				case 'U':                 // push player name into buf 
					string = distress.target_player.name.toUpperCase();
					break;
				case 'u':                 // push player name into buf 				
					string = distress.target_player.name;
					break;
				case 'I':                 // my player name into buf 
					string = distress.sender.name.toUpperCase();
					break;
				case 'i':                 // my player name into buf 
					string = distress.sender.name;
					break;
				case 'v' :
					string = String.valueOf(comm.ping_stats.av);
					break;
				case 'V' :
					string = String.valueOf(comm.ping_stats.sd);
					break;
				case 'y' :
					string = String.valueOf((2 * comm.ping_stats.tloss_sc + comm.ping_stats.tloss_cs) / 3);
					break;
				case 'M' :					// put capitalized last message into buf
					string = view.smessage.last_message.toUpperCase();
					break;
				case 'm' :					// put the last message send into buf
					string = view.smessage.last_message;
					break;
				case 'S' :					// push ship type into buf 
					string = new String(distress.sender.ship.abbrev);
					break;
				case '*' :					// push %* into buf 
					string = "%*";
					break;
				case '}' :                  // push %} into buf 
					string = "%}";
					break;
				case '{' :                  // push %{ into buf 
					string = "%{";
					break;
				case '!' :					// push %! into buf 
					string = "%!";
					break;
				case '?' :					// push %? into buf 
					string = "%?";
					break;
				case '%' :					// push %% into buf 
					string = "%%";
					break;
				default:				
					view.warning.setWarning("Bad Macro character in distress!");
					System.err.println("Defaults: Unrecognizable special character in macro: '%" + buffer[bpos + 1] + "'.");
					string = "%%" + buffer[bpos + 1];
				}
				char[] replace = string.toCharArray();
				insertOver(bpos, bpos + 1, replace);
				bpos += replace.length;
			}
			else {
				++bpos;
			}
		}
	}

	/** parseTests */
	void parseTests() {
		for(int bpos = 0; bpos < line_length - 1; ++bpos) {
			if(buffer[bpos] == '%') {
				switch(buffer[bpos + 1]) {
				case '*' :
				case '%' :
				case '{' :
				case '}' :
				case '!' :
					++bpos;
					break;
				case '?' :
					// solve the conditional
					evaluateTest(bpos);
					break;
				default :
					view.warning.setWarning("Bad character in Macro!");
					System.err.println("Defaults: Unrecognizable special character in RCD (testMacro): '" + buffer[bpos - 1]);
					// remove the bad tokens
					deleteChars(bpos, bpos + 1);
					break;
				}
			}
		}	
	}

	/** evaluateTest */
	void evaluateTest(int bpos) {
		int op_pos = bpos + 2;
		while(buffer[op_pos] != '<' && buffer[op_pos] != '>' && buffer[op_pos] != '=') {
			if(op_pos >= line_length) {
				System.err.println("operation character (<,>,=) not found.");
				// $$$ MIGHT WANT TO DO SOMETHING SMART HERE!!!
				return;
			}
			op_pos++;
		}
		char operation = buffer[op_pos];

		int end = op_pos;
		while(++end < line_length - 1) {
			if(buffer[end] == '%' && (buffer[end + 1] == '?' || buffer[end + 1] == '{')) {
				break;
			}
		}

		String left = new String(buffer, bpos + 2, op_pos - bpos - 2);
		String right = new String(buffer, op_pos + 1, end - op_pos - 1);	
		int compare = left.compareTo(right);
		switch(operation) {
		case '=' :					// character by character equality
			buffer[bpos] = (compare == 0) ? '1' : '0';
			break;
		case '<' :
			buffer[bpos] = (compare < 0) ? '1' : '0';
			break;
		case '>' :
			buffer[bpos] = (compare > 0) ? '1' : '0';
			break;
		default :
			buffer[bpos] = '1';
			view.warning.setWarning("Bad operation in Macro!");
			System.err.println("Defaults: Unrecognizable operation in macro (solveTest): '" + operation + "'.");
		}
		deleteChars(bpos + 1, end - 1);
	}

	/** parseConditionals */
	void parseConditionals() {
		for(int bpos = 1; bpos < line_length - 1;) {
			if(buffer[bpos] == '%' && buffer[bpos + 1] == '{') {
				char c = buffer[bpos - 1];
				if(c == '0' || c == '1') {
					deleteChars(bpos - 1, bpos + 1);
					bpos = evaluateConditionalBlock(bpos - 1, c == '1');
					continue;
				}
			} 
			++bpos;		
		}
	}

	/** evaluateConditionalBlock */
	int evaluateConditionalBlock(int bpos, boolean include) {
		int remove_start = bpos;
		while(bpos < line_length - 1) {
			if(buffer[bpos] == '%') {
				switch(buffer[bpos + 1]) {
				case '}' :					// done with this conditional, return
					if(!include) {
						deleteChars(remove_start, bpos + 1);
						return remove_start;
					}
					deleteChars(bpos, bpos + 1);
					return bpos;
				case '{' :					// handle new conditional
					if(include) {
						char c = buffer[bpos - 1];
						if(c == '0' || c == '1') {
							deleteChars(bpos - 1, bpos + 1);
							bpos = evaluateConditionalBlock(bpos - 1, c == '1');
							continue;
						}
					}
					bpos += 2;
					break;
				case '!' :					// handle not indicator
					if(include) {
						remove_start = bpos;
						++bpos;
					}
					else {
						deleteChars(remove_start, bpos + 1);
						bpos = remove_start;
					}
					include = !include;
					break;
				default :
					++bpos;
				}
			}
			else {
				++bpos;
			}
		}
		if(!include) {
			deleteChars(remove_start, line_length - 1);			
		}
		return line_length;	
	}

	/** parseRemaining */
	void parseRemaining() {
		for(int bpos = 0; bpos < line_length; ++bpos) {
			if(buffer[bpos] == '%') {
				deleteChar(bpos);
			}
		}
	}

	/** prepareInsert */
	protected void prepareInsert(int count) {		
		line_length += count;
		if(line_length >= buffer.length) {
			int new_size = buffer.length + MAX_MACRO_LENGTH;
			char[] new_buffer = new char[new_size];
			System.arraycopy(buffer, 0, new_buffer, 0, buffer.length);
			buffer = new_buffer;
		}		
	}

	/** deleteChar */
	protected void deleteChar(int pos) {
		System.arraycopy(buffer, pos + 1, buffer, pos, line_length - pos);	
		--line_length;
	}

	/** deleteChars */
	protected void deleteChars(int start, int end) {
		if(end >= line_length) {
			line_length = start;
		}
 		else {
			System.arraycopy(buffer, end + 1, buffer, start, line_length - end);
			line_length -= end - start + 1;
		}
	}

	/** insertOver */
	protected void insertOver(int start, int end, char[] other) {	
		int difference = other.length - (end - start + 1);
		if(difference > 0) {
			prepareInsert(difference);
			System.arraycopy(buffer, end, buffer, end + difference, line_length - end);		
		}
		if(difference < 0) {
			deleteChars(end + difference + 1, end);
		}	
		System.arraycopy(other, 0, buffer, start, other.length);					
	}

}
