package jtrek.visual;

import java.awt.event.*;
import java.awt.*;
import jtrek.Copyright;
import jtrek.comm.*;
import jtrek.config.*;

public class LoginPanel extends OffscreenPanel implements Runnable {
	//-------------------
	//-- class members --
	//-------------------

	final static String GUEST1 = "Guest";
	final static String GUEST2 = "guest";

	final static int GETNAME			= 0;
	final static int GETPASS			= 1;
	final static int MAKEPASS1			= 2;
	final static int MAKEPASS2			= 3;
	final static int GUEST_LOGIN		= 4;
	final static int BADMATCH			= 5;
	final static int BADPASS			= 6;
	final static int AUTOLOGIN_NAME		= 7;
	final static int AUTOLOGIN_PASS		= 8;

	//----------------------
	//-- instance members --
	//----------------------

	NetrekFrame view;

	int line_height;
	int state = GETNAME;	
	char[] name = new char[15];
	char[] pass = new char[15];
	char[] vrfy = new char[15];
	int name_length = 0;
	int pass_length = 0;
	int	vrfy_length = 0;
	int seconds_left = 99;

	boolean waiting_on_server = false;
	boolean guest = false;
	boolean autologin_failure = false;
	
	Thread clock;

	String user_name;

	/**
	* Constructor
	*/
	public LoginPanel(String name, NetrekFrame view) {
		super(name);
		this.view = view;
		if(Defaults.autologin) {
			state = AUTOLOGIN_NAME;
		}
		if(!Defaults.browser_mode) {
			user_name = System.getProperty("user.name");
		}
		else {
			user_name = "JTrekApplet";
		}
	}
	
	/** setup */
	void setup() {
		super.setup();
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));	
		clearOffscreen(offscreen.getGraphics());
		clock = new Thread(this);
		clock.start();
	}

	/** accept, meant for comm to call */
	public void accept(boolean accept) {
		switch(state) {
		case GETNAME:
			if(accept) {
				state = GETPASS;
			}
			else {
				state = MAKEPASS1;
			}
			break;
		case AUTOLOGIN_NAME:
			if(accept) {
				state = AUTOLOGIN_PASS;
				view.comm.sendLoginReq(Defaults.name, Defaults.password, user_name, false);
				return;
			}
			state = GETNAME;
			autologin_failure = true;
			break;
		case GETPASS:
		case MAKEPASS2: 
		case GUEST_LOGIN:
		case AUTOLOGIN_PASS:
			if(accept) {
				clock.stop();
				view.loginComplete();
				return;
			}
			if(state == GUEST_LOGIN) {
				System.out.println("Hmmm... The server won't let me log in as guest!");
				view.quit(0);
				return;
			}
			if(state == AUTOLOGIN_PASS) {
				autologin_failure = true;
			}
			state = BADPASS;
			break;
		default :
			System.err.println("LoginPanel.accept() - Don't know what to do in this state: " + state);
		}
		repaint();
		waiting_on_server = false;		
	}

	public void run() {
		if(state >= AUTOLOGIN_NAME) {
			view.comm.sendLoginReq(Defaults.name, Defaults.password, user_name, true);
			waiting_on_server = true;
		}
		while(true) {
			try {	
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				// ingnore this
			}
			if(!waiting_on_server && --seconds_left < 0) {
				System.out.println("Auto-Quit");
				view.quit(0);
				return;
			}
			repaint();
		}
	}

	/** update */
	public void update(Graphics g) {
		Graphics og = offscreen.getGraphics();
		og.setFont(variable_font);

		og.setColor(Color.black);
		og.fillRect(0, 0, view_size.width, view_size.height);
		og.setColor(Color.white);

		int line_height = og.getFontMetrics().getHeight() + 1;

		if(state >= AUTOLOGIN_NAME) {
			String s = "Attemping automatic login for: " + Defaults.name;
			og.drawString(s, 8, line_height);
			og.drawLine(8, line_height + 1, 8 + og.getFontMetrics().stringWidth(s), line_height + 1);
		}
		else {
			int y = 0;
			if(autologin_failure) {
				String s = "Automatic login failed";
				og.drawString(s, 8, (y += line_height));
				y += 1;
				og.drawLine(8, y, 8 + og.getFontMetrics().stringWidth(s), y);
				y += 5;
			}

			og.drawString("Enter your name.  Use the name \"guest\" to remain anonymous.", 8, (y += line_height));
			og.drawString("Press \"Ctrl-D\" to quit.", 8, (y += line_height));

			y += 5;
			String s = new String(name, 0, name_length);
			og.drawString("Your name (default = " + Defaults.name + "): " + s, 8, (y += line_height));
			if(state == GETPASS || state == BADPASS) {
				og.drawString("Enter password: ", 8, (y += line_height));
			}
			if(state > GETPASS && state != BADPASS && state != GUEST_LOGIN) {
				og.drawString("You need to make a password.", 8, (y += line_height));
				og.drawString("So think of a password you can remember, and enter it.", 8, (y += line_height));
				og.drawString("What is your password? :", 8, (y += line_height));
			}

			if (state == MAKEPASS2 || state == BADMATCH) {
				og.drawString("Enter it again to make sure you typed it right.", 8, (y += line_height));
				og.drawString("Your password? :", 8, (y += line_height));
			}

			if(state == BADMATCH) {
				state = GETNAME;
				name_length = pass_length = vrfy_length = 0;
				og.drawString("Passwords do not match!", 12, (y += line_height + 4));		
			}
			else if(state == BADPASS) {
				state = GETNAME;
				name_length = pass_length = vrfy_length = 0;
				og.drawString("Bad password!", 12, (y += line_height + 4));
			}

			s = "Seconds to go: " + seconds_left;
			og.drawString(s, 160, 400);
		}

		g.drawImage(offscreen, BORDER, BORDER, null);
	}

	/** keyPressed */
	public boolean handleKey(KeyEvent e) {
		if(waiting_on_server) {
			return true;
		}
		switch(e.getKeyCode()) {
		case KeyEvent.VK_ENTER :		
			switch(state) {
			case GETNAME :
				if(name_length < 1) {
					name_length = Defaults.name.length();
					Defaults.name.getChars(0, name_length, name, 0);
				}
				String sname = new String(name, 0, name_length);
				boolean check_name = true;
				if(sname.equals(GUEST1) || sname.equals(GUEST2)) {
					state = GUEST_LOGIN;
					check_name = false;
				}
				view.comm.sendLoginReq(sname, "", user_name, check_name);
				waiting_on_server = true;
				return true;
			case MAKEPASS2 :
				String spass = new String(pass, 0, pass_length);
				String svrfy = new String(vrfy, 0, vrfy_length);
				if(!spass.equals(svrfy)) {
					state = BADMATCH;
					repaint();
					break;
				}
			// fall through
			case GETPASS :
				String password;
				password = new String(pass, 0, pass_length);
				view.comm.sendLoginReq(new String(name, 0, name_length), password, user_name, false);
				waiting_on_server = true;
				return true;
			case MAKEPASS1 :					
				state = MAKEPASS2;
				repaint();
				break;
			}
			break;
		case KeyEvent.VK_ESCAPE :
			switch(state) {
			case GETNAME :
				name_length = 0;
				break;
			case GETPASS :					
			case MAKEPASS1 :
				pass_length = 0;
				break;
			case MAKEPASS2 :
				vrfy_length = 0;
				break;
			}				
			break;
		case KeyEvent.VK_BACK_SPACE : 
		case KeyEvent.VK_DELETE :
			switch(state) {
			case GETNAME :
				if(name_length <= 0) {
					return true;
				}
				--name_length;
				break;
			case GETPASS :					
			case MAKEPASS1 :
				if(pass_length <= 0) {
					return true;
				}
				--pass_length;
				break;
			case MAKEPASS2 :
				if(vrfy_length <= 0) {
					return true;
				}
				--vrfy_length;
				break;
			}
			break;
		case KeyEvent.VK_D :
			if((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
				view.quit(0);
				return true;
			}
			// fall through
		default :
			char key = e.getKeyChar();
			if(key < ' ' || key > '~') {
				return true;
			}
			switch(state) {
			case GETNAME :
				if(name_length >= 15) {
					return true;
				}
				name[name_length++] = (char)key;
				break;
			case GETPASS :					
			case MAKEPASS1 :
				if(pass_length >= 15) {
					return true;
				}
				pass[pass_length++] = (char)key;				
				break;
			case MAKEPASS2 :
				if(vrfy_length >= 15) {
					return true;
				}
				vrfy[vrfy_length++] = (char)key;						
				break;
			}
		}
		repaint();
		return true;
	}
}