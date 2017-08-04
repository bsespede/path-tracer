package ar.edu.itba.graphiccomp.group2.light;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class PointLight extends Light {

	private final Vector3d _p0 = new Vector3d();
	private boolean _useDecay;

	public PointLight(Vector3d p0) {
		_p0.set(p0);
		_useDecay = false;
	}

	public PointLight withDecay() {
		_useDecay = true;
		return this;
	}

	public Vector3d position() {
		return _p0;
	}

	@Override
	public Vector3d applyShaders(Scene scene, CollisionContext collision, Vector3d eye) {
		Vector3d position = collision.worldPosition();
		Geometry geometry = collision.geometry();
		Vector3d direction = directonTo(position);
		double distanceSq = direction.lengthSquared();
		direction.normalize();
		if (isCulled(scene, position, direction)) {
			return Vector3ds.zero;
		}
		Vector3d result = geometry.material().shader().apply(this, collision, direction, eye);
		return decay(result, position, direction, distanceSq);
	}

	protected boolean isCulled(Scene scene, Vector3d position, Vector3d direction) {
		return scene.isCulled(scene, position, direction, position());
	}

	private Vector3d directonTo(Vector3d position) {
		Vector3d direction = new Vector3d(position());
		direction.sub(position);
		return direction;
	}

	protected Vector3d decay(Vector3d result, Vector3d position, Vector3d direction, double distanceSq) {
		result.scale(_useDecay ? 1 / distanceSq : 1);
		return result;
	}
}
