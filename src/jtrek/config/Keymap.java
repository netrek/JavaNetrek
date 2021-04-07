package jtrek.config;

import jtrek.Copyright;

public class Keymap {
	public static char[] keymap = new char[256];
	static {
		for(int c = 255; c >= 0; --c) {
			keymap[c] = (char)c;
		}
		keymap[1] = 'k';
		keymap[2] = 't';
		keymap[3] = 'p';
	}
	
	/** mapButtons */
	public static void mapButtons(String string) {
		if((string.length() % 2) != 0) {
			string = string.substring(0, string.length() - 1);	
		}
		char[] buttons = string.toCharArray();
		for(int b = 0; b < buttons.length; ++b) {
			if(buttons[b] == '1') {
				keymap[1] = buttons[++b];
			}
			else if(buttons[b] == '2') {
				keymap[2] = buttons[++b];
			}
			else if(buttons[b] == '3') {
				keymap[3] = buttons[++b];
			}
			else {
				++b;
			}
		}
	}

	/** mapKeys */
	public static void mapKeys(String string) {
		try {
			char[] keys = string.toCharArray();
			for(int k = 0; k < keys.length; ++k) {
				keymap[keys[k]] = keys[++k];
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("Some characters at the end of keymap ignored");
		}
	}


	/** mapKeys */
	public static void mapControlKeys(String string) {
		try {
			char[] keys = string.toCharArray();
			for(int k = 0; k < keys.length; ++k) {
				// figure out what key to map to
				int dest = 0;
				if(keys[k] == '^') {
					dest += 128;
					++k;
				}
				dest += keys[k];

				// figure out what key to map from
				int src = 0;
				if(keys[++k] == '^') {
					src += 128;
					++k;
				}
				src += keys[k];

				keymap[dest] = (char)src;
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.err.println("Some characters at the end of ckeymap ignored");
		}
	}
}
