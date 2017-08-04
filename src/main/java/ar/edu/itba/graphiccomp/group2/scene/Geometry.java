package ar.edu.itba.graphiccomp.group2.scene;

import java.util.function.Consumer;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.mesh.Mesh;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class Geometry extends Spatial {

	private Material _material;
	private Mesh _mesh;

	public Geometry() {
	}

	public Geometry(String name) {
		setName(name);
	}

	public Geometry buildBoundingVolume() {
		setBoundingVolume(mesh().boundingVolume(localTransform()));
		return this;
	}

	public Geometry setMaterial(Material material) {
		_material = material;
		return this;
	}

	public Material material() {
		return _material;
	}

	public Geometry setMesh(Mesh mesh) {
		this._mesh = mesh;
		return this;
	}

	public Mesh mesh() {
		return _mesh;
	}

	public Vector3d uvmapping(Vector3d position, int collisionIndex) {
		if (_material.hasTexture()) {
			Vector2d size = material().texture().size();
			return _material.colorAt(mesh().uvmapping(position, size, collisionIndex, new Vector2d()));
		} else {
			return _material.kd();
		}
	}

	@Override
	// XXX: https://www.cl.cam.ac.uk/teaching/1999/AGraphHCI/SMAG/node2.html
	protected boolean trace(Line line, Transform transform, CollisionContext result) {
		Preconditions.checkIsTrue(!result.intersects());
		transform = localTransform();
		Line transformedLine = new Line();
		transform.inverse().point(line.p0(), transformedLine.p0());
		transform.inverse().direction(line.d(), transformedLine.d());
		double scaleFactor = transformedLine.d().length();
		transformedLine.d().normalize();
		if (mesh().intersection(transformedLine, result)) {
			result.setGeometry(this);
			// Fix normal
			if (result.normal().dot(transformedLine.d()) > 0) {
				// XXX: ensure normal is always facing the ray's direction
				result.normal().scale(-1);
			}
			transform.direction(result.normal());
			result.normal().normalize();
			// Setup world collision
			transform.inverse().point(result.localPosition(), result.worldPosition());
			line.pointAt(result.t() / scaleFactor, result.worldPosition());
			result.setCollisionDistanceSq(Vector3ds.distanceSq(line.p0(), result.worldPosition()));
		}
		return result.intersects();
	}

	protected void traverse(Spatial spatial, Consumer<Spatial> consumer) {
		consumer.accept(this);
	}

	@Override
	public String toString() {
		return "Geomtry: " + name();
	}
}
