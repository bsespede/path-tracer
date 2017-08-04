package ar.edu.itba.graphiccomp.group2.image;

import javax.vecmath.Vector3f;

import ar.edu.itba.graphiccomp.group2.math.Frustum;

public final class ViewPort {

	private Vector3f[] _pixels;
	private int _width, _height;
	private float _gamma;

	public ViewPort(ViewPort other) {
		setDimentions(other.width(), other.height());
		setGamma(other.gamma());
	}

	public ViewPort(Frustum frustum, int pixelsPerUnit) {
		int width = (int) (frustum.width() * pixelsPerUnit);
		int height = (int) (frustum.height() * pixelsPerUnit);
		setDimentions(width, height);
	}

	public void setDimentions(int width, int height) {
		_pixels = new Vector3f[width * height];
		_width = width;
		_height = height;
		for (int i = 0; i < _pixels.length; i++) {
			_pixels[i] = new Vector3f();
		}
	}

	public int width() {
		return _width;
	}

	public int height() {
		return _height;
	}

	public float gamma() {
		return _gamma;
	}

	public ViewPort setGamma(float gamma) {
		_gamma = gamma;
		return this;
	}

	public Vector3f pixel(int column, int row) {
		return _pixels[row * _width + column];
	}

	public Vector3f pixel(int index) {
		return _pixels[index];
	}

	public Vector3f[] pixels() {
		return _pixels;
	}
}
