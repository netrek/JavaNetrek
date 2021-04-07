package jtrek.comm;

import jtrek.Copyright;
import jtrek.config.Defaults;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class MetaServerParser {
	public MetaServerEntry[] readFromMetaServer() {
		String server = Defaults.properties.getPropertyAsString("metaserver");
		int port = Defaults.properties.getPropertyAsInt("metaport");													
		try {
			Socket socket = new Socket(server, port);
			DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			MetaServerEntry[] entries = parseInput(in);
			in.close();
			socket.close();
			return entries;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	MetaServerEntry[] parseInput(DataInputStream in) throws Exception {
		int status_level = Defaults.properties.getPropertyAsInt("metaStatusLevel");
		Vector vector = new Vector();
		
		// add the default server		
		MetaServerEntry entry = new MetaServerEntry();
		entry.address = Defaults.server;
		entry.port = Defaults.port;
		entry.status = MetaServerEntry.DEFAULT;
		entry.server_type = ' ';		
		vector.addElement(entry);
		
		String line = null;		
		while ((line = in.readLine()) != null) {
			// make sure this is a line with server info on it
			if (line.length() == 79 && line.substring(0, 3).equals("-h ") && line.substring(40, 43).equals("-p ")) {
				entry = new MetaServerEntry();
				entry.address = line.substring(3, 39).trim();
				// TODO: Handle NumberFormatExceptions
				entry.port = Integer.parseInt(line.substring(43, 48).trim());
				entry.time = Integer.parseInt(line.substring(49, 52).trim());
				entry.status = -1;
				String status = line.substring(54, 71);				
				for (int i = 0; i <status_level; ++i) {
					if (status.indexOf(MetaServerEntry.STATUS_STRINGS[i]) != -1) {
						entry.status = i;
					}
				}
				if (entry.status == -1) {
					continue;
				}
				// parse the number of players if this server has any players
				if (entry.status == MetaServerEntry.OPEN || entry.status == MetaServerEntry.WAIT) {
					// TODO: Handle FormatErrors here too
					int index_of = status.indexOf(':');
					status = status.substring(index_of + 2);
					index_of = status.indexOf(' ');
					entry.players = Integer.parseInt(status.substring(0, index_of));
				}
				
				// read the flags
				entry.rsa_flag = line.charAt(74) == 'R';
				entry.server_type = line.charAt(78);
				
				// don't list paradise servers
				if (entry.server_type != 'P') {
					vector.addElement(entry);
				}
			}
		}
		MetaServerEntry[] entries = new MetaServerEntry[vector.size()];
		vector.copyInto(entries);
		return entries;
	}
}
