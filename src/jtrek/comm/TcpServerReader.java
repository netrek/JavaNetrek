package jtrek.comm;

import java.io.*;
import jtrek.Copyright;
import jtrek.data.*;
import jtrek.visual.*;

public class TcpServerReader extends ServerReader {

	InputStream in;

	public TcpServerReader(Communications comm, Universe data, NetrekFrame view, InputStream in) {
		super(comm, data, view);
		this.in = in;
		buffer = new byte[1536];
	}
	
	/** doRead */
	void doRead() throws IOException {
		int bytes = in.read(buffer, count, buffer.length - count);
		if(bytes == -1) {
			throw new EOFException("Got read() of -1. Server dead");
		}
		count += bytes;
	}
}

