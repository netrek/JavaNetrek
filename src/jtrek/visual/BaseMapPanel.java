package jtrek.visual;

import java.awt.*;
import java.awt.event.*;
import jtrek.config.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.util.*;

public abstract class BaseMapPanel extends OffscreenPanel implements MouseMotionListener {
	final static int[] X_TRIANGLE = { -4, 0, 4, -4 };
	final static int[] Y_TRIANGLE = { -2, 2, -2, -2 };

	/** drawTriangle */
	static void drawTriangle(Graphics g, int x, int y, boolean up) {
		int[] xs = new int[4];
		int[] ys = new int[4];
		for(int i = 3; i >= 0; --i) {
			xs[i] = x + X_TRIANGLE[i];
			ys[i] = y + (up ? Y_TRIANGLE[i] : -Y_TRIANGLE[i]);
		}
		if(Defaults.fill_lock_triangle) {
			g.fillPolygon(xs, ys, 4);
		}
		g.drawPolygon(xs, ys, 4);
	}

	NetrekFrame view;
	Universe data;
	InformationPanel information;
	Beeplite beeplite;

	int mx;
	int my;

	/** Constructor */
	BaseMapPanel(String name, NetrekFrame view, Universe data, InformationPanel information, Beeplite beeplite) {
		super(name);
		this.view = view;
		this.data = data;
		this.information = information;
		this.beeplite = beeplite;
	}

	/** setup */
	void setup() {
		super.setup();
		addMouseMotionListener(this);
		setFont(variable_font);
	}

	/** getCourse */
	abstract byte getCourse();

	/** getTarget */
	abstract Object getTarget(int target_type);

	/** mouseDragged */
	public void mouseDragged(MouseEvent e) {	
		mx = e.getX();
		my = e.getY();
		if(Defaults.continuous_mouse && view.feature_list.features[FeatureList.CONTINUOUS_MOUSE].value == Feature.ON) {
			if(view.feature_list.features[FeatureList.CONTINUOUS_STEER].value == Feature.ON) {
				int key = 1;
				if(e.isAltDown()) {
					key = 2;
				}
				else if(e.isMetaDown()) {
					key = 3;
				}
				if(Keymap.keymap[key] != 'k') {
					return;
				}
			}			
			processMouseEvent(new MouseEvent(e.getComponent(), 
											 MouseEvent.MOUSE_PRESSED, 
											 e.getWhen(), 
											 e.getModifiers(), 
											 e.getX(), 
											 e.getY(), 
											 e.getClickCount(), 
											 e.isPopupTrigger()));
		}
	}

	/** mouseMoved */
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}

	/** getInfo */
	public void getInfo(boolean extended) {
		int target_type;
		if(extended) {
			target_type = (Universe.TARG_PLAYER | Universe.TARG_SELF);
		}
		else {
			target_type = (Universe.TARG_PLAYER | Universe.TARG_PLANET);
		}
		Point p = getLocation();
		information.showInformation(getTarget(target_type), mx + p.x, my + p.y, extended);
	}
}