package ar.edu.itba.graphiccomp.group2.light;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class AmbientLight extends Light {

	private final Vector3d _ambient = new Vector3d(1, 1, 1);

	public AmbientLight(Vector3d ambient) {
		_ambient.set(ambient);
	}

	@Override
	public Vector3d applyShaders(Scene scene, CollisionContext collision, Vector3d eye) {
		Vector3d shading = new Vector3d(_ambient);
		Geometry geometry = collision.geometry();
		Vector3ds.mult(shading, geometry.uvmapping(collision.localPosition(), collision.index()));
		double scaledR = shading.x * geometry.material().ka().x;
		double scaledG = shading.y * geometry.material().ka().y;
		double scaledB = shading.z * geometry.material().ka().z;
		shading.set(scaledR, scaledG, scaledB);
		return shading;
	}

}
