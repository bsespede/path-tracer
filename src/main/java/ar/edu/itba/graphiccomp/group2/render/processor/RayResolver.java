package ar.edu.itba.graphiccomp.group2.render.processor;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.light.Light;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public abstract class RayResolver {

	public void initialize(RayTracer rayTracer) {
		
	}

	public abstract void solve(RayTracer rayTracer, Line ray, int row, int column, CollisionContext collision);

	protected void directLighting(Scene scene, Line line, CollisionContext collision) {
		Preconditions.checkIsTrue(collision.intersects());
		if (!scene.lights().isEmpty()) {
			Vector3d eye = line.d();
			eye.scale(-1);
			for (Light light : scene.lights()) {
				collision.shade().add(light.applyShaders(scene, collision, eye));
			}
			eye.scale(-1);
		}
	}

	protected Line reflectedRay(Line ray, CollisionContext collision, Line result) {
		if (result == null) {
			result = new Line();
		}
		result.p0().set(collision.worldPosition());
		Vector3ds.reflect(ray.d(), collision.normal(), result.d());
		return result;
	}
}
