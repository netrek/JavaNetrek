package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.util.*;

public class RankListPanel extends XFocusPanel {
	final static String HEADER = "  Rank       Hours  Defense  Ratings      DI";
	
	Universe data;

	char[] buffer = new char[44];

	/** PlayerListPanel */
	public RankListPanel(String name, Universe data) {
		super(name);
		this.data = data;
	}

	/** update */
	public void update(Graphics g) {
		g.translate(BORDER, BORDER);
		g.clearRect(0, 0, view_size.width, view_size.height);
		g.setColor(Color.white);
		int line_height = g.getFontMetrics().getHeight() - 2;
		int y = line_height;	
		g.drawString(HEADER, 5, y);
		for(int r = 0; r < Rank.RANKS.length; ++r) {
			y += line_height;		
			fillBufferRankInfo(Rank.RANKS[r]);
			g.setColor((data.me.stats.rank == Rank.RANKS[r]) ? Color.cyan : Color.white);
			g.drawChars(buffer, 0, 44, 5, y);
		}
	}	


	/** fillBufferWithPlanetInfo */
	void fillBufferRankInfo(Rank rank) {
		BufferUtils.fillWithSpaces(buffer);
		rank.name.getChars(0, rank.name.length(), buffer, 0);
		BufferUtils.formatInteger((int)rank.hours, buffer, 13, 5);
		BufferUtils.formatFloat(rank.defense, buffer, 20, 4, 2);
		BufferUtils.formatFloat(rank.ratings, buffer, 29, 4, 2);
		BufferUtils.formatFloat(rank.ratings * rank.hours, buffer, 36, 5, 2);
	}

}