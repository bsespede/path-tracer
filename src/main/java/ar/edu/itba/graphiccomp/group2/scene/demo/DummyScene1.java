package ar.edu.itba.graphiccomp.group2.scene.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.light.AmbientLight;
import ar.edu.itba.graphiccomp.group2.light.DirectionalLight;
import ar.edu.itba.graphiccomp.group2.light.PointLight;
import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.material.Texture2D;
import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Triangles;
import ar.edu.itba.graphiccomp.group2.mesh.BoxMesh;
import ar.edu.itba.graphiccomp.group2.mesh.DiscMesh;
import ar.edu.itba.graphiccomp.group2.mesh.SphereMesh;
import ar.edu.itba.graphiccomp.group2.mesh.TriangleMesh;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.scene.Scene;
import ar.edu.itba.graphiccomp.group2.scene.Spatial;
import ar.edu.itba.graphiccomp.group2.tree.octree.SpatialOctreeBuilder;
import ar.edu.itba.graphiccomp.group2.util.IOUtil;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class DummyScene1 implements Supplier<Scene> {

	@Override
	public Scene get() {
		Scene scene = new Scene();
		Box bounds = new Box(new Vector3d(0, 0, 0), new Vector3d(20, 20, 20));
		scene.setTree(new SpatialOctreeBuilder(bounds).add(disc1(), sphere1(), sphere2(), box1()).get());
		addLights(scene);
		return scene;
	}

	private Spatial sphere1() {
		Geometry geometry = new Geometry("Sphere 1");
		Vector3d localTranslation = new Vector3d(0, 0, -10);
		geometry.setLocalTranslation(localTranslation);
		geometry.setMesh(new SphereMesh(1));
		Texture2D texture = Texture2D.fromIS(IOUtil.resource("texture/test1.png"));
		geometry.setMaterial(new Material("sphere 1 mat").setTexture(texture)
			.setGlass(Vector3ds.constant(0.95), Vector3ds.constant(0.8), 1.07)
		);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial sphere2() {
		Geometry geometry = new Geometry("Sphere 2");
		Vector3d localTranslation = new Vector3d(-3, 1, -10);
		geometry.setLocalTranslation(localTranslation);
		geometry.setMesh(new SphereMesh(2));
		Texture2D texture = Texture2D.fromIS(IOUtil.resource("texture/test2.png"));
		geometry.setMaterial(
			new Material("sphre 2 material").setTexture(texture).setMatte(Vector3ds.constant(0.5))
		);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial disc1() {
		Geometry geometry = new Geometry("Disc 1");
		geometry.setLocalTranslation(new Vector3d(-1, -2, -12));
		geometry.setMesh(new DiscMesh(new Vector3d(0, 1, 0), 10));
		geometry.setMaterial(
			new Material().setTexture(IOUtil.resource("texture/green.png"))
				.setMirror(Vector3ds.constant(0.8))
		);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial box1() {
		Geometry geometry = new Geometry("Box 1");
		Vector3d localTranslation = new Vector3d(4, 0, -15);
		Vector3d halfExtents = new Vector3d(1, 2, 1);
		geometry.setLocalTranslation(localTranslation);
		geometry.setMesh(new BoxMesh(halfExtents));
		Texture2D texture = Texture2D.fromIS(IOUtil.resource("texture/test4.png"));
		geometry.setMaterial(
			new Material().setTexture(texture).setMatte(Vector3ds.constant(0.3))
		);
		geometry.buildBoundingVolume();
		return geometry;
	}

	private Spatial pyramid() {
		Geometry geometry = new Geometry("Triangle 1");
		Vector3d localTranslation = new Vector3d(3f, -1, -7);
		geometry.setLocalTranslation(localTranslation);
		List<Vector3d> vs = new ArrayList<>();
		vs.add(new Vector3d(0, 2, 0));
		vs.add(new Vector3d(1, 0, -1));
		vs.add(new Vector3d(1, 0, 1));
		vs.add(new Vector3d(-1, 0, 1));
		vs.add(new Vector3d(-1, 0, -1));
		List<Integer> indexes = new ArrayList<>();
		indexes.add(0);
		indexes.add(1);
		indexes.add(2);

		indexes.add(0);
		indexes.add(2);
		indexes.add(3);

		indexes.add(0);
		indexes.add(3);
		indexes.add(4);

		indexes.add(0);
		indexes.add(4);
		indexes.add(1);

		List<Vector2d> uvs = new ArrayList<>();
		uvs.add(new Vector2d(0.5, 0.5));
		uvs.add(new Vector2d(1, 1));
		uvs.add(new Vector2d(1, 0));

		uvs.add(new Vector2d(0.5, 0.5));
		uvs.add(new Vector2d(1, 0));
		uvs.add(new Vector2d(0, 0));

		uvs.add(new Vector2d(0.5, 0.5));
		uvs.add(new Vector2d(0, 0));
		uvs.add(new Vector2d(1, 0));

		uvs.add(new Vector2d(0.5, 0.5));
		uvs.add(new Vector2d(1, 0));
		uvs.add(new Vector2d(1, 1));
		geometry.setMesh(
			new TriangleMesh(indexes, vs, new ArrayList<Vector3d>(), uvs, Triangles.boundingBox(vs))
		);
		Texture2D texture = Texture2D.fromIS(IOUtil.resource("texture/test5.png"));
		geometry.setMaterial(new Material().setTexture(texture).setMatte(new Vector3d(1, 1, 1)));
		geometry.buildBoundingVolume();
		return geometry;
	}

	private void addLights(Scene scene) {
		scene.addLight(new AmbientLight(new Vector3d(0.3, 0.3, 0.3)));
		scene.addLight(new DirectionalLight(new Vector3d(-1, -1, 0)));
		scene.addLight(new PointLight(new Vector3d(0, 2, -10)));
	}
}
