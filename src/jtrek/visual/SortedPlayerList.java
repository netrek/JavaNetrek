// This class contributed by John Kilburg (john@filthy.physics.unlv.edu)

package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;

public class SortedPlayerList extends StandardPlayerList {

	public void update(Graphics g) {
		int[][] team_player_slots = new int[Team.TEAMS.length][Universe.MAX_PLAYERS];
		int[] team_player_count = new int[Team.TEAMS.length];	
		
		Graphics og = offscreen.getGraphics();
		og.setFont(fixed_font);

		int line_height = og.getFontMetrics().getHeight() - 3;
		int y = line_height + 1;
		clearOffscreen(og);
		og.drawString(header, 5, y);

		// stash player indexes in team slots
		for (int p = 0; p < data.players.length; ++p) {
			if (data.players[p].status != Player.FREE) {
				int team_no = data.players[p].team.no;
				team_player_slots[team_no][team_player_count[team_no]++] = p;
			}
		}

		// draw the player list
		for (int t = 0; t < Team.TEAMS.length; ++t) {
			if (t != data.me.team.no) {
				for(int p = 0; p < team_player_count[t]; ++p) {
					y += line_height;
					drawPlayerLine(og, data.players[team_player_slots[t][p]], y);
				}
			}
		}

		// draw my team last
		for(int p = 0, t = data.me.team.no; p < team_player_count[t]; ++p) {
			y += line_height;
			drawPlayerLine(og, data.players[team_player_slots[t][p]], y);
		}

		g.drawImage(offscreen, BORDER, BORDER, null);
	}       
}