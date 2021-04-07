package jtrek.data;

import jtrek.Copyright;
import java.awt.*;

public class Team {
	public final static int NOBODY = 0;
	public final static int IND = 0;
	public final static int FED = 1;
	public final static int ROM = 2;
	public final static int KLI = 3;
	public final static int ORI = 4;

	public final static Team[] TEAMS = new Team[5];
	static {
		TEAMS[IND] = new Team();
		TEAMS[IND].no = (byte)0;
		TEAMS[IND].bit = (byte)0x0;		
		TEAMS[IND].letter = 'I';
		TEAMS[IND].abbrev = "IND";
		TEAMS[IND].color = Color.lightGray;

		TEAMS[FED] = new Team();
		TEAMS[FED].no = (byte)1;
		TEAMS[FED].bit = (byte)0x1;
		TEAMS[FED].letter = 'F';
		TEAMS[FED].abbrev = "FED";
		TEAMS[FED].color = Color.yellow;

		TEAMS[ROM] = new Team();
		TEAMS[ROM].no = (byte)2;
		TEAMS[ROM].bit = (byte)0x2;
		TEAMS[ROM].letter = 'R';
		TEAMS[ROM].abbrev = "ROM";
		TEAMS[ROM].color = Color.red;

		TEAMS[KLI] = new Team();
		TEAMS[KLI].no = (byte)3;
		TEAMS[KLI].bit = (byte)0x4;
		TEAMS[KLI].letter = 'K';
		TEAMS[KLI].abbrev = "KLI";
		TEAMS[KLI].color = Color.green;

		TEAMS[ORI] = new Team();										
		TEAMS[ORI].no = (byte)4;
		TEAMS[ORI].bit = (byte)0x8;
		TEAMS[ORI].letter = 'O';
		TEAMS[ORI].abbrev = "ORI";
		TEAMS[ORI].color = Color.cyan;
	}
	public final static Team[] TEAM_REMAP = {
		TEAMS[IND], TEAMS[FED], TEAMS[ROM], TEAMS[IND],
		TEAMS[KLI], TEAMS[IND], TEAMS[IND], TEAMS[IND],
		TEAMS[ORI], TEAMS[IND], TEAMS[IND], TEAMS[IND],
		TEAMS[IND], TEAMS[IND], TEAMS[IND], TEAMS[IND]
	};	

	public int no;
	public int bit;
	public char letter;
	public String abbrev;
	public Color color;

	public Color getColor() {
		return color;
	}

	private Team() {}

}
