package ar.edu.itba.graphiccomp.group2.scene.demo;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.light.AmbientLight;
import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.material.Texture2D;
import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.mesh.BoxMesh;
import ar.edu.itba.graphiccomp.group2.mesh.FinitePlaneMesh;
import ar.edu.itba.graphiccomp.group2.mesh.SphereMesh;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.scene.Spatial;
import ar.edu.itba.graphiccomp.group2.tree.octree.SpatialOctreeBuilder;
import ar.edu.itba.graphiccomp.group2.util.IOUtil;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class DummyScene3 implements Supplier<Scene> {

	@Override
	public Scene get() {
		Scene scene = new Scene();
		scene.setBackground(
			new Geometry()
				.setMaterial(new Material().setTexture(IOUtil.resource("texture/sky.jpg")))
				.setMesh(new SphereMesh(8))
		);
		scene.addLight(new AmbientLight(Vector3ds.constant(0.5)));
		List<Spatial> spatials = new LinkedList<>();
		spatials.add(floor());
		final double r = 0.5;
		Texture2D earthTexture = Texture2D.fromIS(IOUtil.resource("texture/earth.png"));
		Texture2D redTexture = Texture2D.fromIS(IOUtil.resource("texture/red.png"));
		Texture2D blueTexture = Texture2D.fromIS(IOUtil.resource("texture/blue.png"));
		spatials.add(box(new Vector3d(- 2 * r, 1, 0), r, new Material(redTexture).setMatte(new Vector3d(1, 1, 1))));
		spatials.add(sphere(new Vector3d(0, 0.5, 0), r, new Material(earthTexture).setMatte(new Vector3d(1, 1, 1))));
		spatials.add(sphere(new Vector3d(2 * r, 1, 0), r, new Material(blueTexture)
			.setMirror(Vector3ds.constant(0.9)))
		);
		scene.setTree(new SpatialOctreeBuilder(new Box(new Vector3d(), new Vector3d(10, 10, 10))).add(spatials).get());
		return scene;
	}

	public Geometry floor() {
		Geometry geometry = new Geometry();
		float halfLenght = 2;
		geometry.setMaterial(
			new Material(Texture2D.fromIS(IOUtil.resource("texture/test5.png")))
				.setGlass(Vector3ds.constant(1), Vector3ds.constant(0.8), 1.55)
		);
		geometry.setMesh(new FinitePlaneMesh(new Vector3d(halfLenght, 0, 0), new Vector3d(0, 0, halfLenght)));
		geometry.setLocalTranslation(new Vector3d(0, 0, 0));
		geometry.buildBoundingVolume();
		return geometry;
	}

	public Geometry box(Vector3d center, double r, Material material) {
		Geometry geometry = new Geometry();
		geometry.setMesh(new BoxMesh(new Vector3d(r, r, r)));
		geometry.setLocalTranslation(center);
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}

	public Geometry sphere(Vector3d center, double r, Material material) {
		Geometry geometry = new Geometry();
		geometry.setMesh(new SphereMesh(r));
		geometry.setLocalTranslation(center);
		geometry.setMaterial(material);
		geometry.buildBoundingVolume();
		return geometry;
	}
}
