package ar.edu.itba.graphiccomp.group2.imagecorrect;

import javax.vecmath.Vector3f;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.math.MathUtil;

public final class CapRGBImage implements ImageCorrector {

	@Override
	public ViewPort correct(ViewPort image) {
		ViewPort result = new ViewPort(image);
		int index = 0;
		for (Vector3f pixel : image.pixels()) {
			Vector3f value = result.pixel(index++);
			value.x = (float) MathUtil.clamp(pixel.x, 0, 1);
			value.y = (float) MathUtil.clamp(pixel.y, 0, 1);
			value.z = (float) MathUtil.clamp(pixel.z, 0, 1);
		}
		return result;
	}
}
