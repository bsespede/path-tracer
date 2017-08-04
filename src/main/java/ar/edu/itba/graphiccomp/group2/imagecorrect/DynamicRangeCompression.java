package ar.edu.itba.graphiccomp.group2.imagecorrect;

import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;

import javax.vecmath.Vector3f;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;

public final class DynamicRangeCompression implements ImageCorrector {

	@Override
	public ViewPort correct(ViewPort image) {
		ViewPort result = new ViewPort(image);
		final Vector3f max = new Vector3f();
		for (Vector3f pixel : image.pixels()) {
			max.x = Math.max(max.x, pixel.x);
			max.y = Math.max(max.y, pixel.y);
			max.z = Math.max(max.z, pixel.z);
		}
		System.out.println("[INFO] Max RGB: " + max);
		int index = 0;
		for (Vector3f pixel : image.pixels()) {
			convert(pixel, result.pixel(index++), max);
		}
		return result;
	}

	private void convert(Vector3f from, Vector3f result, Vector3f max) {
		result.x = normalize(from.x, max.x);
		result.y = normalize(from.y, max.y);
		result.z = normalize(from.z, max.z);
	}

	private float normalize(float p, float l) {
		l = max(1, l);
		p = max(0, p);
		p = min(1, p);
		final float depth = 256;
		float c = (depth - 1) / (float) log10(1 + l);
		return c * (float) log10(1 + p) / depth;
	}
}
