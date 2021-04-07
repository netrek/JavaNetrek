package jtrek.visual;

import java.awt.*;
import jtrek.Copyright;

public class ColorBar {
	static Color border_color = Color.white;

	int max = 1;
	int yellow = 0;
	int red = 0;
	boolean negative = false;
	boolean alert = true;

	/** Constructor */
	public ColorBar(int max, int yellow, int red, boolean negative) {
		if(max <= 0) {
			max = 1;
		}
		this.max = max;
		this.yellow = yellow;
		this.red = red;
		this.negative = negative;
	}

	/** Constructor */
	public ColorBar(int max, int yellow, int red) {
		this(max, yellow, red, false);
	}

	/** Constructor */
	public ColorBar(int max, boolean negative) {
		this(max, 0, 0, negative);
		alert = false;
	}

	/** Constructor */
	public ColorBar(int max) {
		this(max, false);
	}

	/** setMax */
	public void setMax(int max) {
		if(max <= 0) {
			max = 1;
		}
		this.max = max;
	}

	/** setValues */
	public void setValues(int max, float yellow_percent, float red_percent) {
		if(max <= 0) {
			max = 1;
		}
		this.max = max;
		yellow = (int)(yellow_percent * max);
		red = (int)(red_percent * max);	
	}

	/** setValues */
	public void setValues(int max, int yellow, int red) {
		if(max <= 0) {
			max = 1;
		}
		this.red = red;
		this.yellow = yellow;
		this.max = max;
	}

	/** draw */
	public void draw(Graphics g, int x, int y, int width, int height, int value) {
		if(value <= 0) {
			value = 0;
		}
		else if(value > max) {
			value = max;
		}
		if(!alert) {
			g.setColor(Color.white);
		}
		else if(negative) {
			if(value <= red) {
				g.setColor(Color.red);
			}
			else if(value <= yellow) {
				g.setColor(Color.yellow);
			}
			else {
				g.setColor(Color.green);
			}
		}
		else {
			if(value >= red) {
				g.setColor(Color.red);
			}
			else if(value >= yellow) {
				g.setColor(Color.yellow);
			}
			else {
				g.setColor(Color.green);
			}
		}
		g.fillRect(x, y, (width * value) / max, height);
		g.setColor(Color.white);
		g.drawRect(x, y, width, height);	
	}

}
