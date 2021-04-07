package jtrek.config;

import jtrek.Copyright;
import java.util.*;

public class JTrekProperties extends Properties {
	public JTrekProperties() {
		super();
	}

	public JTrekProperties(Properties defaults) {
		super(defaults);
	}

	public Object put(Object key, Object value) {
		if(key.toString().startsWith("mac.")) {
			Object original = get(key);
			if(original != null) {
				value = original.toString() + '\n' + value.toString();			
			}
		}
		return super.put(key, value);
	}
	
	public String getPropertyAsString(String key) {
		String value = getProperty(key);
		if(value != null) {
			return value;
		}		
		System.err.println("Warning: value for the string property \"" + key + "\" not found.");
		System.err.println("Will use a blank as the value.");
		return "";
	}
	
	public boolean getPropertyAsBoolean(String key) {
		String value = getProperty(key);
		if(value == null) {
			System.err.println("Warning: value for the boolean property \"" + key + "\" not found.");
		}
		else if(value.equalsIgnoreCase("true")) {
			return true;
		}
		else if(value.equalsIgnoreCase("false")) {
			return false;
		}
		else {
			System.err.println("Warning: the value for the boolean property \"" + key + "\" is invalid: " + value);
		}
 		System.err.println("Will use \"false\" as the value.");
		return false;
	}	
	
	public int getPropertyAsInt(String key) {
		String value = getProperty(key);
		if(value == null) {
			System.err.println("Warning: value for the int property \"" + key + "\" not found.");
		}
		else {
			try {
				return Integer.parseInt(value);
			}
			catch(NumberFormatException e) {
				System.err.println("Warning: the value for the int property \"" + key + "\" is invalid: " + value);
			}
		}
		System.err.println("Will use \"0\" as the value.");
		return 0;
	}
	
	public int getPropertyAsIntWithMax(String key, int max) {
		int value = getPropertyAsInt(key);
		if(value > max) {
			System.err.println("Warning: value for the int property \"" + key + "\" is above the maximum of " + max + ".");
			System.err.println("Will use " + max + " instead of " + value + ".");
		}
		return value;
	}
}
