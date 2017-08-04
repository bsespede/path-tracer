package ar.edu.itba.graphiccomp.group2.math;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.RayTargeteable;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class Disc implements RayTargeteable {

	private Plane _plane;
	private float _sqR;
	private float _r;

	public Disc(Vector3d p0, Vector3d n, float r) {
		_plane = new Plane(p0, n);
		_sqR = r * r;
		_r = r;
	}

	@Override
	public boolean intersects(Line line, CollisionContext result) {
		if (_plane.intersects(line, result) && Vector3ds.distanceSq(_plane.p0(), result.localPosition()) > _sqR) {
			result.setIntersects(false);
		}
		return result.intersects();
	}

	public Plane plane() {
		return _plane;
	}

	public float sqR() {
		return _sqR;
	}

	public Vector3d center() {
		return plane().p0();
	}

	public Vector3d n() {
		return plane().n();
	}

	public float r() {
		return _r;
	}
}
