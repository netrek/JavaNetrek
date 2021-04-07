package jtrek.comm;

public class MetaServerEntry {
	public final static int OPEN = 0;
	public final static int WAIT = 1;
	public final static int NOBODY = 2;
	public final static int TIME_OUT = 3;
	public final static int NO_CONNECT = 4;
	public final static int NULL = 5; 
	public final static int CANNOT_CONNECT = 6; 
	public final static int DEFAULT = 7;
	
	public final static String[] STATUS_STRINGS = {
		"OPEN:", 
		"Wait queue:", 
		"Nobody", 
		"Timed out", 
		"No connection",
		"Active", 
		"CANNOT CONNECT", 
		"DEFAULT SERVER"
	};
	
	public String address;
	public int port;
	public int time;
	public int players;
	public int status;
	public boolean rsa_flag;
	public char server_type;
}
