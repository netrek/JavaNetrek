package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.config.*;
import jtrek.util.*;
import jtrek.data.*;

public class InformationPanel extends BasePanel {
	NetrekFrame view;
	Universe data;
	Object target;
	boolean extended;

	int line_height;
	int char_width;

	int visible_count = 0;

	InformationPanel(String name, NetrekFrame view, Universe data) {
		super(name);
		this.view = view;
		this.data = data;
		FontMetrics metrics = getFontMetrics(fixed_font);
		line_height = metrics.getAscent() + 1;
		char_width = metrics.charWidth('X');
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		visible_count = visible ? Defaults.keep_info : 0;
	}

	public void tick() {
		if(visible_count > 0) {
			if(--visible_count == 0) {
				setVisible(false);
			}
		}
	}

	/**
	* showInformation
	*/
	void showInformation(Object target, int x, int y, boolean extended) {
		this.target = target;
		this.extended = extended;
		int height = 9;
		int width = 24 * char_width + 4;
		if (target instanceof Player) {
			height += line_height * (extended ? 10 : (Defaults.new_info ? 4 : 8));
		}
		else {
			height += line_height * 3;
		}

		// check the bounds
		Dimension d = getParent().getSize();
		if(x + width > d.width) {
			x = d.width - width;
		}
		if(y + height > d.height) {
			y = d.height - height;		
		}
		setBounds(x, y, width, height);
		setVisible(true);
		repaint();
	}

	/**
	* update
	*/
	public void update(Graphics g) {
		Player me = data.me;
		String s;
		g.setColor(Color.black);
		g.fillRect(BORDER - 2, BORDER - 2, view_size.width + 4, view_size.height + 4);
		g.setColor(Color.white);

		int x = 7;
		int y = line_height + 2;

		if (target instanceof Player) {
			Player player = (Player)target;
			g.setColor(player.getColor());
			s = player.getPlayerString() + ':';
			g.drawString(s, x, y);	
			y += line_height;
			if(!extended && Defaults.new_info) {
				int x2 = x + (char_width * 11);
				s = player.login + '@' + player.monitor;
				g.drawString(s, x, y);

				y += line_height;				
				s = "Speed   " + player.speed;
				g.drawString(s, x, y);
				s = "Kills   " + player.kills;
				g.drawString(s, x2, y);

				y += line_height;			
				s = "Ship  " + String.valueOf(player.ship.abbrev);
				g.drawString(s, x, y);			
				if((player.swar & me.team.bit) != 0) {
					s = "WAR";
				}
				else if((player.hostile & me.team.bit) != 0) {
					s = "HOSTILE";
				}
				else {
					s = "PEACEFUL";
				}
				g.drawString(s, x2, y);			
			}
			else {
				s = "Login   " + player.login;
				g.drawString(s, x, y);

				y += line_height;
				s = "Display " + player.monitor;
				g.drawString(s, x, y);

				y += line_height;
				
				if(extended) {
					Stats stats = player.stats;

					s = "        Rating    Total";
					g.drawString(s, x, y);

					y += line_height;
					s = "Bombing: " 
						+ BufferUtils.floatTo22String(stats.bombingRating(data.status))
						+ "  " 
						+ BufferUtils.integerTo5String(stats.tarmsbomb + stats.armsbomb);
					g.drawString(s, x, y);

					y += line_height;
					s = "Planets: " 
						+ BufferUtils.floatTo22String(stats.planetRating(data.status))
						+ "  " 
						+ BufferUtils.integerTo5String(stats.tplanets + stats.planets);
					g.drawString(s, x, y);

					y += line_height;

					if(player.ship.type == Ship.SB && view.feature_list.features[FeatureList.SB_HOURS].value != 0) {
						s = "KPH:    "
							+ BufferUtils.floatTo32String(stats.SBKillsPerHour())
							+ "  " 
							+ BufferUtils.integerTo5String(stats.sbkills);
					}
					else {
						s = "Offense: " 
							+ BufferUtils.floatTo22String(stats.offenseRating(data.status))
							+ "  " 
							+ BufferUtils.integerTo5String(stats.tkills + stats.kills);
					}
					g.drawString(s, x, y);
					y += line_height;

					if(player.ship.type == Ship.SB && view.feature_list.features[FeatureList.SB_HOURS].value != 0) {
						s = "DPH:    "
							+ BufferUtils.floatTo32String(stats.SBDefensePerHour())
							+ "  " 
							+ BufferUtils.integerTo5String(stats.sblosses);
					}
					else {
						s = "Defense: " 
							+ BufferUtils.floatTo22String(stats.defenseRating(data.status))
							+ "  " 
							+ BufferUtils.integerTo5String(stats.tlosses + stats.losses);
					}

					g.drawString(s, x, y);
					y += line_height;

					s = "  Maxkills: " 
						+ BufferUtils.floatTo32String((float)player.calculateMaxKills());
					
					g.drawString(s, x, y);
					y += line_height;

					if(player.ship.type == Ship.SB && view.feature_list.features[FeatureList.SB_HOURS].value != 0) {
						s = "  Hours:    "
							+ BufferUtils.floatTo32String(stats.sbticks / 36000f);
					}
					else {
						s = "  Hours:    "
							+ BufferUtils.floatTo32String(stats.tticks / 36000f);
					}
					g.drawString(s, x, y);

				}
				else {			
					s = "Speeed  " + player.speed;
					g.drawString(s, x, y);

					y += line_height;
					s = "Kills   " + player.kills;
					g.drawString(s, x, y);

					y += line_height;
					s = "Dist    " + Trigonometry.ihypot(me.x - player.x, me.y - player.y);
					g.drawString(s, x, y);

					y += line_height;
					s = "S-Class " + String.valueOf(player.ship.abbrev);
					g.drawString(s, x, y);

					y += line_height;
					if((player.swar & me.team.bit) != 0) {
						s = "WAR";
					}
					else if((player.hostile & me.team.bit) != 0) {
						s = "HOSTILE";
					}
					else {
						s = "PEACEFUL";
					}
					g.drawString(s, x, y);
				}
			}
		}
		else {
			Planet planet = (Planet)target;
			if((planet.info & me.team.bit) != 0) {
				g.setColor(planet.getColor());

				// draw the planet name
				g.setFont(bold_fixed_font);
				s = planet.name + " (" + planet.owner.letter + ')';
				g.drawString(s, x, y);
				g.setFont(fixed_font);

				// draw the number of armies
				y += line_height;
				s = "Armies " + planet.armies;
				g.drawString(s, x, y);

				// draw in the planet resources
				y += line_height;
				s = (((planet.flags & Planet.REPAIR) != 0) ? "REPAIR " : "       ")
					+ (((planet.flags & Planet.FUEL) != 0) ? "FUEL " : "     ")
					+ (((planet.flags & Planet.AGRI) != 0) ? "AGRI " : "     ");
				
				// draw in who has information on each planet
				for(int t = 1; t < Team.TEAMS.length; ++t) {
					s += ((planet.info & Team.TEAMS[t].bit) != 0) ? Team.TEAMS[t].letter : ' ';
				}
				g.drawString(s, x, y);
			}
			else {
				g.setColor(Team.TEAMS[Team.IND].getColor());
				g.drawString(planet.name, x, y);
				g.drawString("No other info", x, y + line_height);
			}
		}
	}
}