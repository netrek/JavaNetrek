package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.config.*;
import jtrek.util.*;

// $$$ ADD SBSTATS TO THIS

public class StandardPlayerList extends PlayerListPanel {
	char[] buffer = new char[240];
	String header;

	/** setup */
	void setup() {
		super.setup();
		createHeader();
	}

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(fixed_font);

		int line_height = og.getFontMetrics().getHeight() - 3;
		int y = line_height + 1;
		clearOffscreen(og);
		og.drawString(header, 5, y);

		// draw the player list
		for(int p = 0; p < data.players.length; ++p) {
			y += line_height;
			if(data.players[p].status != Player.FREE) {
				drawPlayerLine(og, data.players[p], y);
			}
		}
		g.drawImage(offscreen, BORDER, BORDER, null);
	}	

	/** createHeader */
	void createHeader() {
		// fill the buffer with spaces
		BufferUtils.fillWithSpaces(buffer);
		String string;
		int b = 0;
		int l = 0;
		try {
			for(; l < Defaults.player_list.length; ++l) {
				switch (Defaults.player_list[l]) {
				case 'n':                     // Ship Number 
					string = "No";
					break;
				case 'T':                     // Ship Type 
					string = "Ty";
					break;
				case 'R':                     // Rank 				
					string = "Rank      ";
					break;
				case 'N':                     // Name 
					string = "Name            ";
					break;
				case 'K':                     // Kills 
					string = " Kills";
					break;
				case 'l':                     // Login Name 
					string = "Login           ";
					break;
				case 'O':                     // Offense 
					string = "Offse";
					break;
				case 'W':                     // Wins 
					string = "Wins";
					break;
				case 'D':                     // Defense 
					string = "Defse";
					break;
				case 'L':                     // Losses 
					string = "Loss";
					break;
				case 'S':                     // Total Rating (stats) 
					string = "Stats";
					break;
				case 'r':                     // Ratio 
					string = "Ratio";
					break;
				case 'd':                     // Damage Inflicted (DI) 
					string = "    DI";
					break;
				case ' ':                     // White Space 
					string = " ";		
					break;
				case 'B':                     // Bombing 
					string = "Bmbng";
					break;
				case 'b':                     // Armies Bombed 
					string = "Bmbed";
					break;
				case 'P':                     // Planets 
					string = "Plnts";
					break;
				case 'p':                     // Planets Taken 
					string = "Plnts";
					break;
				case 'M':                     // Display, Host Machine 
					string = "Host Machine    ";
					break;
				case 'H':                     // Hours Played 
					string = "Hours ";
					break;
				case 'k':                     // Max Kills 
					string = "Max K";
					break;
				case 'V':                     // Kills per hour 
					string = "  KPH";
					break;
				case 'v':                     // Deaths per hour 
					string = "  DPH";
					break;
				default:
					string = "";
					System.err.println(getClass().getName() + ':' + Defaults.player_list[l] + " is not a valid option.");
					break;
				}
				string.getChars(0, string.length(), buffer, b);
				b += string.length();
				buffer[b++] = ' ';
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			char[] temp = new char[l];
			System.arraycopy(Defaults.player_list, 0, temp, 0, l);
			Defaults.player_list = temp;
			System.err.println("PlayerList String is too long, trimming to: " + new String(Defaults.player_list));
		}
		header = new String(buffer, 0, b);
	}


	/** drawPlayerLine */
	void drawPlayerLine(Graphics g, Player player, int y) {
		
		// fill the buffer with spaces
		BufferUtils.fillWithSpaces(buffer);

		int b = 0;
		int l = 0;
		try {
			for(;l < Defaults.player_list.length; ++l) {			
				switch(Defaults.player_list[l]) {
				case 'n': 
					// ship number
					buffer[b++] = (player.status == Player.ALIVE) ? player.team.letter : ' ';
					buffer[b++] = Player.PLAYER_LETTER[player.no];
					break;
				case 'T':                     
					// ship type
					if (player.status != Player.ALIVE) {
						buffer[b++] = ' ';
						buffer[b++] = ' ';
					}
					else {
						buffer[b++] = player.ship.abbrev[0];
						buffer[b++] = player.ship.abbrev[1];
					}
					break;
				case 'R':                     
					// rank
					String rank = player.stats.rank.name;
					rank.getChars(0, ((rank.length() <= 10) ? rank.length() : 10), buffer, b);
					b += 10;
					break;
				case 'N':                     
					// Name
					player.name.getChars(0, (player.name.length() <= 16) ? player.name.length() : 16, buffer, b);
					b += 16;
					break;
				case 'K':  
					// Kills
					BufferUtils.formatFloat(player.kills, buffer, b, 3, 2);
					b += 6;
					break;
				case 'l':                     
					// Login Name
					player.login.getChars(0, (player.login.length() <= 16) ? player.login.length() : 16, buffer, b);
					b += 16;
					break;
				case 'O':                     
					// Offense
					BufferUtils.formatFloat(player.stats.offenseRating(data.status), buffer, b, 2, 2);
					b += 5;
					break;
				case 'W':                     
					// Wins				
					BufferUtils.formatInteger(player.calculateWins(), buffer, b, 4);
					b += 4;
					break;
				case 'D':                     
					// Defense
					float defense = player.stats.defenseRating(data.status);
					if(defense == Float.NaN) {
						buffer[b + 4] = '-';
					}
					else {
						BufferUtils.formatFloat(defense, buffer, b, 2, 2);
					}
					b += 5;
					break;
				case 'L':                     
					// Losses
					BufferUtils.formatInteger(player.calculateLosses(), buffer, b, 4);
					b += 4;
					break;
				case 'S':                     
					// Total Rating (stats)
					float rating = player.stats.defenseRating(data.status) + player.stats.offenseRating(data.status) + player.stats.bombingRating(data.status);
					BufferUtils.formatFloat(rating, buffer, b, 2, 2);
					b += 5;
					break;
				case 'r':
					// Ratio
					float ratio = (float)player.calculateWins() / (float)Math.max(player.calculateLosses(), 1);
					BufferUtils.formatFloat(ratio, buffer, b, 2, 2);
					b += 5;
					break;
				case 'd':                     
					// Damage Inflicted (DI)
					float di = 0f;					
					if(player.stats.tticks != 0f) {
						di = (player.stats.planetRating(data.status) + player.stats.offenseRating(data.status) 
							+ player.stats.bombingRating(data.status)) * ((float)player.stats.tticks / 36000f);
					}
					BufferUtils.formatFloat(di, buffer, b, 3, 2);
					b += 6;
					break;
				case ' ': 
					// White Space
					buffer[b++] = ' ';
					// we get space by default on each
					break;
				case 'B':                     
					// Bombing
					BufferUtils.formatFloat(player.stats.bombingRating(data.status), buffer, b, 2, 2);
					b += 5;
					break;
				case 'b':                     
					// Armies Bombed
					BufferUtils.formatInteger(player.stats.tarmsbomb + player.stats.armsbomb, buffer, b, 5);			
					b += 5;
					break;
				case 'P':                     
					// Planets
					BufferUtils.formatFloat(player.stats.planetRating(data.status), buffer, b, 2, 2);
					b += 5;
					break;
				case 'p':                     
					// Planets Taken
					BufferUtils.formatInteger(player.stats.tplanets + player.stats.planets, buffer, b, 5);
					b += 5;
					break;
				case 'M':
					// Display, Host Machine
					player.monitor.getChars(0, (player.monitor.length() <= 16) ? player.monitor.length() : 16, buffer, b);
					b += 16;
					break;
				case 'H':                     
					// Hours Played
					float hours = (float)player.stats.tticks / 36000f;
					BufferUtils.formatFloat(hours, buffer, b, 3, 2);
					b += 6;
					break;
				case 'k': 
					// Max Kills
					BufferUtils.formatFloat((float)player.calculateMaxKills(), buffer, b, 2, 2);
					b += 5;
					break;
				// doesn't work for starbases. forget stuffing it into maxkills, send out new packet dammit!
				case 'V':
					// Kills Per Hour
					float kph = (float)player.calculateWins() / (float)player.stats.tticks / 36000f;
					BufferUtils.formatFloat(kph, buffer, b, 2, 2);
					b += 5;
					break;
				case 'v':                     
					// Deaths Per Hour			
					float dph = (float)player.calculateLosses() / (float)player.stats.tticks / 36000f;
					BufferUtils.formatFloat(dph, buffer, b, 2, 2);
					b += 5;
					break;
				default:
					break;
				}
				++b;
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			char[] temp = new char[l];
			System.arraycopy(Defaults.player_list, 0, temp, 0, l);
			Defaults.player_list = temp;
			System.err.println("PlayerList String is too long, trimming to: " + new String(Defaults.player_list));
		}

		g.setColor(player.getColor());
		g.drawChars(buffer, 0, b, 5, y);
	}
}