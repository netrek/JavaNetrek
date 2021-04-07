package jtrek.util;

import java.awt.image.*;
import jtrek.Copyright;

/**
 * Converts a color image to gray scale.
 */
public class ColorToGrayImageFilter extends RGBImageFilter {    

	/** constructor */
	public ColorToGrayImageFilter() {
        canFilterIndexColorModel = true;
    }

	/** filterRGB */
    public int filterRGB(int x, int y, int rgb) {
		if(rgb == 0) {
			return 0;
		}
		int r = (rgb >>> 16) & 0xFF;
		int g = (rgb >>> 8) & 0xFF;
		int b = rgb & 0xFF;
		int max = (r >= g && r >= b) ? r : ((g >= b) ? g : b);
		return 0xFF000000 | max << 16 | max << 8 | max;
    }
}