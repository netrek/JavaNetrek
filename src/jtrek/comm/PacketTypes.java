package jtrek.comm;

import jtrek.Copyright;

public interface PacketTypes {
	public final static byte SP_MESSAGE			= 1;	
	public final static byte SP_PLAYER_INFO		= 2;	// general player info not elsewhere 
	public final static byte SP_KILLS				= 3;	// # kills a player has 
	public final static byte SP_PLAYER				= 4;	// x,y for player 
	public final static byte SP_TORP_INFO			= 5;	// torp status 
	public final static byte SP_TORP				= 6;	// torp location 
	public final static byte SP_PHASER				= 7;	// phaser status and direction 
	public final static byte SP_PLASMA_INFO		= 8;	// player login information 
	public final static byte SP_PLASMA				= 9;	// like SP_TORP 
	public final static byte SP_WARNING			= 10;	// like SP_MESG 
	public final static byte SP_MOTD				= 11;	// line from .motd screen 
	public final static byte SP_YOU				= 12;	// info on you? 
	public final static byte SP_QUEUE				= 13;	// estimated loc in queue? 
	public final static byte SP_STATUS				= 14;	// galaxy status numbers 
	public final static byte SP_PLANET				= 15;	// planet armies & facilities 
	public final static byte SP_PICKOK				= 16;	// your team & ship was accepted 
	public final static byte SP_LOGIN				= 17;	// login response 
	public final static byte SP_FLAGS				= 18;	// give flags for a player 
	public final static byte SP_MASK				= 19;	// tournament mode mask 
	public final static byte SP_PSTATUS			= 20;	// give status for a player 
	public final static byte SP_BADVERSION			= 21;	// invalid version number 
	public final static byte SP_HOSTILE			= 22;	// hostility settings for a player 
	public final static byte SP_STATS				= 23;	// a player's statistics 
	public final static byte SP_PL_LOGIN			= 24;	// new player logs in 
	public final static byte SP_RESERVED			= 25;	// for future use      $$$ IS THIS REALLY FOR FUTURE USE???
	public final static byte SP_PLANET_LOC			= 26;	// planet name, x, y 
	public final static byte SP_SCAN				= 27;	// ATM: results of player scan 
	public final static byte SP_UDP_REPLY			= 28;	// notify client of UDP status 
	public final static byte SP_SEQUENCE			= 29;	// sequence # packet 
	public final static byte SP_SC_SEQUENCE		= 30;	// this trans is semi-critical info 
	public final static byte SP_RSA_KEY			= 31;	// handles binary verification 
	public final static byte SP_MOTD_PIC			= 32;	// motd bitmap pictures (paradise) 
	// 33 - 38 are apparently not used
	public final static byte SP_SHIP_CAP			= 39;	// Handles server ship mods 
	public final static byte SP_S_REPLY			= 40;	// reply to send-short request 
	public final static byte SP_S_MESSAGE			= 41;	// var. Message Packet 
	public final static byte SP_S_WARNING			= 42;	// Warnings with = 4  Bytes 
	public final static byte SP_S_YOU				= 43;	// hostile,armies,whydead,etc .. 
	public final static byte SP_S_YOU_SS			= 44;	// your ship status 
	public final static byte SP_S_PLAYER			= 45;	// variable length player packet 
	public final static byte SP_PING				= 46;	// ping packet 
	public final static byte SP_S_TORP				= 47;	// variable length torp packet 
	public final static byte SP_S_TORP_INFO		= 48;	// SP_S_TORP with TorpInfo 
	public final static byte SP_S_8_TORP			= 49;	// optimized SP_S_TORP 
	public final static byte SP_S_PLANET			= 50;	// see SP_PLANET 
	// 51 - 55 are apparently no used
	public final static byte SP_S_SEQUENCE			= 56;	// SP_SEQUENCE for compressed packets 
	public final static byte SP_S_PHASER			= 57;	// see struct 
	public final static byte SP_S_KILLS			= 58;	// # of kills player have 
	public final static byte SP_S_STATS			= 59;	// see SP_STATS 
	public final static byte SP_FEATURE			= 60;
	public final static byte SP_BITMAP				= 61;

	// packets sent from remote client to xtrek server
	public final static byte CP_MESSAGE			= 1;	// send a message 
	public final static byte CP_SPEED				= 2;	// set speed 
	public final static byte CP_DIRECTION			= 3;	// change direction 
	public final static byte CP_PHASER				= 4;	// phaser in a direction 
	public final static byte CP_PLASMA				= 5;	// plasma (in a direction) 
	public final static byte CP_TORP				= 6;	// fire torp in a direction 
	public final static byte CP_QUIT				= 7;	// self destruct 
	public final static byte CP_LOGIN				= 8;	// log in (name, password) 
	public final static byte CP_OUTFIT				= 9;	// outfit to new ship 
	public final static byte CP_WAR 				= 10;	// change war status 
	public final static byte CP_PRACTR				= 11;	// create practice robot? 
	public final static byte CP_SHIELD				= 12;	// raise/lower sheilds 
	public final static byte CP_REPAIR				= 13;	// enter repair mode 
	public final static byte CP_ORBIT				= 14;	// orbit planet/starbase 
	public final static byte CP_PLANLOCK			= 15;	// lock on planet 
	public final static byte CP_PLAYLOCK			= 16;	// lock on player 
	public final static byte CP_BOMB				= 17;	// bomb a planet 
	public final static byte CP_BEAM				= 18;	// beam armies up/down 
	public final static byte CP_CLOAK				= 19;	// cloak on/off 
	public final static byte CP_DET_TORPS			= 20;	// detonate enemy torps 
	public final static byte CP_DET_MYTORP			= 21;	// detonate one of my torps 
	public final static byte CP_COPILOT 			= 22;	// toggle copilot mode 
	public final static byte CP_REFIT				= 23;	// refit to different ship type 
	public final static byte CP_TRACTOR 			= 24;	// tractor on/off 
	public final static byte CP_REPRESS 			= 25;	// pressor on/off 
	public final static byte CP_COUP				= 26;	// coup home planet 
	public final static byte CP_SOCKET				= 27;	// new socket for reconnection 
	public final static byte CP_OPTIONS 			= 28;	// send my options to be saved 
	public final static byte CP_BYE 				= 29;	// I'm done! 
	public final static byte CP_DOCKPERM			= 30;	// set docking permissions 
	public final static byte CP_UPDATES 			= 31;	// set number of usecs per update 
	public final static byte CP_RESETSTATS			= 32;	// reset my stats packet 
	public final static byte CP_RESERVED			= 33;	// for future use 
	public final static byte CP_SCAN				= 34;	// ATM 
	public final static byte CP_UDP_REQ 			= 35;	// request UDP on/off 
	public final static byte CP_SEQUENCE			= 36;	// sequence # packet 
	public final static byte CP_RSA_KEY 			= 37;	// handles binary verification 
	public final static byte CP_PING_RESPONSE		= 42;	// client response 
	public final static byte CP_S_REQ				= 43;
	public final static byte CP_S_THRS				= 44;
	public final static byte CP_S_MESSAGE			= 45;	// vari. Message Packet 
	public final static byte CP_S_RESERVED			= 46;
	public final static byte CP_S_DUMMY 			= 47;
	public final static byte CP_FEATURE 			= 60;

	public final static byte VPLAYER_SIZE			= 4;
	public final static byte SHORTVERSION			= 11;	// other number blocks, like UDP Version 
	public final static byte OLDSHORTVERSION		= 10;	// S_P2 
	public final static byte SOCKVERSION			= 4;
	public final static byte UDPVERSION 			= 10;

	// short packets
	public final static byte SPK_VOFF				= 0;	// variable packets off
	public final static byte SPK_VON				= 1;	// variable packets on
	public final static byte SPK_MOFF				= 2;	// message packets off
	public final static byte SPK_MON				= 3;	// message packets on
	public final static byte SPK_M_KILLS			= 4;	// send kill mesgs
	public final static byte SPK_M_NOKILLS			= 5;	// don't send kill mesgs
	public final static byte SPK_THRESHOLD			= 6;	// threshold
	public final static byte SPK_M_WARN 			= 7;	// warnings
	public final static byte SPK_M_NOWARN			= 8;	// no warnings
	public final static byte SPK_SALL				= 9;	// only planets,kills and weapons
	public final static byte SPK_ALL				= 10;	// Full Update - SP_STATS
	public final static byte SPK_NUMFIELDS			= 6;

	final static byte SPK_VFIELD 			= 0;
	final static byte SPK_MFIELD 			= 1;
	final static byte SPK_KFIELD 			= 2;
	final static byte SPK_WFIELD 			= 3;
	final static byte SPK_TFIELD 			= 4;
	final static byte SPK_DONE				= 5;

	// UDP control stuff
	final static byte UDP_NUMOPTS			= 10;
	final static byte UDP_CURRENT			= 0;
	final static byte UDP_STATUS 			= 1;
	final static byte UDP_DROPPED			= 2;
	final static byte UDP_SEQUENCE			= 3;
	final static byte UDP_SEND				= 4;
	final static byte UDP_RECV				= 5;
	final static byte UDP_DEBUG				= 6;
	final static byte UDP_FORCE_RESET		= 7;
	final static byte UDP_UPDATE_ALL 		= 8;
	final static byte UDP_DONE				= 9;
	final static byte COMM_TCP				= 0;
	public final static byte COMM_UDP				= 1;
	final static byte COMM_VERIFY			= 2;
	final static byte COMM_UPDATE			= 3;
	final static byte COMM_MODE				= 4;
	final static byte SWITCH_TCP_OK			= 0;
	final static byte SWITCH_UDP_OK			= 1;
	final static byte SWITCH_DENIED			= 2;
	final static byte SWITCH_VERIFY			= 3;
	final static byte CONNMODE_PORT			= 0;
	final static byte CONNMODE_PACKET		= 1;
	final static byte STAT_CONNECTED 		= 0;
	final static byte STAT_SWITCH_UDP		= 1;
	final static byte STAT_SWITCH_TCP		= 2;
	final static byte STAT_VERIFY_UDP		= 3;
	final static byte MODE_TCP				= 0;
	final static byte MODE_SIMPLE			= 1;
	final static byte MODE_FAT				= 2;
	final static byte MODE_DOUBLE			= 3;

	final static int  UDP_RECENT_INTR		= 300;
	final static byte UDP_UPDATE_WAIT		= 5;
}
