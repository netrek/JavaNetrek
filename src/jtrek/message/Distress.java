package jtrek.message;

import jtrek.Copyright;
import jtrek.data.*;
import jtrek.util.*;
import jtrek.visual.*;

public class Distress implements DistressConstants {
	Player sender;
	int damage;
	int shields;
	int armies;
	int wtemp;
	int etemp;
	int fuel_percentage;
	int short_status;
	boolean wtemp_flag;
	boolean etemp_flag;
	boolean cloak_flag;
	int distress_type;
	boolean macro_flag;

	Planet close_planet;
	Planet target_planet;

	Player close_player;
	Player close_friend;
	Player close_enemy;

	Player target_player;
	Player target_friend;
	Player target_enemy;	
	byte[] cclist = new byte[6];				// allow us some day to cc a message up to 5 people
												// sending this to the server allows the server to 
												// do the cc action otherwise it would have to be 
												// the client ... less BW this way
	int i;
	boolean prepend = false;
	String prepend_append = "";					// text which we pre or append

	Distress(Universe data, String message, int from) {
		sender = data.players[from];
		byte[] mtext = BufferUtils.getBytes(message.substring(10));
		distress_type = mtext[0] & 0x1F;
		macro_flag = ((mtext[0] & 0x20) > 0);
		fuel_percentage = mtext[1] & 0x7F;
		damage = mtext[2] & 0x7F;
		shields = mtext[3] & 0x7F;
		etemp = mtext[4] & 0x7F;
		wtemp = mtext[5] & 0x7F;
		armies = mtext[6] & 0x1F;
		short_status = mtext[7] & 0x7F;
		wtemp_flag = ((short_status & Player.WEP) != 0);
		etemp_flag= ((short_status & Player.ENG) != 0);
		cloak_flag = ((short_status & Player.CLOAK) != 0);

		try {
			close_planet = data.planets[mtext[8] & 0x7F];
			target_planet = data.planets[mtext[10] & 0x7F];
			
			close_player = data.players[mtext[13] & 0x7F];
			close_friend = data.players[mtext[15] & 0x7F];
			close_enemy = data.players[mtext[9] & 0x7F];
			
			target_player = data.players[mtext[12] & 0x7F];
			target_friend = data.players[mtext[14] & 0x7F];
			target_enemy = data.players[mtext[11] & 0x7F];
		}
		catch (ArrayIndexOutOfBoundsException e) {	
			System.out.println("Bad distress message: " + message);
			close_planet = target_planet = data.planets[0];
			close_player = close_friend = close_enemy = target_player = target_friend = target_enemy = data.me;
		}

		i = 0;
		while ((mtext[16 + i] & 0xC0) == 0xC0 && (i < 6)) {
			cclist[i] = (byte)(mtext[16 + i] & 0x1F);
			i++;
		}
		cclist[i] = mtext[16 + i];
		prepend = ((cclist[i] & 0xFF) == 0x80);
		int end = 17 + i;
		if(mtext.length > end) {
			prepend_append = BufferUtils.extractString(mtext, end, mtext.length - end);
		}
	}

	Distress(NetrekFrame view, Universe data, int type) {
		distress_type = (byte)type;
		if(distress_type < TAKE || distress_type > GENERIC) {
			distress_type = GENERIC;
		}

		sender = data.me;
		damage = (100 * data.me.damage) / data.me.ship.maxdamage;
		shields = (100 * data.me.shield) / data.me.ship.maxshield;
		armies = data.me.armies;
		fuel_percentage = (100 * data.me.fuel) / data.me.ship.maxfuel;
		wtemp = (100 * data.me.wtemp) / data.me.ship.maxwpntemp;
		etemp = (100 * data.me.etemp) / data.me.ship.maxegntemp;
		short_status = (data.me.flags & 0xFF) | 0x80;
		wtemp_flag = ((data.me.flags & Player.WEP) != 0);
		etemp_flag= ((data.me.flags & Player.ENG) != 0);
		cloak_flag = ((data.me.flags & Player.CLOAK) != 0);

		close_planet = (Planet)data.getTarget(Universe.TARG_PLANET);
		close_player = (Player)data.getTarget(Universe.TARG_PLAYER);
		close_friend = (Player)data.getTarget(Universe.TARG_FRIEND);
		close_enemy = (Player)data.getTarget(Universe.TARG_ENEMY);

		target_planet = (Planet)view.getTarget(Universe.TARG_PLANET);		
		target_player = (Player)view.getTarget(Universe.TARG_PLAYER);
		target_friend = (Player)view.getTarget(Universe.TARG_FRIEND);
		target_enemy = (Player)view.getTarget(Universe.TARG_ENEMY);	

		i = 0;
		macro_flag = false;
		cclist[i] = (byte)0x80;
		prepend_append = "";
	}

	Distress(Player sender, Player target_player, int armies, 
		int damage, int shields, Planet target_planet, int wtemp) {
		this.distress_type = RCM;
		this.sender = sender;
		this.target_player = target_player;
		this.armies = armies;
		this.damage = damage;
		this.shields = shields;
		this.target_planet = target_planet;
		this.wtemp = wtemp;
	}

	public String toMessage() {
		int length = 16;
		char[] buffer = new char[80];
		buffer[0] = (char)((macro_flag ? 1 : 0) << 5 | distress_type);
		buffer[1] = (char)(fuel_percentage | 0x80);
		buffer[2] = (char)(damage | 0x80);
		buffer[3] = (char)(shields | 0x80);
		buffer[4] = (char)(etemp | 0x80);
		buffer[5] = (char)(wtemp | 0x80);
		buffer[6] = (char)(armies | 0x80);
		buffer[7] = (char)(short_status | 0x80);
		buffer[8] = (char)(close_planet.no | 0x80);
		buffer[9] = (char)(close_enemy.no | 0x80);
		buffer[10] = (char)(target_planet.no | 0x80);
		buffer[11] = (char)(target_enemy.no | 0x80);
		buffer[12] = (char)(target_player.no | 0x80);
		buffer[13] = (char)(close_player.no | 0x80);
		buffer[14] = (char)(target_friend.no | 0x80);
		buffer[15] = (char)(close_friend.no | 0x80);

		// cclist better be terminated properly otherwise we hose here
		int i = 0;
		while (((cclist[i] & 0xc0) == 0xc0)) {
			buffer[16 + i] = (char)(cclist[i] & 0xFF);
			i++;
		}
		// get the pre/append cclist terminator in there
		buffer[16 + i] = (char)(cclist[i] & 0xFF);
		buffer[17 + i] = '\0';

		length = 17 + i;
		if (prepend_append.length() > 0) {
			prepend_append.getChars(0, prepend_append.length(), buffer, 17 + i);
			length += prepend_append.length();
		}
		return new String(buffer, 0, length);
	}
}
