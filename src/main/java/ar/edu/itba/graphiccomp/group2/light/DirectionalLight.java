package ar.edu.itba.graphiccomp.group2.light;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;

public class DirectionalLight extends Light {

	private final Vector3d _direction = new Vector3d();

	public DirectionalLight(Vector3d direction) {
		_direction.set(direction);
		_direction.scale(-1);
		_direction.normalize();
	}

	@Override
	public Vector3d applyShaders(Scene scene, CollisionContext collision, Vector3d eye) {
		if (isCulled(scene, collision.worldPosition(), _direction)) {
			return new Vector3d();
		}
		Geometry geometry = collision.geometry();
		return geometry.material().shader().apply(this, collision, _direction, eye);
	}

	private boolean isCulled(Scene scene, Vector3d position, Vector3d direction) {
		return scene.isCulled(scene, position, direction, null, null);
	}
}
