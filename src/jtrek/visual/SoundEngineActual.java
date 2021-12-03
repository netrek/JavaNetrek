package jtrek.visual;

import java.io.File;

import javax.sound.sampled.*;

public class SoundEngineActual implements SoundEngine {
	final static int clipCount = 28;
	final static boolean DEBUG_SOUND = false;
    Clip[] audio_clips = new Clip[clipCount];
    
    private Clip getAudioClip(String filename) {
    	try {
    		File audioFile = new File(filename);
    		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
    		Clip audioClip = AudioSystem.getClip();
    		audioClip.open(audioStream);
    		return audioClip;
    	} catch (Exception e) {
        	try {
        		String filenameDevelop = "src/" + filename;
        		File audioFile = new File(filenameDevelop);
        		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        		Clip audioClip = AudioSystem.getClip();
        		audioClip.open(audioStream);
        		return audioClip;
        	} catch (Exception f) {
        		if (DEBUG_SOUND) {
        			System.err.printf("getAudioClip exception filename %s %s %s\n",filename,e.getMessage(),f.getMessage());
        		}
        		return null;
        	}
    	}
    }
    
    public SoundEngineActual() {
    	for (int i = 0; i < clipCount; i++) {
    		try {
    			audio_clips[i] = AudioSystem.getClip();
    		} catch (Exception e) {
    			if (DEBUG_SOUND) {
    				System.err.printf("SoundEngineActual exception %s\n", e.getMessage());
    			}
    		}
    	}
        audio_clips[SoundPlayer.FIRE_TORP_SOUND] = getAudioClip("jtrek/resources/175266__jonccox__gun-spark.wav");
        audio_clips[SoundPlayer.PHASER_SOUND] = getAudioClip("jtrek/resources/175262__jonccox__gun-cannon.wav");
        audio_clips[SoundPlayer.FIRE_PLASMA_SOUND] = getAudioClip("jtrek/resources/175267__jonccox__gun-plasma.wav");
        audio_clips[SoundPlayer.EXPLOSION_SOUND] = getAudioClip("jtrek/resources/399303__deleted-user-5405837__explosion-012.wav");
        audio_clips[SoundPlayer.CLOAK_SOUND] = getAudioClip("jtrek/resources/71852__ludvique__digital-whoosh-soft.wav");
        audio_clips[SoundPlayer.UNCLOAK_SOUND] = getAudioClip("jtrek/resources/71852__ludvique__digital-whoosh-soft.wav");
        audio_clips[SoundPlayer.SHIELD_DOWN_SOUND] = getAudioClip("jtrek/resources/71852__ludvique__digital-whoosh-soft.wav");
        audio_clips[SoundPlayer.SHIELD_UP_SOUND] = getAudioClip("jtrek/resources/71852__ludvique__digital-whoosh-soft.wav");
        audio_clips[SoundPlayer.TORP_HIT_SOUND] = getAudioClip("jtrek/resources/175270__jonccox__gun-zap.wav");
        audio_clips[SoundPlayer.WARNING_SOUND] = getAudioClip("jtrek/resources/warning.au");
        audio_clips[SoundPlayer.ENGINE_SOUND] = getAudioClip("jtrek/resources/engine.au");
        audio_clips[SoundPlayer.ENTER_SHIP_SOUND] = getAudioClip("jtrek/resources/enter_ship.au");
        audio_clips[SoundPlayer.SELF_DESTRUCT_SOUND] = getAudioClip("jtrek/resources/399303__deleted-user-5405837__explosion-012.wav");
        audio_clips[SoundPlayer.PLASMA_HIT_SOUND] = getAudioClip("jtrek/resources/399303__deleted-user-5405837__explosion-012.wav");
        audio_clips[SoundPlayer.MESSAGE_SOUND] = getAudioClip("jtrek/resources/message.au");
        audio_clips[SoundPlayer.OTHER_FIRE_TORP_SOUND] = getAudioClip("jtrek/resources/175269__jonccox__gun-zap2.wav");
        audio_clips[SoundPlayer.OTHER_PHASER_SOUND] = getAudioClip("jtrek/resources/fire_phaser_other.au");
        audio_clips[SoundPlayer.OTHER_FIRE_PLASMA_SOUND] = getAudioClip("jtrek/resources/175267__jonccox__gun-plasma.wav");
        audio_clips[SoundPlayer.OTHER_EXPLOSION_SOUND] = getAudioClip("jtrek/resources/399303__deleted-user-5405837__explosion-012.wav");
        
        for(int i = SoundPlayer.MESSAGE1_SOUND; i <= SoundPlayer.MESSAGE9_SOUND; ++i) {
                audio_clips[i] = audio_clips[SoundPlayer.MESSAGE_SOUND];
        }
    }
    
    public void playSound(int sound) {
    	if (DEBUG_SOUND) {
        	System.err.printf("playing sound %d\n",sound);
    	}
    	if (audio_clips[sound] != null) {
    		audio_clips[sound].stop();
    		audio_clips[sound].setMicrosecondPosition(0);
    		audio_clips[sound].start();
    	} else {
    		if (DEBUG_SOUND) {
    			System.err.printf("sound %d is null\n", sound);
    		}
    	}
    }
    
    public void abortSound(int sound) {
    	if (audio_clips[sound] != null) {
           audio_clips[sound].stop();
    	}
    }

}
