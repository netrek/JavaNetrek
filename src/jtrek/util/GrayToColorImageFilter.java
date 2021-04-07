package jtrek.util;

import java.awt.image.*;
import jtrek.Copyright;

/**
* Converts any gray colors in a image to color.
*/
public class GrayToColorImageFilter extends RGBImageFilter {
	
	protected int red;
	protected int green;
	protected int blue;

	/** constructor */
	public GrayToColorImageFilter(int color) {
		red = (color >>> 16) & 0xFF;
		green = (color >>> 8) & 0xFF;
		blue = color & 0xFF;
       canFilterIndexColorModel = true;
   }

	/** filterRGB */
   public int filterRGB(int x, int y, int rgb) {
		if (rgb != 0) {
			int r = (rgb >>> 16) & 0xFF;
			int g = (rgb >>> 8) & 0xFF;
			int b = rgb & 0xFF;
			// the color is gray when the red, green and blue elements are all
			// equals.
			if(r == g && r == b) {
				r = (red * r) / 255;
				g = (green * g) / 255;
				b = (blue * b) / 255;
				return 0xFF000000 | r << 16 | g << 8 | b;
			}
		}
		return rgb;
   }
}