package ar.edu.itba.graphiccomp.group2.tree.kd;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import ar.edu.itba.graphiccomp.group2.math.Box;

public final class KDTreeNode<T> {

	private static final int MAX_DEPTH = 10;

	public static <T> KDTreeNode<T> leaf(KDTree<T> tree, Collection<T> objects, int depth) {
		return new KDTreeNode<T>(tree, objects, depth);
	}

	public static enum KDNodeType {
		LEAF, INTERNAL;

		public boolean isLeaf() {
			return equals(LEAF);
		}
	}

	public static enum KDPlaneNormalDir {
		X(new Vector3d(1, 0, 0)), Y(new Vector3d(0, 1, 0)), Z(new Vector3d(0, 0, 1));

		private final Vector3d _n;

		private KDPlaneNormalDir(Vector3d n) {
			_n = n;
		}

		public Vector3d n() {
			return _n;
		}
	}

	private final KDTree<T> _tree;

	private KDNodeType _type;
	private Box _bounds;
	private final int _depth;

	// Internal
	private Pair<Vector3d, KDPlaneNormalDir> _plane;
	private KDTreeNode<T> _left;
	private KDTreeNode<T> _right;

	// Leaf
	private final Collection<T> _objects;

	private KDTreeNode(KDTree<T> tree, Collection<T> objects, int depth) {
		_tree = requireNonNull(tree);
		_type = KDNodeType.LEAF;
		_objects = requireNonNull(objects);
		_bounds = requireNonNull(tree.strategy().bounds(objects));
		_depth = depth;
	}

	private void setInternal(KDTreeNode<T> left, KDTreeNode<T> right) {
		_type = KDNodeType.INTERNAL;
		_left = requireNonNull(left);
		_right = requireNonNull(right);
		_objects.clear();
	}

	public KDNodeType type() {
		return _type;
	}

	public Collection<T> objects() {
		return _objects;
	}

	public Box bounds() {
		return _bounds;
	}

	public int depth() {
		return _depth;
	}

	public KDTreeNode<T> addAll(Collection<? extends T> objects) {
		if (!objects.isEmpty()) {
			if (type().isLeaf()) {
				if (objects().addAll(objects)) {
					_bounds = tree().strategy().bounds(objects());
					split();
				}
			} else {
				Pair<Collection<T>, Collection<T>> dividedObjs = tree().strategy().split(this);
				left().addAll(dividedObjs.getLeft());
				right().addAll(dividedObjs.getRight());
			}
		}
		return this;
	}

	public KDTree<T> tree() {
		return _tree;
	}

	public Pair<Vector3d, KDPlaneNormalDir> plane() {
		return _plane;
	}

	public KDTreeNode<T> left() {
		return _left;
	}

	public KDTreeNode<T> right() {
		return _right;
	}

	private KDTreeNode<T> split() {
//		System.out.println(String.format("[KD] Elems: %d | depth: %d", objects().size(), depth()));
		if (depth() < MAX_DEPTH && tree().strategy().isFull(this)) {
			_plane = tree().strategy().dividerPlane(objects());
			Pair<Collection<T>, Collection<T>> objects = tree().strategy().split(this);
			setInternal(
				leaf(tree(), objects.getLeft(), depth() + 1), 
				leaf(tree(), objects.getRight(), depth() + 1)
			);
			left().split();
			right().split();
		}
		return this;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
