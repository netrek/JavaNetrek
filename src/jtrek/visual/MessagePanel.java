package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.message.*;

public class MessagePanel extends OffscreenPanel {
	final static int MAX_MESSAGE_SIZE = 79;
	final static int WARP = 999;

	char[] message = new char[80];
	int message_length = 0;
	
	char[] address_chars = "IO->ALL".toCharArray();
	char address = 0;

	NetrekFrame view;
	Universe data;

	public MessagePanel(String name, NetrekFrame view) {
		super(name);
		this.view = view;
		data = view.data;
	}

	/** setup */
	void setup() {
		super.setup();
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
	}

	/** active */
	public final boolean isActive() {
		return(address != 0);
	}

	/** formatAddressChars */
	void formatAddressChars() {
		address_chars[0] = data.me.team.letter;
		address_chars[1] = data.me.getLetter();
		int who = -1;
		switch(address) {
		case 'A' :
			"ALL".getChars(0, 3, address_chars, 4);
			break;
		case 'F':
			"FED".getChars(0, 3, address_chars, 4);
			break;
   		case 'R':
			"ROM".getChars(0, 3, address_chars, 4);
			break;
   		case 'K':
			"KLI".getChars(0, 3, address_chars, 4);
			break;
   		case 'O':
			"ORI".getChars(0, 3, address_chars, 4);
			break;
   		case 'G':
			"GOD".getChars(0, 3, address_chars, 4);
			break;
		// FIX HERE
		case 'T' : 
		case 't' :
			data.me.team.abbrev.getChars(0, 3, address_chars, 4);
			address = data.me.team.letter;
			break;
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			who = address - '0';
			break;
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			who = address - 'a' + 10;
			break;
		default:
			view.warning.setWarning("Not legal recipient");
			address = 0;
			return;
		}
		if(who != -1) {
			if(who < 0 || who >= data.players.length) {
				view.warning.setWarning("Slot is not alive.");
				address = 0;
				return;
			}
			if (data.players[who].status == Player.FREE) {
				view.warning.setWarning("Slot is not alive.");
				address = 0;
				return;
			}
			address_chars[4] = data.players[who].team.letter;
			address_chars[5] = address;
			address_chars[6] = ' ';
		}
	}

	/** postMessage */
	void postMessage() {		
		view.smessage.sendMessage(new String(message, 0, message_length), address);
		message_length = 0;
	}

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(fixed_font);
		FontMetrics metrics = og.getFontMetrics();
		clearOffscreen(og);
		if(address > 0) {
			int x = 4;
			int baseline = metrics.getHeight();
			if(address != WARP) {
				og.drawChars(address_chars, 0, 7, x, baseline);
				x += metrics.charsWidth(address_chars, 0, 7) + 5;
			}
			else {
				og.drawChars(address_chars, 0, 4, x, baseline);
				x += metrics.charsWidth(address_chars, 0, 4) + 5;
			}
			og.drawChars(message, 0, message_length, x, baseline);
			x += metrics.charsWidth(message, 0, message_length) + 1;
			og.drawRect(x, 4, 1, baseline - 2);
		}
		g.drawImage(offscreen, BORDER, BORDER, null);
	}

	/** activeWarp */
	public void activeWarp() {
		address_chars[0] = data.me.team.letter;
		address_chars[1] = data.me.getLetter();
		address = WARP;
		repaint();
	}

	/** handleKey */
	public boolean handleKey(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_CANCEL) {
			address = 0;
			message_length = 0;
		}
		else if(address == 0 || address == WARP) {
			char key = e.getKeyChar();
			if(key == KeyEvent.CHAR_UNDEFINED) {
				return true;
			}
			address = key;
			formatAddressChars();
		}
		else {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_ENTER:
				postMessage();
				address = 0;			
				break;
			case KeyEvent.VK_BACK_SPACE:
			case KeyEvent.VK_DELETE:
				if(--message_length < 0) {
					message_length = 0;
					return true;
				}
				break;
				case KeyEvent.VK_V:
				// paste from the clipboard
				if((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
					Clipboard c = getToolkit().getSystemClipboard();
					Transferable t = c.getContents(this);
					if(t == null) {
						this.getToolkit().beep();
						view.warning.setWarning("Nothing is on the clipboard");
						return true;
					}
					else {
						try {
							String s = (String)t.getTransferData(DataFlavor.stringFlavor);
							if(s.length() + message_length >= MAX_MESSAGE_SIZE) {
								int length = MAX_MESSAGE_SIZE - message_length + 1;
								s.getChars(0, length, message, message_length);
								message_length += length;
								postMessage();
							}
							s.getChars(0, s.length(), message, message_length);
							message_length += s.length();
						}
						catch(UnsupportedFlavorException uf) {
							this.getToolkit().beep();
							view.warning.setWarning("Item on clipboard cannot be converted to a string");
							return true;
						}
						catch(Exception exc) {
							this.getToolkit().beep();
							view.warning.setWarning("Cannot access clipboard");
							return true;
						}
					}
				}
				// fall through to handle 'v' and 'V'
			default:
				char key = e.getKeyChar();
				if(key == KeyEvent.CHAR_UNDEFINED) {
					return true;
				}
				if(++message_length >= MAX_MESSAGE_SIZE) {
					message[message_length - 1] = (char)key;
					postMessage();
				}
				else {
					message[message_length - 1] = (char)key;
				}
			}
		}
		repaint();
		return true;
	}
}