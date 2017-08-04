package ar.edu.itba.graphiccomp.group2.render.processor;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.image.ViewPort;
import ar.edu.itba.graphiccomp.group2.light.AreaLight;
import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.material.Material.MaterialType;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.meshsample.MeshSampler;
import ar.edu.itba.graphiccomp.group2.render.RayTracer;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class PathTracerRayResolver extends RayResolver {

	private int _maxDepth;
	private int _samplesPerPixel;

	private final ThreadLocal<PathStep[]> _data = new ThreadLocal<>();

	public PathTracerRayResolver() {
		setMaxDepth(3);
		setSamplesPerPixel(100);
	}

	public PathTracerRayResolver setSamplesPerPixel(int samplesPerPixel) {
		_samplesPerPixel = samplesPerPixel;
		return this;
	}

	public PathTracerRayResolver setMaxDepth(int maxDepth) {
		_maxDepth = maxDepth;
		return this;
	}

	private final PathStep[] path() {
		return _data.get();
	}

	@Override
	public void initialize(RayTracer rayTracer) {
		super.initialize(rayTracer);
		PathStep[] path = new PathStep[_maxDepth + 1];
		for (int i = 0; i < _maxDepth + 1; i++) {
			path[i] = new PathStep();
		}
		_data.set(path);
	}

	@Override
	public void solve(RayTracer rayTracer, Line _r, int row, int column, CollisionContext result) {
		final ViewPort viewPort = rayTracer.camera().viewPort();
		int hits = 0;
		for (int i = 0; i < rayTracer.sampler().numSamples(); i++) {
			Vector2d squareSample = rayTracer.sampler().sampleUnitSquare();
			double sampledRow = (row + squareSample.x * 2 - 1) / (viewPort.height() + 1);
			double sampledCol = (column + squareSample.y * 2 - 1) / (viewPort.width() + 1);
			if (rayTracer.camera().inViewPort(sampledRow, sampledCol)) {
				Line ray = ray(rayTracer, sampledRow, sampledCol);
				CollisionContext collision = path()[0].collision.restart();
				if (rayTracer.scene().trace(ray, collision)) {
					Vector3d shade = new Vector3d();
					for (int j = 0; j < _samplesPerPixel; j++) {
						clearPath(1);
						collision.shade().set(Vector3ds.zero);
						solve(rayTracer, 0, Material.AIR_REFRACTION);
						shade.add(collision.shade());
						result.normal().set(collision.normal());
					}
					shade.scale(1f / _samplesPerPixel);
					result.shade().add(shade);
				} else {
					result.shade().add(rayTracer.scene().background(ray, collision));
				}
				hits++;
			}
		}
		if (hits > 0) {
			result.setIntersects(true);
			result.shade().scale(1f / hits);
		}
	}

	private Line ray(RayTracer rayTracer, double row, double column) {
		Line ray = path()[0].line;
		ray.p0().set(rayTracer.camera().position());
		rayTracer.camera().pointRay(rayTracer, ray, row, column);
		return ray;
	}

	private void clearPath(int start) {
		for (int i = start; i < path().length; i++) {
			path()[i].collision.restart();
		}
	}

	private int solve(RayTracer rayTracer, int depth, double refractionIndex) {
		PathStep[] path = path();
		if (depth >= _maxDepth) {
			return depth;
		}
		Line ray = path[depth].line;
		CollisionContext collision = path[depth].collision;
		if (collision.geometry().material().isEmissive()) {
			AreaLight light = collision.geometry().material().light().get();
			if (depth == 0) {
				collision.shade().set(light.diffuse());
			} else {
				CollisionContext prevCollision = path()[depth - 1].collision;
				Vector3d dir = new Vector3d(collision.worldPosition());
				dir.sub(prevCollision.worldPosition());
				dir.normalize();
				collision.shade().set(light.applyShaders(rayTracer.scene(), prevCollision, Vector3ds.scale(ray.d(), -1), dir, collision.worldPosition()));
			}
			return depth;
		}
		Line nextRay = path[depth + 1].line;
		CollisionContext nextCollision = path[depth + 1].collision;
		int maxDepth = depth;
		Material material = collision.geometry().material();
		MaterialType materialType = material.type();
		if (materialType.isMirror()) {
			if (rayTracer.trace(reflectedRay(ray, collision, nextRay).translateP0(Scene.TRANSLATION_EPS), nextCollision)) {
				maxDepth = solve(rayTracer, depth + 1, refractionIndex);
				Vector3ds.mult(nextCollision.shade(), material.kr());
				collision.shade().add(nextCollision.shade());
			} else {
				collision.shade().add(rayTracer.scene().background(nextRay, nextCollision));
			}
		} else if (materialType.isMatte() || materialType.isMetal()) {
			directLighting(rayTracer.scene(), ray, collision);
			randomRay(ray, collision, nextRay).translateP0(Scene.TRANSLATION_EPS);
			if (rayTracer.trace(nextRay, nextCollision)) {
				maxDepth = solve(rayTracer, depth + 1, refractionIndex);
				Vector3ds.mult(nextCollision.shade(), material.kr());
				applyIndirectLight(ray, material, nextRay, collision, nextCollision);
				if (materialType.isMetal()) {
					collision.shade().add(nextCollision.shade());
				}
			} else {
				collision.shade().add(rayTracer.scene().background(nextRay, nextCollision));
			}
		} else if (materialType.isGlass()) {
			Vector3d reflectedShade = new Vector3d();
			Vector3d refractionShade = new Vector3d();
			Vector3d N = new Vector3d(collision.normal());
			Vector3d I = new Vector3d(ray.d());
			double n1 = refractionIndex;
			double n2 = collision.geometry().material().refractionIndex();
			n2 = (n2 == refractionIndex) ? Material.AIR_REFRACTION : n2;
			double n = n1 / n2;
			double cos1 = -N.dot(I);
			if (cos1 < 0) {
				N.scale(-1);
				cos1 = -N.dot(I);
			}
			Preconditions.checkIsTrue(0 <= cos1 && cos1 <= 1);
			Vector3d reflectDir = new Vector3d();
			{
				reflectDir.x = I.x + 2 * cos1 * N.x;
				reflectDir.y = I.y + 2 * cos1 * N.y;
				reflectDir.z = I.z + 2 * cos1 * N.z;
			}
			// Reflection
			{
				Line line = nextRay;
				nextRay.setP0(collision.worldPosition()).setD(reflectDir).translateP0(Scene.TRANSLATION_EPS);
				if (rayTracer.trace(line, nextCollision)) {
					maxDepth = solve(rayTracer, depth + 1, refractionIndex);
					reflectedShade.set(nextCollision.shade());
				} else {
					reflectedShade.add(rayTracer.scene().background(line, nextCollision));
				}
			}
			double sin1 = Math.sqrt(1 - cos1 * cos1);
			double sin2 = n * sin1;
			if (sin2 < 1) {
				double cos2 = Math.sqrt(1 - sin2 * sin2);
				Vector3d refractDir = new Vector3d();
				{
					refractDir.x = n * I.x + (n * cos1 - cos2) * N.x;
					refractDir.y = n * I.y + (n * cos1 - cos2) * N.y;
					refractDir.z = n * I.z + (n * cos1 - cos2) * N.z;
				}
				// Refraction
				{
					clearPath(depth + 1);
					Line line = nextRay;
					nextRay.setP0(collision.worldPosition()).setD(refractDir).translateP0(Scene.TRANSLATION_EPS);
					if (rayTracer.trace(line, nextCollision)) {
						boolean collisionWithSelf = nextCollision.geometry().equals(collision.geometry());
						double nextN = collisionWithSelf ? material.refractionIndex() : Material.AIR_REFRACTION;
						maxDepth = solve(rayTracer, depth + 1, nextN);
						refractionShade.set(nextCollision.shade());
					} else {
						refractionShade.add(rayTracer.scene().background(line, nextCollision));
					}
				}
				double R = fresnelR(refractionIndex, cos1, n2, cos2);
				double T = fresnelT(R);
				reflectedShade.scale(R);
				refractionShade.scale(T);
			}
			collision.shade().add(reflectedShade);
			collision.shade().add(refractionShade);
		}
		return maxDepth;
	}

	private Line randomRay(Line r, CollisionContext collision, Line result) {
		Preconditions.checkIsTrue(collision.intersects());
		Material material = collision.geometry().material();
		material.sampler().sample(material, r, collision.worldPosition(), collision.normal(), result);
		return result;
	}

	@Override
	protected void directLighting(Scene scene, Line line, CollisionContext collision) {
		super.directLighting(scene, line, collision);
		Vector3d eye = line.d();
		eye.scale(-1);
		for (MeshSampler samples : scene.geometryLightsSamples()) {
			Geometry lightGeom = samples.geometry();
			Vector3d sample = lightGeom.localTransform().point(samples.sample());
			Vector3d direction = new Vector3d(sample);
			direction.sub(collision.worldPosition());
			direction.normalize();
			if (!scene.isCulled(scene, collision.worldPosition(), direction, sample, lightGeom)) {
				// double area = samples.areaOf(triangle);
				AreaLight light = lightGeom.material().light().get();
				collision.shade().add(light.applyShaders(scene, collision, eye, direction, sample));
			}
		}
		eye.scale(-1);
	}

	private void applyIndirectLight(Line in, Material material, Line out, CollisionContext collision, CollisionContext nextCollision) {
		// XXX: Indirect lightning is just handled as an extra light
		in.d().scale(-1);
		out.d().scale(-1);
		Vector3d indirectLight = material.shader().apply(nextCollision.shade(), nextCollision.shade(), nextCollision, out.d(), in.d());
		//collision.shade().add(indirectLight);
		out.d().scale(-1);
		in.d().scale(-1);
	}

	private double fresnelR(double ni, double cosTi, double nt, double cosTt) {
		double rs = ni * cosTi - nt * cosTt / (ni * cosTi + nt * cosTt);
		double rp = ni * cosTt - nt * cosTi / (ni * cosTt + nt * cosTi);
		return (rs * rs + rp * rp) / 2;
	}

	private double fresnelT(double R) {
		return 1 - R;
	}

	private static class PathStep {
		final CollisionContext collision = new CollisionContext();
		final Line line = new Line();

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
		}
	}

}
