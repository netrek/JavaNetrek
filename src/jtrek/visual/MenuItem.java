package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.Copyright;

abstract class MenuItem extends XFocusPanel {
	int id;
	ActionListener actionListener;

	MenuItem(int id) {
		super();
		this.id = id;
	}

	/**
	* Adds the specified action listener to receive action events from
	* this button. Action events occur when a user presses or releases
	* the mouse over this button.
	* @param         l the action listener.
	* @see           java.awt.event.ActionListener
	* @see           java.awt.Button#removeActionListener
	* @since         JDK1.1
	*/ 
    public synchronized void addActionListener(ActionListener l) { 
		actionListener = AWTEventMulticaster.add(actionListener, l);
    }

	/**
	* Removes the specified action listener so that it no longer 
	* receives action events from this button. Action events occur  
	* when a user presses or releases the mouse over this button.
	* @param         l     the action listener.
	* @see           java.awt.event.ActionListener
	* @see           java.awt.Button#addActionListener
	* @since         JDK1.1
	*/ 
    public synchronized void removeActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

	protected void createActionEvent(String command) {
		if(actionListener != null) {
			actionListener.actionPerformed(new ActionEvent(this, id, command));
		}
	}

	public int getId() {
		return id;
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void drawClearBox(Graphics g) {
		Dimension d = getSize();
		g.setColor(getBackground());
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(getForeground());
		g.drawRect(0, 0, d.width - 1, d.height - 1);
		g.drawRect(1, 1, d.width - 3, d.height - 3);
	}

	public void drawText(Graphics g, String text) {
		drawClearBox(g);
		g.drawString(text, 6, 14);
	}
}