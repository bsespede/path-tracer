package ar.edu.itba.graphiccomp.group2.math;

import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.dot;
import static java.lang.Math.abs;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.RayTargeteable;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public final class FinitePlane implements RayTargeteable {

	private final Plane _plane;

	private double _aLen;
	private double _vLen;

	public FinitePlane(Vector3d p0, Vector3d halfExtentA, Vector3d halfExtentB) {
		Preconditions.checkIsTrue(halfExtentA.dot(halfExtentB) == 0);
		Vector3d n = new Vector3d();
		n.cross(halfExtentA, halfExtentB);
		_aLen = halfExtentA.length();
		_vLen = halfExtentB.length();
		_plane = new Plane(p0, n, halfExtentA, halfExtentB);
	}

	public boolean intersects(Line line, CollisionContext result) {
		if (!_plane.intersects(line, result)) {
			return false;
		}
		Vector3d position = result.localPosition();
		double dx = position.x - _plane.p0().x;
		double dy = position.y - _plane.p0().y;
		double dz = position.z - _plane.p0().z;
		if (abs(dot(_plane.u(), dx, dy, dz)) > _aLen) {
			result.setIntersects(false);
			return false;
		}
		if (abs(dot(_plane.v(), dx, dy, dz)) > _vLen) {
			result.setIntersects(false);
			return false;
		}
		return true;
	}

	public boolean intersects(Line line, Vector3d position) {
		if (!_plane.intersects(line, position)) {
			return false;
		}
		double dx = position.x - _plane.p0().x;
		double dy = position.y - _plane.p0().y;
		double dz = position.z - _plane.p0().z;
		if (abs(dot(_plane.u(), dx, dy, dz)) > _aLen) {
			return false;
		}
		if (abs(dot(_plane.v(), dx, dy, dz)) > _vLen) {
			return false;
		}
		return true;
	}

	public Vector3d u() {
		return _plane.u();
	}

	public double uLength() {
		return _aLen;
	}

	public Vector3d v() {
		return _plane.v();
	}

	public double vLength() {
		return _vLen;
	}

	public Vector3d p0() {
		return _plane.p0();
	}

	public Vector3d n() {
		return _plane.n();
	}

	public FinitePlane invertN() {
		_plane.n().scale(-1);
		return this;
	}

	public double lenght(Vector3d direction) {
		return _plane.lenght(direction, vLength(), uLength());
	}

	public Plane infPlane() {
		return _plane;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
