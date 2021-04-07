package jtrek.message;

import jtrek.Copyright;

public interface MessageConstants {
	int VALID		= 0x01;
	int GOD			= 0x10;
	int MOO			= 0x12;

	// order flags by importance (0x100 - 0x400) 
	// restructuring of message flags to squeeze them all into 1 int - jmn 
	// hopefully quasi-back-compatible:
	// MVALID, MINDIV, MTEAM, MALL, MGOD use up 5 bits. this leaves us 3 bits.
	// since the server only checks for those flags when deciding message
	// related things and since each of the above cases only has 1 flag on at
	// a time we can overlap the meanings of the flags 

	int INDIV		= 0x02;
	// these go with MINDIV flag 
	int DBG			= 0x20;
	int CONFIG		= 0x40;
	int DIST		= 0x60;
	int MACRO		= 0x80;
	int TEAM		= 0x04;
	// these go with MTEAM flag
	int TAKE		= 0x20;
	int DEST		= 0x40;
	int BOMB		= 0x60;

	int ALL			= 0x08;
	// these go with MALL flag
	int CONQ		= 0x20;
	int KILL		= 0x80;
	int DISTR		= 0xC0;
	int PHASER		= 0x100;
}
