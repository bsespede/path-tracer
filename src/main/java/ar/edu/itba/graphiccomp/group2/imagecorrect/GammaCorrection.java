package ar.edu.itba.graphiccomp.group2.imagecorrect;

import javax.vecmath.Vector3f;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;

public class GammaCorrection implements ImageCorrector {

	@Override
	public ViewPort correct(ViewPort image) {
		if (image.gamma() ==  0) {
			return image;
		}
		ViewPort result = new ViewPort(image);
		float psi = 1 / image.gamma();
		int i = 0;
		for (Vector3f pixel : image.pixels()) {
			Vector3f converted = result.pixel(i++);
			converted.x = (float) Math.pow(pixel.x, psi);
			converted.y = (float) Math.pow(pixel.y, psi);
			converted.z = (float) Math.pow(pixel.z, psi);
		}
		return result;
	}

}
