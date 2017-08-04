package ar.edu.itba.graphiccomp.group2.mesh;

import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.isZero;
import static java.lang.Math.max;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.bounding.AABB;
import ar.edu.itba.graphiccomp.group2.bounding.BoundingVolume;
import ar.edu.itba.graphiccomp.group2.math.FinitePlane;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.MathUtil;
import ar.edu.itba.graphiccomp.group2.math.Plane;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.meshsample.MeshSampler;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public class FinitePlaneMesh implements Mesh {

	public static BoundingVolume boundingVolume(Plane plane, Transform transform, double uLen, double vLen) {
		final Vector3d center = transform.point(new Vector3d(plane.p0()));
		final Vector3d halfExtents = new Vector3d();
		halfExtents.x = max(plane.lenght(new Vector3d(1, 0, 0), uLen, vLen), 0.1);
		halfExtents.y = max(plane.lenght(new Vector3d(0, 1, 0), uLen, vLen), 0.1);
		halfExtents.z = max(plane.lenght(new Vector3d(0, 0, 1), uLen, vLen), 0.1);
		halfExtents.scale(0.5);
		return new AABB(center, halfExtents);
	}

	private FinitePlane _plane;

	public FinitePlaneMesh(Vector3d dirA, double aLen, Vector3d dirB, double bLen) {
		Preconditions.checkIsTrue(MathUtil.equals(dirA.angle(dirB), Math.PI / 2));
		dirA.normalize();
		dirA.scale(aLen / 2);
		dirB.normalize();
		dirB.scale(bLen / 2);
		_plane = new FinitePlane(new Vector3d(), dirA, dirB);
	}

	public FinitePlaneMesh invertN() {
		_plane.invertN();
		return this;
	}

	public FinitePlaneMesh(Vector3d halfExtentA, Vector3d halfExtentB) {
		_plane = new FinitePlane(new Vector3d(), halfExtentA, halfExtentB);
	}

	@Override
	public boolean intersection(Line line, CollisionContext result) {
		return _plane.intersects(line, result);
	}

	@Override
	public Vector2d uvmapping(Vector3d position, Vector2d bounds, int collisionIndex, Vector2d result) {
		Vector3d d;
		if (isZero(_plane.p0())) {
			d = position;
		} else {
			d = new Vector3d(position);
			d.sub(_plane.p0());
		}
		double du = _plane.u().dot(d) / _plane.uLength() / 2;
		double dv = _plane.v().dot(d) / _plane.vLength() / 2;
		result.x = du + 0.5;
		result.y = dv + 0.5;
		return result;
	}

	@Override
	public BoundingVolume boundingVolume(Transform tranform) {
		return boundingVolume(_plane.infPlane(), tranform, 2 * _plane.uLength(), 2 * _plane.vLength());
	}

	@Override
	public MeshSampler sampler(Geometry geometry) {
		throw new IllegalStateException("Sampling not supported for:" + getClass().getSimpleName());
	}

}
