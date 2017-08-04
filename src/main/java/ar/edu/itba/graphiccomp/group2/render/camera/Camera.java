package ar.edu.itba.graphiccomp.group2.render.camera;

import java.util.Objects;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.math.Frustum;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.MathUtil;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public abstract class Camera {

	protected final ViewPort _viewPort;
	protected final Frustum _frustum;
	protected final Vector3d _position;
	protected final Vector3d _front;
	protected final Vector3d _up;
	protected final Vector3d _right;

	public Camera(Vector3d position, Frustum frustum, ViewPort viewPort) {
		_viewPort = Objects.requireNonNull(viewPort);
		_frustum = Objects.requireNonNull(frustum);
		_position = new Vector3d(position);
		_front = new Vector3d(0, 0, -1);
		_up = new Vector3d(0, 1, 0);
		_right = new Vector3d();
		recalculateRight();
		Preconditions.checkIsTrue(front().length() == 1);
	}
	
	public Camera(Camera camera) {
		_viewPort = Objects.requireNonNull(camera.viewPort());
		_frustum = Objects.requireNonNull(camera.frustum());
		_position = camera.position();
		_front = camera.front();
		_up = camera.up();
		_right = camera.right();
	}

	public void recalculateRight() {
		_right.cross(front(), up());
		_right.normalize();
	}

	public void setFront(Vector3d lookDirection, Vector3d upDirection) {
		// upDirection.cross(new Vector3d(1, 0, 0), lookDirection);
		// System.out.println(upDirection);
		Preconditions.checkIsTrue(MathUtil.equals(lookDirection.angle(upDirection), Math.PI / 2, 0.1), "Angles must form 90 degrees!");
		_front.set(lookDirection);
		_up.set(upDirection);
		_front.normalize();
		_up.normalize();
		recalculateRight();
	}

	public ViewPort viewPort() {
		return _viewPort;
	}

	public Frustum frustum() {
		return _frustum;
	}

	public Vector3d position() {
		return _position;
	}

	public Vector3d front() {
		return _front;
	}

	public Vector3d up() {
		return _up;
	}

	public Vector3d right() {
		return _right;
	}

	public boolean inViewPort(double row, double column) {
		return 0 <= column && column <= 1 && 0 <= row && row <= 1;
	}

	public abstract void pointRay(RayTracer rayTracer, Line ray, double row, double column);
	
}
