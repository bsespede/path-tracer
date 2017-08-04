package ar.edu.itba.graphiccomp.group2.scene.demo;

import java.util.function.Supplier;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.light.DirectionalLight;
import ar.edu.itba.graphiccomp.group2.light.PointLight;
import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.material.Texture2D;
import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.mesh.DiscMesh;
import ar.edu.itba.graphiccomp.group2.mesh.FinitePlaneMesh;
import ar.edu.itba.graphiccomp.group2.mesh.SphereMesh;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.scene.Spatial;
import ar.edu.itba.graphiccomp.group2.tree.octree.SpatialOctreeBuilder;
import ar.edu.itba.graphiccomp.group2.util.IOUtil;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class DummyScene2 implements Supplier<Scene> {

	public static final Vector3d cameraPos = new Vector3d(-2, 1, 1.26);
	public static final Vector3d cameraFront = new Vector3d(0.24, 0, -0.99);

	@Override
	public Scene get() {
		Scene scene = new Scene();
		scene.addLight(new PointLight(new Vector3d(2, 2, -5)));
		scene.addLight(new DirectionalLight(new Vector3d(1, -1, 0)));
		Box bounds = new Box(new Vector3d(0, 5, -5), new Vector3d(10, 10, 10));
		scene.setTree(new SpatialOctreeBuilder(bounds).add(
			mirror1(), floor(), glassSphere(), mirror2(), matteSphere(), metalSphere(), glassSphere2()
		).get());
		scene.setBackground(
			new Geometry()
				.setMaterial(new Material().setTexture(IOUtil.file("scene/texture/sky.jpg")))
				.setMesh(new SphereMesh(new Vector3d(0, 0, -5), 7))
		);
		return scene;
	}

	private Spatial floor() {
		Geometry geometry = new Geometry("floor");
		geometry.setMesh(new DiscMesh(new Vector3d(0, 1, 0), 5));
		geometry.setLocalTranslation(new Vector3d(0, 0, -5));
		Material material = new Material("green glass")
			.setTexture(Texture2D.fromIS(IOUtil.file("scene/texture/green.png")))
			.setGlass(Vector3ds.constant(0.8), Vector3ds.constant(0.8), 1.33);
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial mirror1() {
		Geometry geometry = new Geometry("mirror 1");
		geometry.setMesh(new FinitePlaneMesh(new Vector3d(1, 0, -1), 2, new Vector3d(0, 1, 0), 2));
		geometry.setLocalTranslation(new Vector3d(-2, 1, -7));
		Material material = new Material("mirror material")
			.setMirror(Vector3ds.constant(0.9));
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial mirror2() {
		Geometry geometry = new Geometry("mirror 2");
		geometry.setMesh(new FinitePlaneMesh(new Vector3d(1, 0, 1), 2, new Vector3d(0, 1, 0), 2));
		geometry.setLocalTranslation(new Vector3d(2, 1, -7));
		Material material = new Material("mirror material")
			.setTexture(Texture2D.fromIS(IOUtil.file("scene/texture/test2.png")))
			.setMirror(Vector3ds.constant(0.9));
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial glassSphere() {
		Geometry geometry = new Geometry("Glass Sphere");
		geometry.setMesh(new SphereMesh(0.5f));
		geometry.setLocalTranslation(new Vector3d(0, 0.5, -5));
		Material material = new Material("mirror material")
			.setGlass(Vector3ds.constant(1), Vector3ds.constant(1), 1.20);
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial glassSphere2() {
		Geometry geometry = new Geometry("Glass Sphere 2");
		geometry.setMesh(new SphereMesh(0.5f));
		geometry.setLocalTranslation(new Vector3d(2, 0.5, -5));
		Material material = new Material("mirror material 2")
			.setGlass(Vector3ds.constant(0.8), Vector3ds.constant(1), 1.10);
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial matteSphere() {
		Geometry geometry = new Geometry("Mate Sphere");
		geometry.setMesh(new SphereMesh(0.5f));
		geometry.setLocalTranslation(new Vector3d(-1, 0.5, -5));
		Material material = new Material("matte test2")
			.setTexture(Texture2D.fromIS(IOUtil.file("scene/texture/test2.png")))
			.setMatte(Vector3ds.constant(0.5));
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial metalSphere() {
		Geometry geometry = new Geometry("Metal Sphere");
		geometry.setMesh(new SphereMesh(0.5f));
		geometry.setLocalTranslation(new Vector3d(-0.5, 1.25, -5));
		Material material = new Material("metal test3")
			.setTexture(Texture2D.fromIS(IOUtil.file("scene/texture/metal-2.jpg")))
			.setMetal(Vector3ds.constant(1), 20)
		;
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}
}
