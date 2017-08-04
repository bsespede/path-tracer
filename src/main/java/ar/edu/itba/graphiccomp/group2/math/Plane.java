package ar.edu.itba.graphiccomp.group2.math;

import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.abs;
import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.dot;
import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.project;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.RayTargeteable;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class Plane implements RayTargeteable {

	public static enum PlaneSide {
		FRONT, BACK, BOTH;

		public PlaneSide union(PlaneSide side) {
			if (this == side) {
				return this;
			}
			return BOTH;
		}
	}

	public static PlaneSide sideOf(Vector3d v, Vector3d p0, Vector3d n) {
		double dx = v.x - p0.x;
		double dy = v.y - p0.y;
		double dz = v.z - p0.z;
		double dot = Vector3ds.dot(n, dx, dy, dz);
		return 
			Math.abs(dot) < MathUtil.EPS ? PlaneSide.BOTH :
			(dot < 0 ? PlaneSide.BACK : PlaneSide.FRONT);
	}

	private final Vector3d _p0 = new Vector3d();
	private final Vector3d _n = new Vector3d();

	private final Vector3d _u = new Vector3d();
	private final Vector3d _v = new Vector3d();

	public Plane(Vector3d p0, Vector3d n) {
		_p0.set(p0);
		_n.set(n);
		_n.normalize();
		setupUV();
	}

	public Plane(Vector3d p0, Vector3d n, Vector3d u, Vector3d v) {
		this(p0, n);
		_u.set(u);
		_u.normalize();
		_v.set(v);
		_v.normalize();
		Preconditions.checkIsTrue(MathUtil.isZero(_u.dot(_v)));
	}

	public void setupUV() {
		Vector3d n = n();
		if (!MathUtil.isZero(n.x) || !MathUtil.isZero(n.y)) {
			_u.set(n.y, -n.x, 0);
		} else {
			_u.set(n.z, -n.z, 0);
		}
		_v.cross(n, _u);
		_u.normalize();
		_v.normalize();
		Preconditions.checkIsTrue(MathUtil.isZero(_u.dot(_v)));
	}

	public Vector3d n() {
		return _n;
	}

	public Vector3d p0() {
		return _p0;
	}

	public Vector3d u() {
		return _u;
	}

	public Vector3d v() {
		return _v;
	}

	public PlaneSide side(Vector3d v) {
		return sideOf(v, p0(), n());
	}

	@Override
	public boolean intersects(Line line, CollisionContext result) {
		double t2 = line.d().dot(_n);
		if (MathUtil.isZero(t2)) {
			return false;
		}
		double t1 = t1(line.p0());
		double t = t1 / t2;
		if (t < 0) {
			return false;
		}
		result.setPositionAt(line, t);
		result.normal().set(n());
		return true;
	}

	public boolean intersects(Line line, Vector3d position) {
		double t2 = line.d().dot(_n);
		if (MathUtil.isZero(t2)) {
			return false;
		}
		double t1 = t1(line.p0());
		double t = t1 / t2;
		if (t < 0) {
			return false;
		}
		line.pointAt(t, position);
		return true;
	}

	private double t1(Vector3d p0) {
		double dx = _p0.x - p0.x;
		double dy = _p0.y - p0.y;
		double dz = _p0.z - p0.z;
		return dot(_n, dx, dy, dz);
	}

	public double lenght(Vector3d direction, double vLen, double uLen) {
		final Vector3d projection = new Vector3d();
		projection.add(abs(project(direction, u(), uLen)));
		projection.add(abs(project(direction, v(), vLen)));
		return projection.length();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
