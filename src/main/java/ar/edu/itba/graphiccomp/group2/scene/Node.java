package ar.edu.itba.graphiccomp.group2.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Transform;

public final class Node extends Spatial {

	private final List<Spatial> _children = new ArrayList<>();

	public Node() {
	}

	public Node(String name) {
		setName(name);
	}

	public List<Spatial> children() {
		return _children;
	}

	public Node addChild(Spatial spatial) {
		children().add(spatial);
		return this;
	}

	@Override
	public boolean trace(Line line, Transform transform, CollisionContext collision) {
		throw new IllegalStateException("No implemented (yet)");
	}

	@Override
	protected void traverse(Spatial spatial, Consumer<Spatial> consumer) {
		consumer.accept(this);
		for (Spatial child : _children) {
			traverse(child, consumer);
		}
	}
}
