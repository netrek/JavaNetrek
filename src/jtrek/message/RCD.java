package jtrek.message;

import java.util.*;
import jtrek.Copyright;
import jtrek.config.*;

public class RCD extends RCM implements Cloneable {
	final static int CTRL = 128;

	// using control with non-alphabetic keys didn't work in Java.  When non-alphabetic keys where pressed
	// with control down, the key event always came back as "KeyEvent.UNDEFINED" it was possible to hack
	// this to get numeric keys to work, but not other keys. So I decided to just leave it working with
	// only alphabetic keys.  That is why some of the keys here, like '1' & '#', etc don't have control
	// with them like they do in other clients.
	public final static RCD[] DEFAULTS = {
		new RCD("no zero", 'X', "this should never get looked at"),
		new RCD("taking", 't' + CTRL, " %T%c@%b (%S) Carrying %a to %l%?%n>-1%{ @ %n%}"),
		new RCD("ogg", 'o' + CTRL, " %T%c@%b Help Ogg %p at %l"),
		new RCD("bomb", 'b' + CTRL, " %T%c@%b %?%n>4%{bomb %l @ %n%!bomb%}"),
		new RCD("space_control", 'c' + CTRL, " %T%c@%b Help Control at %L"),
		new RCD("save_planet", '1', " %T%c@%b Emergency at %L!!!!"),
		new RCD("base_ogg", '2', " %T%c@%b Sync with --]> %g <[-- OGG ogg OGG base!!"),
		new RCD("help1", '3', " %T%c@%b Help me! %d%% dam, %s%% shd, %f%% fuel %a armies."),
		new RCD("help2", '4', " %T%c@%b Help me! %d%% dam, %s%% shd, %f%% fuel %a armies."),
		new RCD("escorting", 'e' + CTRL, " %T%c@%b ESCORTING %g (%d%%D %s%%S %f%%F)"),
		new RCD("ogging", 'p' + CTRL, " %T%c@%b Ogging %h"),
		new RCD("bombing", 'm' + CTRL, " %T%c@%b Bombing %l @ %n"),
		new RCD("controlling", 'l' + CTRL, " %T%c@%b Controlling at %l"),
		new RCD("asw", '5', " %T%c@%b Anti-bombing %p near %b."),
		new RCD("asbomb", '6', " %T%c@%b DON'T BOMB %l. Let me bomb it (%S)"),
		new RCD("doing1", '7', " %T%c@%b (%i)%?%a>0%{ has %a arm%?%a=1%{y%!ies%}%} at %l.  (%d%% dam, %s%% shd, %f%% fuel)"),
		new RCD("doing2", '8', " %T%c@%b (%i)%?%a>0%{ has %a arm%?%a=1%{y%!ies%}%} at %l.  (%d%% dam, %s%% shd, %f%% fuel)"),
		new RCD("free_beer", 'f' + CTRL, " %T%c@%b %p is free beer"),
		new RCD("no_gas", 'n' + CTRL, " %T%c@%b %p @ %l has no gas"),
		new RCD("crippled", 'h' + CTRL, " %T%c@%b %p @ %l crippled"),
		new RCD("pickup", '9', " %T%c@%b %p++ @ %l"),
		new RCD("pop", '0', " %T%c@%b %l%?%n>-1%{ @ %n%}!"),
		new RCD("carrying", 'F', " %T%c@%b %?%S=SB%{Your Starbase is c%!C%}arrying %?%a>0%{%a%!NO%} arm%?%a=1%{y%!ies%}."),
		new RCD("other1", '@', " %T%c@%b (%i)%?%a>0%{ has %a arm%?%a=1%{y%!ies%}%} at %l. (%d%%D, %s%%S, %f%%F)"),
		new RCD("other2", '#', " %T%c@%b (%i)%?%a>0%{ has %a arm%?%a=1%{y%!ies%}%} at %l. (%d%%D, %s%%S, %f%%F)"),
		new RCD("help", 'E', " %T%c@%b Help(%S)! %s%% shd, %d%% dmg, %f%% fuel,%?%S=SB%{ %w%% wtmp,%!%}%E%{ ETEMP!%}%W%{ WTEMP!%} %a armies!"),
	};

	public String lite;

	public RCD(String name, int key, String macro) {
		super(name, key, macro);
		this.lite = null;
	}

    public Object clone() {
		RCD rcd = (RCD)super.clone();
		if(lite != null) {
			rcd.lite = new String(lite);
		}
		return rcd;
    }
}
