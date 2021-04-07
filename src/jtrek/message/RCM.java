package jtrek.message;

import java.util.*;
import jtrek.Copyright;
import jtrek.config.*;

public class RCM implements DistressConstants, Cloneable {
	public final static String[] WHY_DEAD = {
			"", "[quit]", "[photon]", "[phaser]", "[planet]", "[explosion]",
			"", "", "[ghostbust]", "[genocide]", "", "[plasma]", "[detted photon]", "[chain explosion]",
			"[TEAM]", "", "[team det]", "[team explosion]"
		};

		public final static RCM[] DEFAULTS = {
			new RCM("none", '0', "Unknown RCM message"),
			new RCM("kill", '1', "GOD->ALL %i (%S) (%T%c%?%a>0%{+%a armies%!%}) was kill %?%d>0%{%k%!NO CREDIT%} for %u (%r%p) %?%w>0%{%W%!%}"),
			new RCM("planet", '2',"GOD->ALL %i (%S) (%T%c%?%a>0%{+%a armies%!%}) killed by %l (%z) %?%w>0%{%W%!%}"),
			new RCM("bomb", '3',"%N->%Z We are being attacked by %i (%T%c) who is %d%% damaged."),
			new RCM("destroy", '4',"%N->%Z %N destroyed by %i (%T%c)"),
			new RCM("take", '5',"%N->%O %N taken by %i (%T%c)"),
			new RCM("ghostbust", '6',"GOD->ALL %i (%S) (%T%c) was kill %k for the GhostBusters"),
		};
	   
		public String name;
		public int key;
		public String macro;

		public RCM(String name, int key, String macro) {
			this.name = name;
			this.key = key;
			this.macro = macro;
		}

		public Object clone() {
			try {
				RCM rcm = (RCM)super.clone();
				rcm.name = new String(name);
				rcm.key = key;
				rcm.macro = new String(macro);
				return rcm;
			}
			catch (CloneNotSupportedException e) { 
		    	// this shouldn't happen, since we are Cloneable
		    	throw new InternalError();
			}
	    }
}
