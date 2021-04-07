package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import jtrek.Copyright;

public class ChoiceMenuItem extends MenuItem {
	Vector items;
	int selected_index = 0;

	ChoiceMenuItem(int id) {
		super(id);
		items = new Vector();
	}

	int getItemCount() {
		return items.size();
	}

	void addItem(String item) {
		items.addElement(item);
		repaint();
	}

	void insertItem(String item, int index) {
		items.insertElementAt(item, index);
		repaint();
	}

	void removeItem(int position) {
		items.removeElementAt(position);
		repaint();
	}

	void removeItem(String item) {
		if(items.removeElement(item)) {
			repaint();
		}
	}

	void removeAllItems() {
		items.removeAllElements();
		repaint();
	}

	int getSelectedIndex() {
		return selected_index;
	}

	String getSelectedItem() {
		if(items.isEmpty()) {
			return null;
		}
		return (String)items.elementAt(selected_index);
	}

	void setSelectedIndex(int index) {
		selected_index = index;
		repaint();
	}

	public void paint(Graphics g) {
		if(selected_index >= items.size()) {
			drawClearBox(g);	
		}
		else {
			drawText(g, getSelectedItem());
		}
	}

	/** 
	* mouseClicked 
	*/
	public void mousePressed(MouseEvent e) {
		if(e.isAltDown() || e.isMetaDown()) {
			if(--selected_index < 0) {
				selected_index = items.size() - 1;
			}
		}
		else {
			if(++selected_index >= items.size()) {
				selected_index = 0;
			}
		}
		createActionEvent((String)items.elementAt(selected_index));
		repaint();
	}
}
