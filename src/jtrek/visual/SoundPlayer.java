package jtrek.visual;

import jtrek.Copyright;

public class SoundPlayer {
	public final static int NO_SOUND				= -1;
	public final static int FIRE_TORP_SOUND			= 0;
	public final static int PHASER_SOUND			= 1;
	public final static int FIRE_PLASMA_SOUND		= 2;
	public final static int EXPLOSION_SOUND			= 3;
	public final static int CLOAK_SOUND				= 4;
	public final static int UNCLOAK_SOUND			= 5;
	public final static int SHIELD_DOWN_SOUND		= 6;
	public final static int SHIELD_UP_SOUND			= 7;
	public final static int TORP_HIT_SOUND			= 8;
	public final static int WARNING_SOUND			= 9;
	public final static int ENGINE_SOUND			= 10;
	public final static int ENTER_SHIP_SOUND		= 11;
	public final static int SELF_DESTRUCT_SOUND		= 12;
	public final static int PLASMA_HIT_SOUND		= 13;
	public final static int MESSAGE_SOUND			= 14;
	public final static int MESSAGE1_SOUND			= 15;
	public final static int MESSAGE2_SOUND			= 16;
	public final static int MESSAGE3_SOUND			= 17;
	public final static int MESSAGE4_SOUND			= 18;
	public final static int MESSAGE5_SOUND			= 19;
	public final static int MESSAGE6_SOUND			= 20;
	public final static int MESSAGE7_SOUND			= 21;
	public final static int MESSAGE8_SOUND			= 22;
	public final static int MESSAGE9_SOUND			= 23;
	public final static int OTHER_FIRE_TORP_SOUND	= 24;
	public final static int OTHER_PHASER_SOUND		= 25;
	public final static int OTHER_FIRE_PLASMA_SOUND	= 26;
	public final static int OTHER_EXPLOSION_SOUND	= 27;
	
	boolean all_sounds_on = true;
	boolean[] sounds_on = new boolean[28];
	SoundEngine sound_engine = null;
	
	public SoundPlayer() {
		for(int i = sounds_on.length; --i >= 0;) {
			sounds_on[i] = true;
		}
	}
	
	public void setSoundEngine(SoundEngine sound_engine) {
		this.sound_engine = sound_engine;
	}
	
	public void playSound(int sound) {
		if(sound_engine != null && all_sounds_on && sounds_on[sound]) {
			sound_engine.playSound(sound);
		}
	}
	
	public void abortSound(int sound) {
		if(sound_engine != null) {
			sound_engine.abortSound(sound);
		}
	}
}
