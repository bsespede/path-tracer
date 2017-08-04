package ar.edu.itba.graphiccomp.group2.tree.octree;

import static java.lang.Math.abs;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public final class OctreeNode<T> {

	private static final float MIN_HALF_EXTENT = 0.01f;

	private static final int TOP_NW = 0;
	private static final int TOP_NE = 1;
	private static final int TOP_SE = 2;
	private static final int TOP_SW = 3;

	private static final int BOTTOM_NW = 4;
	private static final int BOTTOM_NE = 5;
	private static final int BOTTOM_SE = 6;
	private static final int BOTTOM_SW = 7;

	public static final int CHILDREN_COUNT = 8;

	public static enum NodeType {
		LEAF, INTERNAL;
	}

	private int _depth;
	private Box _bounds;
	private OctreeNode<T>[] _children;
	private List<T> _objects = new LinkedList<>();
	private NodeType _type;
	private Octree<T> _tree;

	public OctreeNode(int depth, Box bounds, Octree<T> tree) {
		_depth = depth;
		_bounds = Objects.requireNonNull(bounds);
		_tree = Objects.requireNonNull(tree);
		_type = NodeType.LEAF;
	}

	public int depth() {
		return _depth;
	}

	public List<T> objects() {
		return _objects;
	}

	public NodeType type() {
		return _type;
	}

	public Box bounds() {
		return _bounds;
	}

	public boolean add(T object) {
		boolean added = false;
		switch (_type) {
		case LEAF: {
			if (_tree.strategy().test(object, _bounds)) {
				_objects.add(object);
				added = true;
				debugAdd(object);
				if (depth() < _tree.maxDepth() && size() > _tree.splitSize() && abs(_bounds.halfExtents().x) > MIN_HALF_EXTENT) {
					split();
				}
			}
		}
			break;
		case INTERNAL: {
			for (int i = 0; i < CHILDREN_COUNT; i++) {
				added |= _children[i].add(object);
			}
		}
		}
		return added;
	}

	@SuppressWarnings("unchecked")
	public void split() {
		debugSplit();
		_type = NodeType.INTERNAL;
		_children = new OctreeNode[CHILDREN_COUNT];
		for (int i = 0; i < CHILDREN_COUNT; i++) {
			_children[i] = new OctreeNode<T>(depth() + 1, childBoundingBox(i), _tree);
		}
		for (T object : objects()) {
			boolean added = false;
			for (OctreeNode<T> child : _children) {
				added |= child.add(object);
			}
			Preconditions.checkIsTrue(added, "Element not added");
		}
		_objects = null;
	}

	private Box childBoundingBox(int childIndex) {
		Vector3d center = new Vector3d(_bounds.center());
		Vector3d halfExtents = new Vector3d(_bounds.halfExtents());
		halfExtents.scale(0.5);
		switch (childIndex) {
		case TOP_NW:
			center.x += halfExtents.x;
			center.y += halfExtents.y;
			center.z += halfExtents.z;
			break;
		case TOP_NE:
			center.x -= halfExtents.x;
			center.y += halfExtents.y;
			center.z += halfExtents.z;
			break;
		case TOP_SE:
			center.x -= halfExtents.x;
			center.y += halfExtents.y;
			center.z -= halfExtents.z;
			break;
		case TOP_SW:
			center.x += halfExtents.x;
			center.y += halfExtents.y;
			center.z -= halfExtents.z;
			break;
		case BOTTOM_NW:
			center.x += halfExtents.x;
			center.y -= halfExtents.y;
			center.z += halfExtents.z;
			break;
		case BOTTOM_NE:
			center.x -= halfExtents.x;
			center.y -= halfExtents.y;
			center.z += halfExtents.z;
			break;
		case BOTTOM_SE:
			center.x -= halfExtents.x;
			center.y -= halfExtents.y;
			center.z -= halfExtents.z;
			break;
		case BOTTOM_SW:
			center.x += halfExtents.x;
			center.y -= halfExtents.y;
			center.z -= halfExtents.z;
			break;
		default:
			throw new IllegalStateException("Invalid child index: " + childIndex);
		}
		return new Box(center, halfExtents, false);
	}

	public int size() {
		return _objects.size();
	}

	public OctreeNode<T> child(int i) {
		return _children[i];
	}

	public OctreeNode<T>[] children() {
		return _children;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	private void debugSplit() {
		// String nodeChildType = !_directionFromParent.isPresent() ? "(root)" :
		// NAMES[_directionFromParent.get()];
		// System.out.println("Splitting node: " + nodeChildType + " | depth: "
		// + depth());
	}

	private void debugAdd(T object) {
		// String nodeChildType = !_directionFromParent.isPresent() ? "(root)" :
		// NAMES[_directionFromParent.get()];
		// System.out.println("Added " + object.name() + " to " +
		// nodeChildType);
	}

	public boolean isLeaf() {
		return _type.equals(NodeType.LEAF);
	}

}
