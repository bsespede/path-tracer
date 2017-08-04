package ar.edu.itba.graphiccomp.group2.math;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.RayTargeteable;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class Box implements RayTargeteable {

	// Z
	public static final int FRONT = 0;
	public static final int BACK = 1;

	// Y
	public static final int TOP = 2;
	public static final int BOTTOM = 3;

	// X
	public static final int LEFT = 4;
	public static final int RIGHT = 5;

	private static final int FACES_COUNT = 6;

	private final FinitePlane[] _faces;
	private final Vector3d _center = new Vector3d();
	private final Vector3d _halfExtents = new Vector3d();

	private final Vector3d _lb = new Vector3d();
	private final Vector3d _rt = new Vector3d();

	public Box(Vector3d center, Vector3d halfExtents) {
		this(center, halfExtents, true);
	}

	public Box(Vector3d center, Vector3d halfExtents, boolean createFaces) {
		_center.set(center);
		_halfExtents.set(halfExtents);
		_faces = createFaces ? new FinitePlane[6] : null;
		if (createFaces) {
			// YX
			_faces[FRONT] = new FinitePlane(
					new Vector3d(center.x, center.y, center.z + halfExtents.z), 
					new Vector3d(halfExtents.x, 0, 0), new Vector3d(0, halfExtents.y, 0));
			_faces[BACK] = new FinitePlane(
					new Vector3d(center.x, center.y, center.z - halfExtents.z), 
					new Vector3d(halfExtents.x, 0, 0), new Vector3d(0, halfExtents.y, 0));
			// XZ
			_faces[TOP] = new FinitePlane(
					new Vector3d(center.x, center.y + halfExtents.y, center.z), 
					new Vector3d(halfExtents.x, 0, 0), new Vector3d(0, 0, halfExtents.z));
			_faces[BOTTOM] = new FinitePlane(
					new Vector3d(center.x, center.y - halfExtents.y, center.z), 
					new Vector3d(halfExtents.x, 0, 0), new Vector3d(0, 0, halfExtents.z));
			// YZ
			_faces[LEFT] = new FinitePlane(
					new Vector3d(center.x - halfExtents.x, center.y, center.z), 
					new Vector3d(0, halfExtents.y, 0), new Vector3d(0, 0, halfExtents.z));
			_faces[RIGHT] = new FinitePlane(
					new Vector3d(center.x + halfExtents.x, center.y, center.z), 
					new Vector3d(0, halfExtents.y, 0), new Vector3d(0, 0, halfExtents.z));
		}
		_lb.set(center());
		_lb.sub(halfExtents());
		_rt.set(center());
		_rt.add(halfExtents());
		Preconditions.checkIsTrue(Vector3ds.isPositive(halfExtents()));
	}

	public Vector3d center() {
		return _center;
	}

	public Vector3d halfExtents() {
		return _halfExtents;
	}

	public boolean contains(Vector3d v) {
		return
			_lb.x <= v.x && v.x <= _rt.x && 
			_lb.y <= v.y && v.y <= _rt.y && 
			_lb.z <= v.z && v.z <= _rt.z;
	}

	public boolean intersects(Line r) {
		return intersects(r, Float.MAX_VALUE);
	}

	public boolean intersects(Line r, double length) {
		Vector3d min = lbb();
		Vector3d max = rtf();
		double tmin = (min.x - r.p0().x) / r.d().x;
		double tmax = (max.x - r.p0().x) / r.d().x;
		if (tmin > tmax) {
			double tmp = tmin;
			tmin = tmax;
			tmax = tmp;
		}
		double tymin = (min.y - r.p0().y) / r.d().y;
		double tymax = (max.y - r.p0().y) / r.d().y;
		if (tymin > tymax) {
			double tmp = tymin;
			tymin = tymax;
			tymax = tmp;
		}
		if ((tmin > tymax) || (tymin > tmax))
			return false;
		if (tymin > tmin) {
			tmin = tymin;
		}
		if (tymax < tmax) {
			tmax = tymax;
		}
		double tzmin = (min.z - r.p0().z) / r.d().z;
		double tzmax = (max.z - r.p0().z) / r.d().z;
		if (tzmin > tzmax) {
			double tmp = tzmin;
			tzmin = tzmax;
			tzmax = tmp;
		}
		if ((tmin > tzmax) || (tzmin > tmax)) {
			return false;
		}
		if (tzmin > tmin) {
			tmin = tzmin;
		}
		if (tzmax < tmax) {
			tmax = tzmax;
		}
		double rtmin = 0;
		double rtmax = length;
		if ((tmin > rtmax) || (tmax < rtmin)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean intersects(Line r, CollisionContext result) {
		final CollisionContext testResult = new CollisionContext();
		for (int i = 0; i < FACES_COUNT; i++) {
			if (_faces[i].intersects(r, testResult)) {
				result.updateIfCollisionIsCloser(r, testResult, i);
				testResult.restart();
			}
		}
		return result.intersects();
	}

	public double distanceSq(Vector3d p) {
		double x = MathUtil.clamp(p.x, lbb().x, rtf().x);
		double y = MathUtil.clamp(p.y, lbb().y, rtf().y);
		double z = MathUtil.clamp(p.z, lbb().z, rtf().z);
		double dx = p.x - x;
		double dy = p.y - y;
		double dz = p.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public Vector3d lbb() {
		return _lb;
	}

	public Vector3d rtf() {
		return _rt;
	}

	public Box apply(Transform transform) {
		Vector3d rtf = transform.point(new Vector3d(rtf()));
		Vector3d lbb = transform.point(new Vector3d(lbb()));
		double dx = Math.abs(rtf.x - lbb.x);
		double dy = Math.abs(rtf.y - lbb.y);
		double dz = Math.abs(rtf.z - lbb.z);
		Vector3d halfExtents = new Vector3d(dx / 2, dy / 2, dz / 2);
		return new Box(transform.point(new Vector3d(center())), halfExtents);
	}

	public Vector3d size() {
		Vector3d size = new Vector3d(halfExtents());
		size.scale(2);
		return size;
	}

	public Box copy() {
		return new Box(center(), halfExtents());
	}

	public FinitePlane face(int index) {
		return _faces[index];
	}

	@Override
	public String toString() {
		return String.format("Box{\n\tcenter: %s\n\t, halfExtents: %s\n}", center(), halfExtents());
	}
}
