package jtrek.config;

import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.message.*;
import jtrek.visual.*;

public class FeatureList {
	public final static int FEATURE_PACKETS		= 0;
	public final static int NEW_MACRO			= 1;
	public final static int SMART_MACRO			= 2;
	public final static int WHY_DEAD			= 3;
	public final static int SB_HOURS			= 4;
	public final static int CLOAK_MAXWARP		= 5;
	public final static int SELF_8FLAGS			= 6;
	public final static int SELF_8FLAGS2		= 7;
	public final static int GEN_DISTRESS		= 8;
	public final static int MULTILINE_ENABLED	= 9;
	public final static int SHIP_CAP			= 10;
	public final static int BEEPLITE			= 11;
	public final static int CONTINUOUS_MOUSE	= 12;
	public final static int CONTINUOUS_STEER	= 13;

	Communications comm;

	public Feature[] features = { 
		new Feature("FEATURE_PACKETS", Feature.SERVER_TYPE, Feature.ON, Feature.ALREADY_SENT),
		new Feature("NEWMACRO", Feature.CLIENT_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("SMARTMACRO", Feature.CLIENT_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("WHY_DEAD", Feature.SERVER_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("SBHOURS", Feature.SERVER_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("CLOAK_MAXWARP", Feature.SERVER_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("SELF_8FLAGS", Feature.SERVER_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("SELF_8FLAGS2", Feature.SERVER_TYPE, Feature.OFF, Feature.SEND_FEATURE),
		new Feature("RC_DISTRESS", Feature.SERVER_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("MULTIMACROS", Feature.SERVER_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("SHIP_CAP", Feature.SERVER_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("BEEPLITE", Feature.CLIENT_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("CONTINUOUS_MOUSE", Feature.CLIENT_TYPE, Feature.ON, Feature.SEND_FEATURE),
		new Feature("CONTINUOUS_STEER", Feature.CLIENT_TYPE, Feature.ON, Feature.SEND_FEATURE)
	};

	/** Constructor */
	public FeatureList(Communications comm) {
		this.comm = comm;
	}

	/** checkFeature */
	public void checkFeature(char type, int arg1, int arg2, int value, String name) {
		System.out.println(name + ": " + ((value == Feature.ON) ? "ON" : (value == Feature.OFF) ? "OFF" : "UNKNOWN") + '(' + value + ')');
		for(int f = 0; f < features.length; ++f) {
			if(name.equals(features[f].name)) {
				// if server returns unknown, set to off for server mods, desired
				// value for client mods. Otherwise,  set to value from server.
				features[f].value = (value == Feature.UNKNOWN) ? ((features[f].type == Feature.SERVER_TYPE) ? Feature.OFF : features[f].desired_value) : value;
				if(features[f].arg1 != 0) {
					features[f].arg1 = arg1;
				}
				if(features[f].arg2 != 0) {
					features[f].arg2 = arg2;
				}
				if(name.equals("FEATURE_PACKETS")) {
					reportFeatures();
				}
				else if(name.equals("RC_DISTRESS") && features[GEN_DISTRESS].value != 0) {
					Defaults.rcds = Defaults.preferred_rcds;
				}
				else if(name.equals("BEEPLITE")) {
					switch (value) {
					case Feature.UNKNOWN: // Unknown, we can use all of the features!
						features[FeatureList.BEEPLITE].arg1 
							= Beeplite.PLAYERS_MAP | Beeplite.PLAYERS_LOCAL 
							| Beeplite.SELF | Beeplite.PLANETS | Beeplite.SOUNDS 
							| Beeplite.COLOR | Beeplite.TTS;
						break;
					case Feature.ON:
						if (features[FeatureList.BEEPLITE].arg1 == 0) {					
							// Server says we can have beeplite, but no
							// options??? must be configured wrong.
							features[FeatureList.BEEPLITE].arg1  
								= Beeplite.PLAYERS_MAP | Beeplite.PLAYERS_LOCAL
								| Beeplite.SELF | Beeplite.PLANETS | Beeplite.SOUNDS 
								| Beeplite.COLOR | Beeplite.TTS;
						}
						int flags = features[FeatureList.BEEPLITE].arg1;
						String s = "";
						if ((flags & Beeplite.PLAYERS_MAP) == 0) {
							s += " PLAYERS_MAP";
						}
						if ((flags & Beeplite.PLAYERS_LOCAL) == 0) {
							s += " PLAYERS_LOCAL";
						}
						if ((flags & Beeplite.SELF) == 0) {
							s += " SELF";
						}
						if ((flags & Beeplite.PLANETS) == 0) {
							s += " PLANETS";
						}
						if ((flags & Beeplite.SOUNDS) == 0) {
							s += " SOUNDS";
						}
						if ((flags & Beeplite.COLOR) == 0) {
							s += " COLOR";
						}
						if ((flags & Beeplite.TTS) == 0) {
							s += " TTS";
						}
						if (s.length() > 0) {
							System.out.println("  disabled:" + s);
						}
						break;
					case Feature.OFF:
						features[FeatureList.BEEPLITE].arg1 = 0;
						break;
					}
				}
				return;
			}
		}
		System.out.println("Feature " + name + " from server unknown to client!");
	}

	/** checkFeature */
	public void checkFeature(String s) {
		s = s.trim();

		if (s.length() < 11)
			return;

		String message = "JTREK: ";
		
		if (s.equals("NO_NEWMACRO")) {
			features[NEW_MACRO].value = Feature.OFF;
		}
		else if (s.equals("NO_SMARTMACRO")) {
			features[SMART_MACRO].value = Feature.OFF;
		}
		else if (s.equals("WHY_DEAD")) {
			features[WHY_DEAD].value = Feature.ON;
		}
		else if (s.equals("RC_DISTRESS")) {
			features[GEN_DISTRESS].value = Feature.ON;
			Defaults.rcds = Defaults.preferred_rcds;
		}
		else if (s.equals("NO_CONTINUOUS_MOUSE")) {
			features[CONTINUOUS_MOUSE].value = Feature.OFF;
		}
		else if (s.equals("MULTIMACROS")) {
			features[MULTILINE_ENABLED].value = Feature.ON;
		}
		else if (s.equals("SBHOURS")) {
			features[SB_HOURS].value = Feature.ON;
		}
		else if (s.equals("INFO")) {
			// Client specific notes sent by the server
		}
		else {
			message += "UNKNOWN FEATURE: ";
		}
		System.out.println(message + s);
	}

	/** reportFeatures */
	public void reportFeatures() {
		// call this from handleRSAKey, before sending the response.
		if(features[FEATURE_PACKETS].value == Feature.OFF) {
			return;
		}
		for(int i = 0; i < features.length; ++i) {
			Feature f = features[i];
			// only if it was not already sent (FEATURE_PACKETS)
			if(f.send_with_rsa) {
				comm.sendFeature(f.name, f.type, f.desired_value, f.arg1, f.arg2);
			}
		}
	}

	/** showFeatures */
	public void showFeatures() {
		System.out.println("SERVER FEATURES:");
		for(int i = 0; i < features.length; ++i) {
			Feature f = features[i];
			System.out.println(f.name + " = " + f.value);
		}
	}
}
