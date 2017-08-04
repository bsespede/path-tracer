package ar.edu.itba.graphiccomp.group2.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;

public class PNGImage {

	public void build(ViewPort viewPort, File output) {
		try {
			BufferedImage image = new BufferedImage(viewPort.width(), viewPort.height(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = image.createGraphics();
			for (int i = 0; i < viewPort.width(); i++) {
				for (int j = 0; j < viewPort.height(); j++) {
					Vector3f pixel = viewPort.pixel(i, j);
					graphics.setColor(new Color(pixel.x, pixel.y, pixel.z));
					graphics.drawLine(i, j, i, j);
				}
			}
			ImageIO.write(image, "png", output);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
