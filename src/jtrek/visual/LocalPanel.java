package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.config.*;
import jtrek.data.*;
import jtrek.util.*;

public class LocalPanel extends BaseMapPanel {
	final static Color TRACTOR_COLOR = new Color(0x008800);
	final static Color PRESSOR_COLOR = new Color(0x888800);
	final static int ALERT_FILTER = (Player.GREEN | Player.YELLOW | Player.RED);

	final static int TARGET_VIEW_WIDTH = 20000;
	static int SCALE = 40;
	static int VIEW = SCALE * 250;

	LocalBitmaps bitmaps;
	int alert = 0;
	int tractor_count = 0;
	
	int sound_phaser = 0;
	int sound_torps = 0;
	int sound_other_torps = 0;
	int num_other_torps = 0;
	int sound_plasma = 0;
	int sound_flags = 0;
	
	float background_x = 0f;
	float background_y = 0f;

	/** Constructor */
	public LocalPanel(String name, NetrekFrame view, Universe data, InformationPanel information, Beeplite beeplite) {
		super(name, view, data, information, beeplite);
	}
	
	/** setup */
	void setup() {
		super.setup();
		bitmaps = new LocalBitmaps(this);

		border_color = Color.green;
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));	
	}

	/** getTorpFrames */
	public final int getTorpCloudFrames() {
		return bitmaps.torp_cloud_frames;
	}

	/** getPlasmaFrames */
	public final int getPlasmaCloudFrames() {
		return bitmaps.plasma_cloud_frames;
	}

	/** newShip */
	public void newShip() {
		if(bitmaps != null) {
			bitmaps.createMyShip(this, data.me.team.no, data.me.ship.type);
		}
	}
	
	/**
	 * Recalculates scale.  Tactical screen should cover 20,000 netrek units
	 * and (by default) is 500 pixels wide
	 * That's 20% across the universe.
	 */
	private void recalculateScale() {
		int viewWidth = this.getWidth();
		if (viewWidth < 100) {
			// giving up, view too small
			return;
		}
		LocalPanel.VIEW = TARGET_VIEW_WIDTH / 2;
		LocalPanel.SCALE = TARGET_VIEW_WIDTH / viewWidth;
		
	}
	
	public void goneToOutfit() {
		alert = 0;
		tractor_count = 0;
		sound_phaser = 0;
		sound_torps = 0;
		sound_other_torps = 0;
		num_other_torps = 0;
		sound_plasma = 0;
		sound_flags = 0;
	}

	/** getCourse */
	byte getCourse() {
		int centerX = this.getWidth() / 2;
		int centerY = this.getHeight() / 2;
		return (byte)Math.rint(
			Math.atan2((double)(mx - centerX), (double)(centerY - my)) / Math.PI * 128);
	}

	/** getTarget */
	Object getTarget(int target_sype) {
		int centerX = this.getWidth() / 2;
		int centerY = this.getHeight() / 2;
		return data.getTarget(data.me.x + ((mx - centerX) * SCALE), data.me.y + ((my - centerY) * SCALE), target_sype);
	}

	/** update */
	public void update(Graphics g) {
		recalculateScale(); // added to support changing view size
		Graphics og = offscreen.getGraphics();
		og.setFont(getFont());

		Player[] players = data.players;

		int dx, dy, sx, sy, source_y;
		Player me = data.me;

		// Change border color to signify alert status
		if (alert != (me.flags & ALERT_FILTER)) {
			alert = (me.flags & ALERT_FILTER);
			switch (alert) {
			case Player.GREEN :
				view.sound_player.abortSound(SoundPlayer.WARNING_SOUND);
				border_color = Color.green;
				break;
			case Player.YELLOW :
				view.sound_player.abortSound(SoundPlayer.WARNING_SOUND);
				border_color = Color.yellow;
				break;
			case Player.RED :
				view.sound_player.playSound(SoundPlayer.WARNING_SOUND);
				border_color = Color.red;
				break;
			}		
			paintBorder(g);
		}

		background_x -= Trigonometry.COS[me.dir] * (float)me.speed / 4f;
		if(background_x < -bitmaps.background_width) {
			background_x += bitmaps.background_width;
		}
		else if(background_x > 0.0f) {
			background_x -= bitmaps.background_width;
		}
		
		background_y -= Trigonometry.SIN[me.dir] * (float)me.speed / 4f;
		if(background_y < -bitmaps.background_height) {
			background_y += bitmaps.background_height;
		}
		else if(background_y > 0.0f) {
			background_y -= bitmaps.background_height;	
		}
		
		for(int y = (int)background_y; y < view_size.height; y += bitmaps.background_height) {
			for(int x = (int)background_x; x < view_size.width; x += bitmaps.background_width) {
				og.drawImage(bitmaps.background, x, y, Color.black, null);					
			}
		}

		jtrek.util.Rectangle bounds = new jtrek.util.Rectangle(
		  me.x - VIEW, me.y - VIEW, me.x - VIEW + 20000, me.y - VIEW + 20000);

		og.translate(-bounds.x1 / SCALE, -bounds.y1 / SCALE);

		// draw the edges of the galaxy
		og.setColor(Color.red);
		og.drawRect(0, 0, 2500, 2500);

		// draw out the planets
		for(int p = data.planets.length; --p >= 0;) {
			Planet planet = data.planets[p];

			if(!bounds.contains(planet.x, planet.y)) {
				continue;
			}

			dx = planet.x / SCALE - 15;
			dy = planet.y / SCALE - 15;
			
			// do we have info on the planet?
			/*Image planet_image = null;
			if ((planet.info & me.team.bit) == 0) {
				planet_image = bitmaps.noinfo;
			}
			else if((planet.flags & Planet.AGRI) != 0) {
				planet_image = bitmaps.agri_planets[planet.owner.no];
			}
			else {
				planet_image = bitmaps.bare_planets[planet.owner.no];
			}

			source_y = 30 * (++planet.orbit % bitmaps.planet_frames);
			*/

			//no longer cycling fancy planet graphics - Darrell 0.9.7
			//og.drawImage(planet_image, dx, dy, dx + 30, dy + 30, 0, source_y, 30, source_y + 30, null);

			if ((planet.info & me.team.bit) != 0) {
				switch(Defaults.show_local) {
				case 0 :
					og.drawImage(bitmaps.teams, dx, dy, dx + 30, dy + 30, 0, planet.owner.no * 30, 30, planet.owner.no * 30 + 30, null);
					break;
				case 1 :
					int planet_type = (planet.armies > 4) ? 4 : 0;
					if ((planet.flags & Planet.REPAIR) != 0) {
						planet_type += 2;
					}
					if ((planet.flags & Planet.FUEL) != 0) {
						planet_type += 1;
					}
					og.drawImage(bitmaps.resources, dx, dy, dx + 30, dy + 30, 0, planet_type * 30, 30, planet_type * 30 + 30, null);
					break;
				default :
					break;
				}
				og.setColor(planet.getColor());
			}
			else {
				og.setColor(Team.TEAMS[Team.NOBODY].getColor());
			}
			
			// Now just drawing circle for the planet - Darrell 0.9.7
			og.drawOval(dx, dy, 30, 30);

			
			// draw the name of the planet
			if(Defaults.name_mode != 0) {
				String s = planet.name;
				// draw a maximum of 5 characters
				if(Defaults.name_mode == 1 && s.length() > 5) {
					s = s.substring(0, 5);
				}
				og.drawString(s, dx + 15 - og.getFontMetrics().stringWidth(s) / 2, dy + 38);
			}
		} // end draw planets

		// draw the players
		for(int p = players.length - 1; p >= 0; p--) {
			Player player = players[p];

			// see if this player is active
			if ((player.status != Player.ALIVE && player.status != Player.EXPLODE) || (player.flags & Player.OBSERV) != 0) {
				continue;
			}

			// handle the drawing of cloak phase frames
			if ((player.flags & Player.CLOAK) != 0) {
				if (player.cloakphase < (Player.CLOAK_PHASES - 1)) {
					if(player.me && player.cloakphase == 0) {
						view.sound_player.playSound(SoundPlayer.CLOAK_SOUND);
					}
					++player.cloakphase;
				}
			}
			else if (player.cloakphase > 0) {
				if (player.cloakphase == Player.CLOAK_PHASES - 1) {
					view.sound_player.playSound(SoundPlayer.UNCLOAK_SOUND);
				}
				else {
					view.sound_player.abortSound(SoundPlayer.CLOAK_SOUND);
				}
				--player.cloakphase;
			}

			if(!bounds.contains(player.x, player.y)) {
				// if he's off screen we don't draw phasers anyway
				data.phasers[player.no].status = Phaser.FREE;
				continue;
			}

			dx = player.x / SCALE;
			dy = player.y / SCALE;

			sx = dx - bitmaps.ship_size / 2;
			sy = dy - bitmaps.ship_size / 2;

			if ((player.flags & Player.CLOAK) != 0 && player.cloakphase >= (Player.CLOAK_PHASES - 1)) {
				if (player == me) {
					source_y = player.cloakphase * 20;
					og.drawImage(bitmaps.cloak, sx, sy, sx + 20, sy + 20, 0, source_y, 20, source_y + 20, null);
					if ((player.flags & Player.SHIELD) != 0) {
						og.setColor(player.getColor());
						og.drawOval(sx, sy, 18, 18);
					}
				}
				continue;
			} 

			if (player.status == Player.ALIVE) {
				
				// if my ship image is null, create it
				if (player.me && bitmaps.myship == null) {
					newShip();
				}
				Image ship = player.me ? bitmaps.myship : bitmaps.ships[player.team.no][player.ship.type];
				//Image ship = bitmaps.ships[player.team.no][player.ship.type];

				source_y = bitmaps.rosette[player.dir];
				
				og.drawImage(ship, 
							 sx, 
							 sy, 
							 sx + bitmaps.ship_size, 
							 sy + bitmaps.ship_size, 
							 0, 
							 source_y, 
							 bitmaps.ship_size, 
							 source_y + bitmaps.ship_size, 
							 null);				

				if (player.cloakphase > 0) {
					source_y = player.cloakphase * 20;
					og.drawImage(bitmaps.cloak, sx, sy, sx + 20, sy + 20, 0, source_y, 20, source_y + 20, null);
					continue;
				}

				if(player.me) {
					if(Defaults.vary_hull) {
						int hull_left = (100 * (player.ship.maxdamage - player.damage)) / player.ship.maxdamage;
						int hull_num = 7;

						if (hull_left <= 16) {
							hull_num = 0;
							og.setColor(Color.red);
						}
						else if (hull_left <= 28) {
							hull_num = 1;
							og.setColor(Color.yellow);
						}
						else if (hull_left <= 40) {
							hull_num = 2;
							og.setColor(Color.green);
						}
						else if (hull_left <= 52) {
							hull_num = 3;
							og.setColor(Color.green);
						}
						else if (hull_left <= 64) {
							hull_num = 4;
							og.setColor(Color.green);
						}
						else if (hull_left <= 76) {
							hull_num = 5;
							og.setColor(Color.white);
						}
						else if (hull_left <= 88) {
							hull_num = 6;
							og.setColor(Color.white);
						}
						else {
							og.setColor(Color.white);
						}

						source_y = hull_num * bitmaps.hull_size;

						sx = dx - bitmaps.hull_size / 2;
						sy = dy - bitmaps.hull_size / 2;

						og.drawImage(
								bitmaps.hull, 
								sx, 
								sy, 
								sx + bitmaps.hull_size, 
								sy + bitmaps.hull_size, 
								0, 
								source_y,
								bitmaps.hull_size, 
								source_y + bitmaps.hull_size, 
								null);
					}
					if((sound_flags & Player.SHIELD) != 0 && (me.flags & Player.SHIELD) == 0) {
						view.sound_player.playSound(SoundPlayer.SHIELD_DOWN_SOUND);
					}
					if((sound_flags & Player.SHIELD) == 0 && (me.flags & Player.SHIELD) != 0) {
						view.sound_player.playSound(SoundPlayer.SHIELD_UP_SOUND);
					}
				}

				if ((player.flags & Player.SHIELD) != 0) {
					int color_num = player.team.no;
					int shield_num = 2;
					if(player.me) {
						color_num = -1;
						if(Defaults.vary_shields) {
							shield_num = (int)(5f * (float)player.shield / (float)player.ship.maxshield);
							if (shield_num >= 5)
								shield_num = 4;
							if(!Defaults.warn_shields) {
								switch(shield_num) {
								case 0:
									// set the color to red
									color_num = 2;
									break;
								case 1:
								case 2:
									// set the color to yellow
									color_num = 1;
									break;
								default:
									// set the color to green
									color_num = 3;
									break;
								}
							}
						}
						if(Defaults.warn_shields) {
							switch (alert) {
							case Player.GREEN :
								// set the color to green
								color_num = 3;
								break;
							case Player.YELLOW :
								// set the color to yellow
								color_num = 1;
								break;
							case Player.RED :
								// set the color to red
								color_num = 2;
								break;
							}
						}
					}

					source_y = shield_num * bitmaps.shield_size;

					sx = dx - bitmaps.shield_size / 2;
					sy = dy - bitmaps.shield_size / 2;

					og.drawImage(
							(color_num == -1 ? bitmaps.my_shield : bitmaps.shield[color_num]),
							sx,
							sy,
							sx + bitmaps.shield_size,
							sy + bitmaps.shield_size,
							0,
							source_y,
							bitmaps.shield_size,
							source_y + bitmaps.shield_size,
							null);
				}

				if (Defaults.use_lite && beeplite.player[p] > 0 && (beeplite.flag & Beeplite.PLAYERS_LOCAL) != 0) {
					source_y = bitmaps.emphasis_player_size * (beeplite.player[p] % bitmaps.emphasis_player_frames);

					sx = dx - bitmaps.emphasis_player_size / 2;
					sy = dy - bitmaps.emphasis_player_size / 2;

					og.drawImage(bitmaps.emphasis_player, sx, sy, sx + bitmaps.emphasis_player_size, 
					  sy + bitmaps.emphasis_player_size, 0, source_y, bitmaps.emphasis_player_size,
					  source_y + bitmaps.emphasis_player_size, null);
				}

				og.drawChars(Player.PLAYER_LETTER, player.no, 1, dx + 12, dy - 2);
			}
			else if(player.status == Player.EXPLODE) {
				
				if(player.explode == 1) {
					view.sound_player.playSound(player.me ? SoundPlayer.EXPLOSION_SOUND : SoundPlayer.OTHER_EXPLOSION_SOUND);
				}
				
				boolean starbase = (player.ship.type == Ship.SB);
				if (starbase && player.explode < bitmaps.sb_explosion_frames) {
					sx = dx - bitmaps.sb_explosion_size / 2;
					sy = dy - bitmaps.sb_explosion_size / 2;
					source_y = bitmaps.sb_explosion_size * player.explode; 

					og.drawImage(bitmaps.sb_explosion, sx, sy, sx + bitmaps.sb_explosion_size, 
					  sy + bitmaps.sb_explosion_size, 0, source_y, bitmaps.sb_explosion_size, 
					  source_y + bitmaps.sb_explosion_size, null);
				}
				else if(!starbase && player.explode < bitmaps.explosion_frames) {
					sx = dx - bitmaps.explosion_size / 2;
					sy = dy - bitmaps.explosion_size / 2;
					source_y = bitmaps.explosion_size * player.explode; 
					og.drawImage(bitmaps.explosion, sx, sy, sx + bitmaps.explosion_size, 
					  sy + bitmaps.explosion_size, 0, source_y, bitmaps.explosion_size, 
					  source_y + bitmaps.explosion_size, null);
				}
				++player.explode;
			}
			
			// draw the player's phaser
			Phaser phaser = data.phasers[player.no];
			if (phaser.status != Phaser.FREE) {
				if(sound_phaser == 0) {
					view.sound_player.playSound(player.me ? SoundPlayer.PHASER_SOUND : SoundPlayer.OTHER_PHASER_SOUND);
					++sound_phaser;
				}
				switch (phaser.status) {
				case Phaser.MISS :
					// Here I will have to compute the end coordinate
					float compute = Phaser.MAX_DISTANCE * player.ship.phaserdamage / 100.0f;
					sx = (int)(player.x + compute * Trigonometry.COS[phaser.dir]) / SCALE;
					sy = (int)(player.y + compute * Trigonometry.SIN[phaser.dir]) / SCALE;
					break;

				case Phaser.HIT2 :
					sx = phaser.x / SCALE;
					sy = phaser.y / SCALE;
					break;
				default:
					// Start point is dx, dy
					sx = phaser.target.x / SCALE;
					sy = phaser.target.y / SCALE;
					break;
				}

				if (sx != dx || sy != dy) {
					if (phaser.status == Phaser.MISS || ((phaser.fuse % 2) == 0) || player.team.no != me.team.no) {
						// get the team color so that my phaser will not always be white
						og.setColor(player.team.getColor());
					}
					else {
						og.setColor(Color.white);
					}
					og.drawLine(dx, dy, sx, sy);
					if(++phaser.fuse > phaser.maxfuse) {
						phaser.status = Phaser.FREE;
					}
				}
			}
			else if(player.me) {
				sound_phaser = 0;
			}

			// show my tractor/pressor
			if (Defaults.show_tractor_pressor && player.me) {
				if((player.flags & (Player.TRACT | Player.PRESS)) == 0 || player.status != Player.ALIVE) {
					tractor_count = 0;
					continue;
				}

				if(!Defaults.continuous_tractor && ++tractor_count > 2) {
					continue;
				}

				if(player.tractor < 0 || player.tractor >= players.length) {
					continue;
				}

				Player tractee = players[player.tractor];
				if(tractee.status != Player.ALIVE || ((tractee.flags & Player.CLOAK) != 0 && tractee.cloakphase == (Player.CLOAK_PHASES - 1))) {
					continue;
				}

				sx = tractee.x / SCALE;
				sy = tractee.y / SCALE;

				if (sx == dx && sy == dy) {
					continue;
				}
				
				double theta = Math.atan2((double)(sx - dx), (double)(dy - sy)) + Math.PI / 2.0;
				char dir = (char)(theta / Math.PI * 128d);
				if(dir >= Trigonometry.COS.length) {
					dir = 0;
				}
				
				og.setColor(((player.flags & Player.PRESS) != 0) ? PRESSOR_COLOR : TRACTOR_COLOR);
				int cos = (int)(Trigonometry.COS[dir] * 10);
				int sin = (int)(Trigonometry.SIN[dir] * 10);
				drawDottedLine(og, dx, dy, sx + cos, sy + sin);
				drawDottedLine(og, dx, dy, sx - cos, sy - sin);
			}
		}  // end draw players loop
		
		// draw torps
		for(int p = players.length; --p >= 0;) {
			Player player = players[p];
			if(player.ntorp == 0) {
				continue;
			}
			for (int t = 0; t < Universe.MAX_TORPS; ++t) {
				Torp torp = data.torps[t + (p * Universe.MAX_TORPS)];
				if(torp.status == 0) {
					continue;
				}

				if(!bounds.contains(torp.x, torp.y)) {
					// Call any torps off screen "free" (if owned by other)
					if (torp.status == Torp.EXPLODE && !player.me) {
						torp.status = Torp.FREE;
						--player.ntorp;
					}
					continue;
				}

				dx = torp.x / SCALE - 1;
				dy = torp.y / SCALE - 1;

				if (torp.status == Torp.EXPLODE) {
					if (--torp.fuse <= 0) {
						torp.status = Torp.FREE;
						--player.ntorp;
						continue;
					}
					
					if (torp.fuse == bitmaps.torp_cloud_frames - 1) {
						view.sound_player.playSound(SoundPlayer.TORP_HIT_SOUND);
					}
		
					sx = dx - bitmaps.torp_cloud_size / 2;
					sy = dy - bitmaps.torp_cloud_size / 2;
					source_y = bitmaps.torp_cloud_size * torp.fuse;
					
					Image image;
					if(player.me) {
						image = bitmaps.my_torp_cloud;
					}
					else if(((torp.war & me.team.bit) != 0 || (player.team.bit & (me.hostile | me.swar)) != 0)) {
						image = bitmaps.enemy_torp_cloud;
					}
					else {
						image = bitmaps.friend_torp_cloud;
					}

					og.drawImage(
						image, 
						sx, 
						sy, 
						sx + bitmaps.torp_cloud_size, 
						sy + bitmaps.torp_cloud_size, 
						0, 
						source_y,
						bitmaps.torp_cloud_size, 
						source_y + bitmaps.torp_cloud_size, 
						null);
				} 
				else {					
					Image image;										
					if(player.me) {
						image = bitmaps.my_torp;
					}
					else if(((torp.war & me.team.bit) != 0 || (player.team.bit & (me.hostile | me.swar)) != 0)) {
						image = bitmaps.enemy_torp;
					}
					else {					
						image = bitmaps.friend_torp;
					}
					
					source_y = bitmaps.torp_size * (++torp.fuse % bitmaps.torp_frames);
					sx = dx - bitmaps.torp_size / 2;
					sy = dy - bitmaps.torp_size / 2;					
					
					og.drawImage(
						image, 
						sx, 
						sy, 
						sx + bitmaps.torp_size,
						sy + bitmaps.torp_size,
						0,
						source_y,
						bitmaps.torp_size,
						source_y + bitmaps.torp_size,
						null);
				}
			}
		} // end draw torps

		// draw plasmas
		for(int p = players.length; --p >= 0;) {
			Player player = players[p];
			
			if(player.nplasmatorp <= 0) {
				continue;
			}

			Plasma plasma = data.plasmas[p];

			if (plasma.status == Plasma.FREE) {
				continue;
			}

			if(!bounds.contains(plasma.x, plasma.y)) {
				continue;
			}

			dx = plasma.x / SCALE - 2;
			dy = plasma.y / SCALE - 2;

			if (plasma.status == Plasma.EXPLODE) {
				if (--plasma.fuse <= 0) {
					plasma.status = Plasma.FREE;
					--player.nplasmatorp;
					continue;
				}
				
				if (plasma.fuse == bitmaps.plasma_cloud_frames - 1) {
					view.sound_player.playSound(SoundPlayer.PLASMA_HIT_SOUND);
				}
			
				sx = dx - bitmaps.plasma_cloud_size / 2;
				sy = dy - bitmaps.plasma_cloud_size / 2;
				
				source_y = bitmaps.plasma_cloud_size * plasma.fuse;
				
				Image image;
				if(player.me) {
					image = bitmaps.my_plasma_cloud;
				}
				else if(((plasma.war & me.team.bit) != 0 || (player.team.bit & (me.hostile | me.swar)) != 0)) {
					image = bitmaps.enemy_plasma_cloud;
				}
				else {
					image = bitmaps.friend_plasma_cloud;
				}

				og.drawImage(
					image, 
					sx, 
					sy, 
					sx + bitmaps.plasma_cloud_size, 
					sy + bitmaps.plasma_cloud_size,
					0, 
					source_y, 
					bitmaps.plasma_cloud_size, 
				  	source_y + bitmaps.plasma_cloud_size, 
					null);
			}
			else {
				source_y = bitmaps.plasma_size * (++plasma.fuse % bitmaps.plasma_frames);
				
				sx = dx - bitmaps.plasma_size / 2;
				sy = dy - bitmaps.plasma_size / 2;
				
				Image image;
				if(player.me) {
					image = bitmaps.my_plasma;
				}
				else if(((plasma.war & me.team.bit) != 0 || (player.team.bit & (me.hostile | me.swar)) != 0)) {
					image = bitmaps.enemy_plasma;
				}
				else {
					image = bitmaps.friend_plasma;
				}
				
				og.drawImage(
					image, 
					sx, 
					sy, 
					sx + bitmaps.plasma_size,
					sy + bitmaps.plasma_size,
					0,
					source_y,
					bitmaps.plasma_size,
					source_y + bitmaps.plasma_size,
					null);
			}
		} // end draw plasmas

		// show lock triangle on local map
		if(Defaults.show_lock >= 2) {
			og.setColor(Color.white);
			if ((me.flags & Player.PLOCK) != 0) {
				// locked onto a ship
				Player player = players[me.playerl];
				if ((player.flags & Player.CLOAK) == 0) {
					if(bounds.contains(player.x, player.y)) {
						dx = player.x / SCALE;
						dy = player.y / SCALE + 18;
						drawTriangle(og, dx, dy, false);
					}			
				}
			}
			else if ((me.flags & Player.PLLOCK) != 0) {
				// locked onto a planet
				Planet planet = data.planets[me.planet];
				if(bounds.contains(planet.x, planet.y)) {
					dx = planet.x / SCALE;
					dy = planet.y / SCALE - 20;
					drawTriangle(og, dx, dy, true);
				}
			}
		}
		og.translate(bounds.x1 / SCALE, bounds.y1 / SCALE);
		g.drawImage(offscreen, BORDER, BORDER, null);
		
		if(sound_torps < me.ntorp) {
			view.sound_player.playSound(SoundPlayer.FIRE_TORP_SOUND);
		}
			
		if(sound_other_torps < num_other_torps) {
			view.sound_player.playSound(SoundPlayer.OTHER_FIRE_TORP_SOUND);
		}
		if (sound_plasma < me.nplasmatorp) {
			view.sound_player.playSound(SoundPlayer.FIRE_PLASMA_SOUND);
		}

		sound_flags = me.flags;
		sound_torps = me.ntorp;
		sound_other_torps = num_other_torps;
		num_other_torps = 0;
		sound_plasma = me.nplasmatorp;
	}

	/** drawDottedLine */
	protected void drawDottedLine(Graphics g, int x1, int y1, int x2, int y2) {
		float dx = (x2 - x1) / 12f;
		float dy = (y2 - y1) / 12f;
		for(int i = 12; --i >= 1;) {
			// reuse x2 and y2
			x2 = x1 + (int)(dx * i);
			y2 = y1 + (int)(dy * i);
			g.drawLine(x2, y2, x2, y2);
		}
	}
}