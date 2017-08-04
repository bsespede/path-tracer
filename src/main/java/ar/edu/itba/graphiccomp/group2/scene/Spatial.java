package ar.edu.itba.graphiccomp.group2.scene;

import java.util.function.Consumer;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.bounding.BoundingVolume;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.mesh.Mesh;

public abstract class Spatial {

	private String _name;
	private BoundingVolume _boundingVolume;
	private Transform localTransform = new Transform();

	public Spatial() {
	}

	public Spatial(String name) {
		setName(name);
	}

	public void setTransform(Transform transform) {
		localTransform = transform;
	}

	public String name() {
		return _name;
	}

	public Spatial setName(String name) {
		_name = name;
		return this;
	}

	public boolean hasBoundingVolume() {
		return boundingVolume() != null;
	}

	public BoundingVolume boundingVolume() {
		return _boundingVolume;
	}

	public Spatial setBoundingVolume(BoundingVolume boundingVolume) {
		_boundingVolume = boundingVolume;
		return this;
	}

	public Spatial setBoundingVolume(Mesh mesh) {
		_boundingVolume = mesh.boundingVolume(localTransform());
		return this;
	}

	public Transform localTransform() {
		return localTransform;
	}

	public Spatial setLocalTranslation(Vector3d translation) {
		localTransform().setTranslation(translation);
		return this;
	}

	public final boolean trace(Line line, CollisionContext collision) {
		if (boundingVolume() != null && !boundingVolume().intersects(line)) {
			return false;
		}
		return trace(line, null, collision);
	}

	protected abstract boolean trace(Line line, Transform transform, CollisionContext collision);

	public final void traverse(Consumer<Spatial> consumer) {
		traverse(this, consumer);
	}

	protected abstract void traverse(Spatial spatial, Consumer<Spatial> consumer);

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
