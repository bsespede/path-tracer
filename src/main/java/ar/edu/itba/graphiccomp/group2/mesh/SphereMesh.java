package ar.edu.itba.graphiccomp.group2.mesh;

import static ar.edu.itba.graphiccomp.group2.util.Vector3ds.lenghtSq;
import static java.lang.Math.sqrt;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.bounding.BoundingVolume;
import ar.edu.itba.graphiccomp.group2.bounding.SphereBB;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Sphere;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.meshsample.MeshSampler;
import ar.edu.itba.graphiccomp.group2.meshsample.SphereMeshSampler;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;

public class SphereMesh implements Mesh {

	private Sphere _sphere;

	public SphereMesh(double r) {
		this(new Vector3d(), r);
	}

	public SphereMesh(Vector3d center, double r) {
		_sphere = new Sphere(center, r);
	}

	@Override
	public boolean intersection(Line line, CollisionContext result) {
		return _sphere.intersects(line, result);
	}

	@Override
	public Vector2d uvmapping(Vector3d position, Vector2d bounds, int collisionIndex, Vector2d result) {
		double dx = position.x - _sphere.center().x;
		double dy = position.y - _sphere.center().y;
		double dz = position.z - _sphere.center().z;
		double lenSq = sqrt(lenghtSq(dx, dy, dz));
		dx /= lenSq;
		dy /= lenSq;
		dz /= lenSq;
		result.x = 0.5 + Math.atan2(dx, dz) / (2 * Math.PI);
		result.y = 0.5 - Math.asin(dy) / Math.PI;
		return result;
	}

	@Override
	public BoundingVolume boundingVolume(Transform transform) {
		Vector3d center = transform.point(new Vector3d(_sphere.center()));
		double r = _sphere.r();
		return new SphereBB(center, r);
	}

	public Sphere sphere() {
		return _sphere;
	}

	@Override
	public MeshSampler sampler(Geometry geometry) {
		return new SphereMeshSampler(geometry);
	}
}
