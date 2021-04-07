package jtrek.util;

import jtrek.Copyright;

public class BufferUtils {
	final static char[] SPACES = "                                                                                ".toCharArray();
	
	/** fillWithSpaces */
	public static void fillWithSpaces(char[] buffer, int offset, int length) {
		int b = 0;
		while(length > b + SPACES.length) {
			System.arraycopy(SPACES, 0, buffer, offset + b, SPACES.length);
			b += SPACES.length;
		}
		System.arraycopy(SPACES, 0, buffer, offset + b, buffer.length - b);
	}

	/** fillWithSpaces */
	public final static void fillWithSpaces(char[] buffer) {
		fillWithSpaces(buffer, 0, buffer.length);
	}

	/** formatInteger */
	public static void formatInteger(int number, char[] buffer, int offset, int length) {
		for(int p = length; --p >= 0;) {
			buffer[offset + p] = (char)(number % 10 + '0');
			number /= 10;
		}
		// blank out leading zeros
		int last = offset + length - 2;
		for(int b = offset; b <= last; ++b) {
			if(buffer[b] != '0') {
				break;
			}
			buffer[b] = ' ';
		}
	}

	/** formatFloat */
	public static void formatFloat(float number, char[] buffer, int offset, int integer, int decimal) {
		formatInteger((int)number, buffer, offset, integer);
		buffer[(offset += integer)] = '.';
		++offset;
		for(int p = 0; p < decimal; ++p) {
			number *= 10;
			buffer[offset + p] = (char)((int)number % 10 + '0');
		}
	}

	/** formatFloat */
	public static String formatFloat(float number, int decimal) {
		String s = Integer.toString((int)number) + '.';
		for(int p = 0; p < decimal; ++p) {
			number *= 10;
			s = s.concat(Integer.toString((int)number % 10));
		}
		return s;
	}

	/** integerTo5String */
	public static String integerTo5String(int number) {
		char[] buffer = new char[5];
		formatInteger(number, buffer, 0, 5);
		return new String(buffer);
	}

	/** floatTo22String */
	public static String floatTo22String(float number) {
		char[] buffer = new char[5];
		formatFloat(number, buffer, 0, 2, 2);
		return new String(buffer);
	}

	/** floatTo32String */
	public static String floatTo32String(float number) {
		char[] buffer = new char[6];
		formatFloat(number, buffer, 0, 3, 2);
		return new String(buffer);
	}

	/** create a string from the buffer */
	public static String extractString(byte[] buffer, int offset, int max_length) {
		char[] chars = new char[max_length];
		int pos = 0;
		for(; pos <= offset + max_length; ++pos) {
			if(buffer[offset + pos] == 0) {
				break;
			}
			chars[pos] = (char)buffer[offset + pos];
		}
		return new String(chars, 0, pos);
		//return new String(buffer, 0, offset, pos);
	}
	
	/** Extract the bytes from a string */
	public static byte[] getBytes(String s) {
		/*
		byte[] bytes = new byte[s.length()];
		s.getBytes(0, s.length(), bytes, 0);
		return bytes;
		*/
		char[] chars = s.toCharArray();
		byte[] bytes = new byte[chars.length];
		for (int i = 0; i < chars.length; ++i) {
			bytes[i] = (byte)(chars[i] & 0xFF);
		}
		return bytes;
	}
}
