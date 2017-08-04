package ar.edu.itba.graphiccomp.group2.math;

public class Frustum {

	private float _near, _far;
	private float _width, _height;

	public Frustum() {
		this(1, 10000, 2, 1);
	}

	public Frustum(float near, float far, float width, float height) {
		_near = near;
		_far = far;
		setDimentions(width, height);
	}

	public Frustum setFOV(float near, float far, float fov) {
		_near = near;
		_far = far;
		float hip = near / (float) Math.cos(Math.toRadians(fov / 2));
		float width = 2 * (float) Math.sqrt(hip * hip - near * near);
		float height = width * 0.6f;
		setDimentions(width, height);
		return this;
	}

	public Frustum setDimentions(float width, float height) {
		_width = width;
		_height = height;
		return this;
	}

	public float near() {
		return _near;
	}

	public float far() {
		return _far;
	}

	public float width() {
		return _width;
	}

	public float height() {
		return _height;
	}

}
