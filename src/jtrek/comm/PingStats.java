package jtrek.comm;

import jtrek.Copyright;

public class PingStats {
	double s2;
	int sum;
	int n;

	public int iloss_sc = 0;	// inc % loss 0--100, server to client
	public int iloss_cs = 0;	// inc % loss 0--100, client to server
	public int tloss_sc = 0;	// total % loss 0--100, server to client
	public int tloss_cs = 0;	// total % loss 0--100, client to server
	public int lag = 0;			// delay in ms of last ping
	public int av = 0;			// rt time
	public int sd = 0;			// std deviation

	void calculateLag() {
		// probably ghostbusted
		if (lag > 2000) {
			return;
		}
		
		sum += lag;
		s2 += (lag * lag);
		
		if (++n != 1) {
			av = sum / n;
			sd = (int)Math.sqrt((double)((s2 - av * sum) / (n - 1)));
		}
	}

}
