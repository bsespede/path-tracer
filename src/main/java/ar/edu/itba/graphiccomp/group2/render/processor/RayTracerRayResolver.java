package ar.edu.itba.graphiccomp.group2.render.processor;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.material.Material.MaterialType;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.render.camera.Camera;
import ar.edu.itba.graphiccomp.group2.render.sampler.Sampler;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class RayTracerRayResolver extends RayResolver {

	@Override
	public void solve(RayTracer rayTracer, Line ray, int row, int column, CollisionContext result) {
		Camera camera = rayTracer.camera();
		Scene scene = rayTracer.scene();
		final ViewPort viewPort = camera.viewPort();
		final List<CollisionContext> collisions = new LinkedList<>();
		for (int i = 0; i < rayTracer.sampler().numSamples(); i++) {
			Vector2d squareSample = rayTracer.sampler().sampleUnitSquare();
			double sampledRow = (row + squareSample.x * 2 - 1) / (viewPort.height() + 1);
			double sampledCol = (column + squareSample.y * 2 - 1) / (viewPort.width() + 1);
			if (0 <= sampledRow && sampledRow <= 1 && 0 <= sampledCol && sampledCol <= 1) {
				CollisionContext collision = new CollisionContext();
				camera.pointRay(rayTracer, ray, sampledRow, sampledCol);
				scene.trace(ray, collision);
				if (rayTracer.renderer().renderLights()) {
					solveReflectance(rayTracer, ray, collision, 0, Material.AIR_REFRACTION);
				}
				collisions.add(collision);
			}
		}
		avg(collisions, result);
	}

	private CollisionContext avg(List<CollisionContext> collisions, CollisionContext avg) {
		int hitsCounter = 0;
		for (CollisionContext collision : collisions) {
			if (collision.intersects()) {
				hitsCounter++;
				avg.normal().add(collision.normal());
				avg.shade().add(collision.shade());
				avg.worldPosition().add(collision.worldPosition());
				double cdSq = avg.collisionDistanceSq();
				avg.setCollisionDistanceSq(cdSq + collision.collisionDistanceSq());
				avg.setIntersects(true);
			}
		}
		avg.normal().scale(1f / hitsCounter);
		avg.shade().scale(1f / hitsCounter);
		avg.setCollisionDistanceSq(avg.collisionDistanceSq() / hitsCounter);
		avg.worldPosition().scale(1f / hitsCounter);
		return avg;
	}

	private void solveReflectance(RayTracer rayTracer, Line line, CollisionContext collision, int depth, double refractionIndex) {
		if (!collision.intersects()) {
			if (rayTracer.scene().background().isPresent()) {
				Geometry background = rayTracer.scene().background().get();
				background.trace(line, collision);
				collision.shade().set(background.uvmapping(collision.localPosition(), collision.index()));
			}
			return;
		}
		MaterialType materialType = collision.geometry().material().type();
		if (materialType.equals(MaterialType.MATTE)) {
			directLighting(rayTracer.scene(), line, collision);
			return;
		}
		if (materialType.equals(MaterialType.MIRROR) || materialType.equals(MaterialType.METAL)) {
			reflectance(rayTracer, line, collision, depth, refractionIndex);
		} else if (materialType.equals(MaterialType.GLASS)) {
			refraction(rayTracer, line, collision, depth, refractionIndex);
		}
	}

	private void reflectance(RayTracer rayTracer, Line line, CollisionContext collision, int depth, double refractionIndex) {
		if (depth >= rayTracer.reflectionsMaxDepth()) {
			return;
		}
		Scene scene = rayTracer.scene();
		if (collision.geometry().material().type().equals(MaterialType.METAL)) {
			directLighting(scene, line, collision);
		}
		Line reflectedRay = reflectedRay(line, collision, null);
		CollisionContext nextCollision = new CollisionContext();
		if (scene.trace(reflectedRay.translateP0(Scene.TRANSLATION_EPS), nextCollision)) {
			solveReflectance(rayTracer, reflectedRay, nextCollision, depth + 1, refractionIndex);
			Vector3ds.mult(nextCollision.shade(), collision.geometry().material().kr());
			Vector3ds.mult(nextCollision.shade(), collision.geometry().uvmapping(collision.localPosition(), 0));
			Vector3ds.add(collision.shade(), nextCollision.shade());
		} else {
			Vector3d background = scene.background(reflectedRay, nextCollision);
			Vector3ds.mult(background, collision.geometry().material().kr());
			Vector3ds.mult(background, collision.geometry().uvmapping(collision.localPosition(), collision.index()));
			Vector3ds.add(collision.shade(), background);
		}
	}

	private void refraction(RayTracer rayTracer, Line line, CollisionContext collision, int depth, double ni) {
		if (depth >= rayTracer.refractionsMaxDepth()) {
			return;
		}
		final Scene scene = rayTracer.scene();
		// solveShading(scene, line, collision);
		final CollisionContext nextCollision = new CollisionContext();
		final Line ray = new Line();
		final double nt = collision.geometry().material().refractionIndex();
		final double nr = ni / nt;
		double c1 = -line.d().dot(collision.normal());
		boolean tir = nr * Math.sqrt(1 - c1 * c1) > 1;
		if (tir) {
			Vector3d normal = new Vector3d(collision.normal());
			normal.scale(-1);
			ray.p0().set(collision.worldPosition());
			Vector3ds.reflect(line.d(), normal, ray.d());
			ray.p0().set(collision.worldPosition());
			if (scene.trace(ray.translateP0(Scene.TRANSLATION_EPS), nextCollision)) {
				solveReflectance(rayTracer, ray, nextCollision, depth + 1, nt);
				Vector3ds.mult(nextCollision.shade(), collision.geometry().material().kr());
				Vector3ds.add(collision.shade(), nextCollision.shade(), 0.8);
			}
		} else {
			double c2 = Math.sqrt(1 - nr * nr * (1 - c1 * c1));
			Vector3d t = ray.d();
			t.x = nr * line.d().x + (nr * c1 - c2) * collision.normal().x;
			t.y = nr * line.d().y + (nr * c1 - c2) * collision.normal().y;
			t.z = nr * line.d().z + (nr * c1 - c2) * collision.normal().z;
			t.normalize();
			ray.p0().set(collision.worldPosition());
			if (scene.trace(ray.translateP0(Scene.TRANSLATION_EPS), nextCollision)) {
				double newNt = nextCollision.geometry().equals(collision.geometry()) ? nt : Material.AIR_REFRACTION;
				solveReflectance(rayTracer, ray, nextCollision, depth + 1, newNt);
			} else {
				nextCollision.shade().set(scene.background(ray, nextCollision));
			}
			double fresnelR = fresnelR(ni, c1, nt, c2);
			double fresnelT = fresnelT(fresnelR);
			Vector3ds.mult(nextCollision.shade(), collision.geometry().material().kt());
			Vector3ds.add(collision.shade(), nextCollision.shade(), fresnelT);
			nextCollision.restart();
			// Refrection
			ray.p0().set(collision.worldPosition());
			Vector3ds.reflect(line.d(), collision.normal(), ray.d());
			if (scene.trace(ray.translateP0(Scene.TRANSLATION_EPS), nextCollision)) {
				solveReflectance(rayTracer, ray, nextCollision, depth + 1, ni);
			} else {
				nextCollision.shade().set(scene.background(ray, nextCollision));
			}
			Vector3ds.mult(nextCollision.shade(), collision.geometry().material().kr());
			Vector3ds.add(collision.shade(), nextCollision.shade(), fresnelR);
		}
		Vector3ds.mult(collision.shade(), collision.geometry().uvmapping(collision.localPosition(), collision.index()));
	}

	private double fresnelR(double ni, double cosTi, double nt, double cosTt) {
		double rs = ni * cosTi - nt * cosTt / (ni * cosTi + nt * cosTt);
		double rp = ni * cosTt - nt * cosTi / (ni * cosTt + nt * cosTi);
		return (rs * rs + rp * rp) / 2;
	}

	private double fresnelT(double R) {
		return 1 - R;
	}

}
