package jtrek.visual;

import jtrek.data.Universe;

public class Dashboard extends OffscreenPanel {
	Universe data;
	
	public Dashboard() {
		super("tstat");
	}

	public void setUniverse(Universe data) {
		this.data = data;
	}
}
