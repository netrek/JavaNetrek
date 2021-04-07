package jtrek.data;

import jtrek.Copyright;

public class Rank {
	public final static Rank[] RANKS = { 
			new Rank( 0f, 0f, 0.0f, "Ensign"),
			new Rank( 2f, 1f, 0.0f, "Lieutenant"),
			new Rank( 4f, 2f, 0.8f, "Lt. Cmdr."),
			new Rank( 8f, 3f, 0.8f, "Commander"),
			new Rank(15f, 4f, 0.8f, "Captain"),
			new Rank(20f, 5f, 0.8f, "Flt. Capt."),
			new Rank(25f, 6f, 0.8f, "Commodore"),
			new Rank(30f, 7f, 0.8f, "Rear Adm."),
			new Rank(40f, 8f, 0.8f, "Admiral")
		};

		private Rank() {}

		private Rank(float hours, float ratings, float defense, String name) {
			this.name = name;
			this.defense = defense;
			this.ratings = ratings;
			this.hours = hours;
		}

		public float hours; 
		public float ratings;
		public float defense;
		public String name;
}
