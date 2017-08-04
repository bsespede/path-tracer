package ar.edu.itba.graphiccomp.group2.light;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;

public class AreaLight extends Light {

	@Override
	public Vector3d applyShaders(Scene scene, CollisionContext collision, Vector3d eye) {
		throw new IllegalStateException("Not supported");
	}

	public Vector3d applyShaders(Scene scene, CollisionContext collision, Vector3d eye, Vector3d dir, Vector3d p) {
		Geometry geometry = collision.geometry();
		Vector3d result = geometry.material().shader().apply(this, collision, dir, eye);
		// double dirDq = Vector3ds.distanceSq(collision.worldPosition(), p);
		// double dInvSq = 1 / dirDq;
		// result.scale(dInvSq);
		return result;
	}
}
