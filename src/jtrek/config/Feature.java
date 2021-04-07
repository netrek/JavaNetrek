package jtrek.config;

import jtrek.Copyright;

public class Feature {
	public final static char SERVER_TYPE = 'S';
	public final static char CLIENT_TYPE = 'C';

	public final static boolean ALREADY_SENT = false;
	public final static boolean SEND_FEATURE = true;

	public final static int UNKNOWN = -1;
	public final static int OFF = 0;
	public final static int ON = 1;

	public String name;
	public char type;
	public int desired_value;
	public int arg1;
	public int arg2;
	public boolean send_with_rsa;
	public int value;

	/** Constructor */
	public Feature(String name, char type, int desired_value, boolean send_with_rsa) {
		this.name = name;
		this.type = type;
		this.desired_value = desired_value;
		this.send_with_rsa = send_with_rsa;
		this.value = OFF;
		this.arg1 = 0;
		this.arg2 = 0;
	}

}
