package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;
import jtrek.config.Keymap;

public class HelpPanel extends BasePanel {

	final static int MAX_HELP_LENGTH = 40;

	final static String[] help_message = {
		"0     Set speed",
		"1     Set speed",
		"2     Set speed",
		"3     Set speed",
		"4     Set speed",
		"5     Set speed",
		"6     Set speed",
		"7     Set speed",
		"8     Set speed",
		"9     Set speed",
		")     speed = 10",
		"!     speed = 11",
		"@     speed = 12",
		"%     speed = maximum",
		"#     speed = 1/2 maximum",
		"<     slow speed 1",
		">     speed up 1",
		"k     Set course",
		"p     Fire phaser",
		"t     Fire photon torpedo",
		"f     Fire plasma torpedo",
		"d     detonate enemy torps",
		"D     detonate own torps",
		"L     List players",
		"P     List planets",
		"S     Status graph toggle",
		"]     Put up shields",
		"[     Put down shields",
		"u     Shield toggle",
		"s     Shield toggle",
		"i     Info on player/planet",
		"I     Extended info on player",
		"b     Bomb planet",
		"z     Beam up armies",
		"x     Beam down armies",
		"{     Cloak",
		"}     Uncloak",
		"T     Toggle tractor beam",
		"y     Toggle pressor beam",
		"_     Turn on tractor beam",
		"^     Turn on pressor beam",
		"$     Turn off tractor/pressor beam",
		"R     Enter repair mode",
		"o     Orbit planet or dock to outpost",
		"e     Docking permission toggle",
		"r     Refit (change ship type)",
		"Q     Quit",
		"q     Fast Quit",
		"?     Message window toggle",
		"c     Cloaking device toggle",
		"l     Lock on to player/planet",
		";     Lock on to planet",
		"h     Help window toggle",
		"w     War declarations window",
		"N     Planet names toggle",
		"V     Rotate local planet display",
		"B     Rotate galactic planet display",
		"*     Send in practice robot",
		"E     Send Distress signal",
		"F     Send armies carried report",
		"U     Show rankings window",
		"m     Message Window Zoom",
		"/     Toggle sorted player list",
		":     Toggle message logging",
		"+     Show UDP options window",
		"=     Update all",
		",     Ping stats window",
		".     NetstatWindow",
		"\\     LagMeter",
		"`      Toggle PacketWindow",
		"-     Update small",
		"|     Update medium",
		"      (space) Unmap special windows",
		"X     Enter Macro Mode",
		"X?    Show current Macros",
//		"~     Sound control window",
		"&     Reread .xtrekrc",
	};

	HelpPanel(String name) {
		super(name);
	}

	/**
	* update
	*/
	public void update(Graphics g) {
		FontMetrics metrics = g.getFontMetrics();

		int column_width = metrics.charWidth(' ') * MAX_HELP_LENGTH + 2;
		int row_height = metrics.getAscent() + 1;

		int x = 8;

		for(int i = 0, column = 0; column < 3 && i < help_message.length; ++column) {
			int y = row_height + 26;
			for(int row = 0; row < help_message.length / 3 && i < help_message.length; ++row, ++i) {
				g.drawString(updateKeyToKeymap(help_message[i]), x, y);
				y += row_height;
			}
			x += column_width;
		}
	}

	String updateKeyToKeymap(String message) {
		char key = message.charAt(0);

		if(key < 32 || key > 126 || message.length() < 6) {
			return message;
		}
				 
		int max = 4;
		int slot = 0;
		char[] buffer = message.toCharArray();

		for(int i = ' '; i <= '~' && slot < max; ++i) {
			if(i != key && Keymap.keymap[i] == key) {
				buffer[++slot] = (char)i;
			}
		}
		for(int i = ' ' + 128; i <= '~' && slot < max - 1; ++i) {
			if(Keymap.keymap[i] == key) {
				buffer[++slot] = '^';
				buffer[++slot] = (char)i;
			}
		}
		return new String(buffer);
	}
}