package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import jtrek.Copyright;
import jtrek.config.*;
import jtrek.message.*;

public class MacroPanel extends BasePanel {
	boolean show_distress = false;

	NetrekFrame view;

	MacroPanel(String name, NetrekFrame view) {
		super(name);
		this.view = view;
	}

	/** setup */
	void setup() {
		super.setup();
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				show_distress = !show_distress;
				repaint();
			}
		});
	}

	/**
	 * update
	 */
	public void update(Graphics g) {
		Dimension d = getSize();
		g.clearRect(0, 0, d.width, d.height);
		paintBorder(g); 
		if(show_distress) {
			drawDistress(g);
		}
		else {
			drawMacros(g);
		}
	}

	String[] divideLine(String line) {
		Vector vector = new Vector();
		while(line.length() > 90) {
			vector.addElement(line.substring(0, 90));
			line = line.substring(90);
		}
		vector.addElement(line);
		String[] lines = new String[vector.size()];
		vector.copyInto(lines);
		return lines;
	}

	void drawDistress(Graphics g) {
		int row_height = g.getFontMetrics().getAscent() + 1;
		int y = row_height + 6;

		StringBuffer buffer = new StringBuffer("Packages active: NBT ");
		if(view.feature_list.features[FeatureList.NEW_MACRO].value == Feature.ON) {
			buffer.append("NEWMACRO ");
		}
		if(view.feature_list.features[FeatureList.SMART_MACRO].value == Feature.ON) {
			buffer.append("SMARTMACRO");
		}
		g.drawString(buffer.toString(), 8, y);

		y += row_height;
		g.drawString("Currently showing: RCDs", 8, y);

		y += row_height;
		g.setColor(Color.yellow);
		g.drawString("Key     Distress Name", 8, y);

		for(int i = 1; i < Defaults.rcds.length; ++i) {
			RCD rcd = Defaults.rcds[i];
			buffer.setLength(0);
			if(rcd.key > 128) {
				buffer.append('^');
				buffer.append((char)(rcd.key - 128));
			}
			else {
				buffer.append((char)rcd.key);
				buffer.append(' ');
			}
			buffer.append("       ");
			buffer.append(rcd.name);
			y += row_height;
			g.setColor(Color.yellow);
			g.drawString(buffer.toString(), 8, y);
			
			g.setColor(getForeground());
			String[] lines = divideLine(rcd.macro);
			for(int j = 0; j < lines.length; ++j) {
				y += row_height;
				g.drawString(lines[j], 56, y);
			}
		}
	}

	void drawMacros(Graphics g) {
		int row_height = g.getFontMetrics().getAscent() + 1;
		int y = row_height + 6;

		StringBuffer buffer = new StringBuffer("Packages active: NBT ");
		if(view.feature_list.features[FeatureList.NEW_MACRO].value == Feature.ON) {
			buffer.append("NEWMACRO ");
		}
		if(view.feature_list.features[FeatureList.SMART_MACRO].value == Feature.ON) {
			buffer.append("SMARTMACRO");
		}
		g.drawString(buffer.toString(), 8, y);

		y += row_height;
		g.drawString("Currently showing: Macros", 8, y);

		Enumeration macs = Defaults.macros.elements();
		while(macs.hasMoreElements()) {
			Macro macro = (Macro)macs.nextElement();
			buffer.setLength(0);
			if(macro.key > 128) {
				buffer.append('^');
				buffer.append((char)(macro.key - 128));
			}
			else {
				buffer.append(macro.key);
				buffer.append(' ');
			}
			if(macro.type == Macro.NEWMOUSE) {
				switch(macro.who) {
				case Macro.PLAYER:
					buffer.append(" PL MS ");
					break;
				case Macro.TEAM:
					buffer.append(" TM MS ");
					break;
				default:
					buffer.append(" SELF  ");
					break;
				}
			} 
			else {
				switch(macro.who) {
				case 'T':
					buffer.append(" TEAM  ");
					break;
				case 'A':
					buffer.append(" ALL   ");
					break;
				case 'F':
					buffer.append(" FED   ");
					break;
				case 'R':
					buffer.append(" ROM   ");
					break;
				case 'K':
					buffer.append(" KLI   ");
					break;
				case 'O':
					buffer.append(" ORI   ");
					break;
				case 'M':
					buffer.append(" MOO   ");
					break;
				case '\0':
					buffer.append(" SPEC  ");
					break;
				default:
					buffer.append(" ----  ");
					break;
				}
			}
			buffer.append(macro.macro);
			y += row_height;
			g.drawString(buffer.toString(), 8, y);
		}
	}
}