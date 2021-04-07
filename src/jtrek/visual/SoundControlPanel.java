package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.config.Defaults;
import jtrek.Copyright;

public class SoundControlPanel extends BasePanel implements ActionListener {
	MenuPanel menu;
	SoundPlayer sound_player;
	
	/** 
	 * OptionsPanel 
	 */
	public SoundControlPanel(String name, SoundPlayer sound_player) {
		super(name);
		this.sound_player = sound_player;
		MenuItem[] items = new MenuItem[18];
		ChoiceMenuItem c;
		items[0] = c = new ChoiceMenuItem(-1);
		c.addItem("Sound is turned ON");
		c.addItem("Sound is turned OFF");
		c.setSelectedIndex(Defaults.sound ? 0 : 1);
		items[1] = c = new ChoiceMenuItem(0);
		c.addItem("Fire torp sound is ON");
		c.addItem("Fire torp sound is OFF");
		items[2] = c = new ChoiceMenuItem(1);
		c.addItem("Phaser sound is ON");
		c.addItem("Phaser sound is OFF");
		items[3] = c = new ChoiceMenuItem(2);
		c.addItem("Fire plasma sound is ON");
		c.addItem("Fire plasma sound is OFF");
		items[4] = c = new ChoiceMenuItem(3);
		c.addItem("Explosion sound is ON");
		c.addItem("Explosion sound is OFF");
		items[5] = c = new ChoiceMenuItem(4);
		c.addItem("Cloak sound is ON");
		c.addItem("Cloak sound is OFF");
		items[6] = c = new ChoiceMenuItem(5);
		c.addItem("Uncloak sound is ON");
		c.addItem("Uncloak sound is OFF");
		items[7] = c = new ChoiceMenuItem(6);
		c.addItem("Shield down sound is ON");
		c.addItem("Shield down sound is OFF");
		items[8] = c = new ChoiceMenuItem(7);
		c.addItem("Shield up sound is ON");
		c.addItem("Shield up sound is OFF");
		items[9] = c = new ChoiceMenuItem(8);
		c.addItem("Torp hit sound is ON");
		c.addItem("Torp hit sound is OFF");
		items[10] = c = new ChoiceMenuItem(9);
		c.addItem("Warning sound is ON");
		c.addItem("Warning sound is OFF");
		items[11] = c = new ChoiceMenuItem(10);
		c.addItem("Engine sound is ON");
		c.addItem("Engine sound is OFF");
		items[12] = c = new ChoiceMenuItem(11);
		c.addItem("Enter ship sound is ON");
		c.addItem("Enter ship sound is OFF");
		items[13] = c = new ChoiceMenuItem(12);
		c.addItem("Self destruct sound is ON");
		c.addItem("Self destruct sound is OFF");
		items[14] = c = new ChoiceMenuItem(13);
		c.addItem("Plasma hit sound is ON");
		c.addItem("Plasma hit sound is OFF");
		items[15] = c = new ChoiceMenuItem(14);
		c.addItem("Message sound is ON");
		c.addItem("Message sound is OFF");
		items[16] = c = new ChoiceMenuItem(15);
		c.addItem("Other ships sound is ON");
		c.addItem("Other ships sound is OFF");
		items[17] = new ButtonMenuItem(16, "Done");		
		menu = new MenuPanel("sound controls", items);
		add(menu);
		menu.addMenuItems();
		for(int i = 0; i < items.length; ++i) {
			items[i].addActionListener(this);
		}		
	}
	
	//public void addKeyListener(KeyListener l) {
	//	super.addKeyListener(l);
	//	menu.addKeyListener(l);	
	//}
	
	/** setBounds */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		menu.setBounds(0, 0, width, height);
	}
	
	public void actionPerformed(ActionEvent e) {
		int id = e.getID();
		boolean play_sound = false;
		switch(id) {
		case -1:
			sound_player.all_sounds_on = !sound_player.all_sounds_on; 
			break;
		case 14:
			for(int i = 14; i <= 23; ++i) {
				sound_player.sounds_on[i] = !sound_player.sounds_on[i];
			}
			play_sound = sound_player.sounds_on[14];
			break;
		case 15:
			for(int i = 24; i <= 27; ++i) {
				sound_player.sounds_on[i] = !sound_player.sounds_on[i];
			}
			play_sound = sound_player.sounds_on[24];
			break;
		case 16:
			setVisible(false);
			break;
		default:
			sound_player.sounds_on[id] = !sound_player.sounds_on[id];
			play_sound = sound_player.sounds_on[id];
		}
		if(play_sound) {
			sound_player.playSound(id);
		}
	}
}
