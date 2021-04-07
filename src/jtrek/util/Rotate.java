package jtrek.util;

import java.awt.*;
import jtrek.Copyright;

public class Rotate {
	// PI == 128

	final public static byte unrotateDir(byte d, int r) {
        return (byte)((d - r) & 0xFF);
	}

	final public static int rotateDir(int d, int r) {
		return (d + r) & 0xFF;
	}

	final public static Point rotateCoord(int ox, int oy, int d, int cx, int cy) {
		int x = 0;
		int y = 0;
		switch (d) {
		case 0:
			x = ox;
			y = oy;
			break;
		case 64:
		case -192:
			x = cy - oy + cx;
			y = ox - cx + cy;
			break;
		case 128:
		case -128:
			x = cx - ox + cx;
			y = cy - oy + cy;
			break;
		case 192:
		case -64:
			x = oy - cy + cx;
			y = cx - ox + cy;
			break;
		default:
			// do it the hard way
			if (ox != cx || oy != cy) {
				double dir = Math.atan2((double)(ox - cx), (double)(cy - oy)) 
					- Math.PI / 2d + (double)d * Math.PI / 128d;
				int r = Trigonometry.ihypot(ox - cx, cy - oy);
				x = (int)(r * Math.cos(dir) + cx);
				y = (int)(r * Math.sin(dir) + cy);
			}
		}
		return new Point(x, y);
	}
}
