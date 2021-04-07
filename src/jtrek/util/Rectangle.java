package jtrek.util;

import jtrek.Copyright;

/** 
* A retangle optimized for checking if points are contained with the rectangle
*/
public class Rectangle {
	public int x1;
	public int y1;
	public int x2;
	public int y2;

	public Rectangle(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	final public boolean contains(int x, int y) {
		return (x >= x1 && x < x2 && y >= y1 && y < y2);
	}	
}
