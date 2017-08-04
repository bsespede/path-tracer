package ar.edu.itba.graphiccomp.group2.scene;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.light.Light;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.meshsample.MeshSampler;
import ar.edu.itba.graphiccomp.group2.tree.base.TreeStructure;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class Scene {

	public static final double TRANSLATION_EPS = 0.0001;

	private List<Light> _lights = new LinkedList<>();
	private List<MeshSampler> _geometryLightsSamples = new LinkedList<>();
	private TreeStructure<Spatial> _tree;
	private Optional<Geometry> _background = Optional.empty();

	public Scene setTree(TreeStructure<Spatial> tree) {
		_tree = tree;
		return this;
	}

	public TreeStructure<Spatial> tree() {
		return _tree;
	}

	public Scene addLight(Light light) {
		_lights.add(light);
		return this;
	}

	public List<Light> lights() {
		return _lights;
	}

	public boolean trace(Line line, CollisionContext collision) {
		_tree.test(line, collision);
		return collision.intersects();
	}

	public boolean isCulled(Scene scene, Vector3d start, Vector3d end, Geometry ignoreHit) {
		Vector3d direction = new Vector3d(end);
		direction.sub(start);
		return isCulled(scene, start, direction, end, ignoreHit);
	}

	public boolean isCulled(Scene scene, Vector3d start, Vector3d direction, Vector3d end) {
		return isCulled(scene, start, direction, end, null);
	}

	public boolean isCulled(Scene scene, Vector3d start, Vector3d direction, Vector3d end, Geometry ignoreHit) {
		Line line = new Line(start, direction).translateP0(TRANSLATION_EPS);
		CollisionContext collision = new CollisionContext();
		if (scene.trace(line, collision)) {
			if (ignoreHit != null && collision.geometry().equals(ignoreHit)) {
				return false;
			}
			if (end != null) {
				double lightDistSq = Vector3ds.distanceSq(start, end);
				return collision.collisionDistanceSq() < lightDistSq;
			}
			return true;
		}
		return false;
	}

	public Optional<Geometry> background() {
		return _background;
	}

	public void setBackground(Geometry background) {
		_background = Optional.of(background);
	}

	public Vector3d background(Line line, CollisionContext collision) {
		Vector3d result = new Vector3d();
		if (background().isPresent()) {
			_background.get().trace(line, collision);
			result.set(_background.get().uvmapping(collision.localPosition(), collision.index()));
		}
		return result;
	}

	public void generateGeometryLightsSamples() {
		_geometryLightsSamples.clear();
		tree().traverse(elem -> {
			if (elem instanceof Geometry) {
				Geometry geom = (Geometry) elem;
				if (geom.material().isEmissive()) {
					_geometryLightsSamples.add(geom.mesh().sampler(geom));
				}
			}
		});
	}

	public List<MeshSampler> geometryLightsSamples() {
		return _geometryLightsSamples;
	}

}
