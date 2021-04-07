package jtrek.data;

import jtrek.Copyright;
import jtrek.util.*;
import java.awt.*;

public class Universe {
	public final static int SCALE = 40;
	public final static int MAX_PLANETS = 40;
	public final static int MAX_PLAYERS = 32;
	public final static int MAX_TORPS = 8;

	public final static int PIXEL_SIZE = 100000;
	public final static int HALF_PIXEL_SIZE = PIXEL_SIZE / 2;

	public final static int TARG_PLAYER	= 0x01;
	public final static int TARG_PLANET	= 0x02;
	public final static int TARG_CLOAK	= 0x04;
	public final static int TARG_SELF	= 0x08;
	public final static int TARG_ENEMY	= 0x10;
	public final static int TARG_FRIEND	= 0x20;
	public final static int TARG_BASE   = 0x40;

	public Planet[] planets = new Planet[MAX_PLANETS];
	public Player[] players = new Player[MAX_PLAYERS];
	public Player me = null;

	public Phaser[] phasers = new Phaser[MAX_PLAYERS];
	public Torp[] torps = new Torp[MAX_PLAYERS * MAX_TORPS];
	public Plasma[] plasmas = new Plasma[MAX_PLAYERS];
	public Status status = new Status();
	public int rotate = 0;

	/** Universe */
	public Universe() {
		for(int p = 0; p < planets.length; ++p) {
			planets[p] = new Planet(p);
		}

		for(int p = 0; p < players.length; ++p) {
			players[p] = new Player(p);
		}

		for(int p = 0; p < phasers.length; ++p) {
			phasers[p] = new Phaser();
		}

		for(int t = 0; t < torps.length; ++t) {
			torps[t] = new Torp(t, players[t / 8]);
		}

		for(int p = 0; p < plasmas.length; ++p) {
			plasmas[p] = new Plasma(p, players[p]);
		}
	}

	/** getTarget */
	public Object getTarget(int x, int y, int target_type) {
		Object object = null;
		int dist; 
		int closedist = PIXEL_SIZE;

		if ((target_type & TARG_PLANET) != 0) {
			Planet planet;
			for(int p = 0; p < planets.length; ++p) {
				planet = planets[p];
				dist = Trigonometry.ihypot(x - planet.x, y - planet.y);
				if (dist < closedist) {
					closedist = dist;
					object = planet;
				}
			}
		}

		if((target_type & (TARG_PLAYER | TARG_FRIEND | TARG_ENEMY | TARG_BASE)) != 0) {
			boolean friendly;
			Player player;
			for(int p = 0; p < players.length; ++p) {
				player = players[p];
				if(player.status != Player.ALIVE) {
					continue;
				}
				if((player.flags & Player.CLOAK) != 0 && (target_type & TARG_CLOAK) == 0) {
					continue;
				}
				if(player.me && (target_type & TARG_SELF) == 0) {
					continue;
				}
				if((target_type & TARG_BASE) != 0 && player.ship.type != Ship.SB) {
					continue;
				}
				friendly = player.friendlyPlayer(me);
				if (friendly && (target_type & TARG_ENEMY) != 0) {
					continue;
				}
				if (!friendly && (target_type & TARG_FRIEND) != 0) {
					continue;
				}

				dist = Trigonometry.ihypot(x - player.x, y - player.y);
				if (dist < closedist) {
					closedist = dist;
					object = player;				
				}
			}
		}
		if(closedist >= PIXEL_SIZE) {
			// Didn't find a target, bad news.  Just return self. Oh well...
			return me;
		}
		return object;
	}

	/** getTarget */
	public Object getTarget(int target_type) {
		return getTarget(me.x, me.y, target_type);
	}

	/**
	*  Give all weapons for all ships the status of not being active.
	*/   
	public void resetWeaponInfo() {
		for(int t = 0; t < torps.length; ++t) {
			torps[t].status = Torp.FREE;
		}

		for(int p = 0; p < plasmas.length; ++p) {
			plasmas[p].status = Plasma.FREE;
		}

		for(int p = 0; p < players.length; ++p) {
			players[p].ntorp = 0;
			players[p].nplasmatorp = 0;
		}

		for(int p = 0; p < phasers.length; ++p) {
			phasers[p].status = Phaser.FREE;
		}
	}

	/** rotateGalaxy */
	public void rotateGalaxy(int rotate) {
		// calculate the degrees we must rotate the galaxy by in order to get
		// the desired galaxy rotation value.
		int rotate_deg = rotate - this.rotate;
		this.rotate = rotate;

		if(rotate_deg % 256 != 0) {
			Planet planet;
			for(int p = 0; p < planets.length; ++p) {
				planet = planets[p];

				// Rotate coordinates
				Point point = Rotate.rotateCoord(planet.x, planet.y, rotate_deg,
				  Universe.HALF_PIXEL_SIZE, Universe.HALF_PIXEL_SIZE);

				planet.x = point.x;
				planet.y = point.y;
			}

			// we could wait for the server to do this but looks better if we do it now.
			Player player;
			for(int p = 0; p < players.length; ++p) {
				player = players[p];
				if(player.status != Player.ALIVE) {
					continue;
				}

				// Rotate coordinates
				Point point = Rotate.rotateCoord(player.x, player.y, rotate_deg,
				  Universe.HALF_PIXEL_SIZE, Universe.HALF_PIXEL_SIZE);

				player.x = point.x;
				player.y = point.y;

				player.dir = Rotate.rotateDir(player.dir, rotate_deg);
			}
			// phasers/torps/etc .. wait for serve
		}
	}
}
