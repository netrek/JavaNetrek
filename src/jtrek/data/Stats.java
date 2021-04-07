package jtrek.data;

import jtrek.Copyright;

public class Stats {
	public final static int MAPMODE			= 1;
	public final static int NAMEMODE		= 2;
	public final static int SHOWSHIELDS		= 4;
	public final static int KEEPPEACE		= 8;
	public final static int SHOWLOCAL		= 16;	// two bits for these two
	public final static int SHOWGLOBAL		= 64;

	public double maxkills;					// max kills ever
	public int kills;  						// how many kills
	public int losses;						// times killed
	public int armsbomb;					// armies bombed
	public int planets;						// planets conquered
	public int tkills;						// Kills in tournament play
	public int tlosses;						// Losses in tournament play
	public int tarmsbomb;					// Tournament armies bombed
	public int tplanets;					// Tournament planets conquered
	public int tticks;						// Tournament ticks

	// SB stats are entirely separate
	public int sbkills;						// Kills as starbase
	public int sblosses;					// Losses as starbase
	public int sbticks;						// Time as starbase
	public double sbmaxkills;				// Max kills as starbase

	public long st_lastlogin;				// Last time this player was played
	public int flags;						// Misc option flags
	public Rank rank = Rank.RANKS[0];		// Ranking of the player

	/** bombingRating */
	public float bombingRating(Status sts) {
		if(tticks <= 0 || sts.armsbomb <= 0) {
			return 0f;
		}
		return (float)(((double)tarmsbomb * (double)sts.timeprod) / ((double)tticks * (double)sts.armsbomb));
	}

	/** planetRating */
	public float planetRating(Status sts) {
		if(tticks <= 0 || sts.planets <= 0) {
			return 0f;
		}
		return (float)(((double)tplanets * (double)sts.timeprod) / ((double)tticks * (double)sts.planets));
	}

	/** offenseRating */
	public float offenseRating(Status sts) {
		if(tticks <= 0 || sts.kills <= 0) {
			return 0f;
		}
		return (float)(((double)tkills * (double)sts.timeprod) / ((double)tticks * (double)sts.kills));
	} 

	/** defenseRating */
	public float defenseRating(Status sts) {
		if(sts.timeprod <= 0) {
			return 0f;
		}
		double top = (double)tticks * (double)sts.losses;
		if(top <= 0) {
			return (float)(top / (double)sts.timeprod);
		}
		if(tlosses <= 0) {
			return 1f;
		}		
		return (float)(top / ((double)tlosses * (double)sts.timeprod));
	}

	/** SBKillsPerHour */
	public float SBKillsPerHour() {
		return (sbticks == 0) ? 0f : (float)sbkills * 36000f / (float)sbticks;
	}

	/** SBDefensePerHour */
	public float SBDefensePerHour() {
		return (sbticks == 0) ? 0f : (float)sblosses * 36000f / (float)sbticks;
	}
}
