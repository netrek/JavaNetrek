package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.config.*;

public class Beeplite {
	public static int PLAYERS_MAP		= 0x01;
	public static int PLAYERS_LOCAL		= 0x02;
	public static int SELF				= 0x04;
	public static int PLANETS			= 0x08;
	public static int SOUNDS			= 0x10;
	public static int COLOR				= 0x20;
	public static int TTS				= 0x40;

	int flag = 0;
	int[] planet = new int[Universe.MAX_PLANETS];
	int[] player = new int[Universe.MAX_PLAYERS];

	/**
	* set the flag
	*/
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	* emphasis the planet
	*/
	public void litePlanet(Planet target) {
		target.flags |= Planet.REDRAW;
		planet[target.no] = Defaults.beep_lite_cycle_time_planet;
	}

	/**
	* emphasis the player
	*/
	public void litePlayer(Player target) {
		if((target.flags & Player.CLOAK) == 0) {
			player[target.no] = Defaults.beep_lite_cycle_time_player;
		}
	}

}
