package jtrek.visual;

import jtrek.data.Universe;

public abstract class PlayerListPanel extends OffscreenPanel {

	Universe data;
	
	public PlayerListPanel() {
		super("player");
	}

	public void setUniverse(Universe data) {
		this.data = data;
	}
}