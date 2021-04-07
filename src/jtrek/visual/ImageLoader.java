package jtrek.visual;

import java.awt.*;
import java.io.*;

// This class loads images using getResouceAsStream.  The reasons this is needed is 
// because Netscape won't let me load images using just getResource and because loading
// images from a jar in an application using getResource doesn't work in Sun's Java VM
public class ImageLoader {
	public static Image loadImage(String name) {
		try {
            InputStream in = jtrek.visual.ImageLoader.class.getResourceAsStream(name);
			if(in == null) {
				System.err.println("Couldn't load image: " + name);
				return null;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
			byte[] buffer = new byte[1024];
			int length;
			while((length = in.read(buffer)) >= 0) {
				out.write(buffer, 0, length);
			}
			return Toolkit.getDefaultToolkit().createImage(out.toByteArray());
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
