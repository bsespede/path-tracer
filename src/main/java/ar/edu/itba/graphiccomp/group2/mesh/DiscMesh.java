package ar.edu.itba.graphiccomp.group2.mesh;

import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.dot;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.bounding.BoundingVolume;
import ar.edu.itba.graphiccomp.group2.math.Disc;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Plane;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.meshsample.MeshSampler;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;

public class DiscMesh implements Mesh {

	private Disc _disc;

	public DiscMesh(Vector3d n, float r) {
		_disc = new Disc(new Vector3d(), n, r);
	}

	@Override
	public boolean intersection(Line line, CollisionContext result) {
		return _disc.intersects(line, result);
	}

	@Override
	public Vector2d uvmapping(Vector3d position, Vector2d bounds, int collisionIndex, Vector2d result) {
		Plane plane = _disc.plane();
		double dx = plane.p0().x - position.x;
		double dy = plane.p0().y - position.y;
		double dz = plane.p0().z - position.z;
		double ucoord = 0.5 + dot(plane.u(), dx, dy, dz) / _disc.r() / 2;
		double vcoord = 0.5 + dot(plane.v(), dx, dy, dz) / _disc.r() / 2;
		result.set(ucoord, vcoord);
		return result;
	}

	@Override
	public BoundingVolume boundingVolume(Transform tranform) {
		return FinitePlaneMesh.boundingVolume(_disc.plane(), tranform, 2 * _disc.r(), 2 * _disc.r());
	}

	@Override
	public MeshSampler sampler(Geometry geometry) {
		throw new IllegalStateException("Sampling not supported for:" + getClass().getSimpleName());
	}

}
