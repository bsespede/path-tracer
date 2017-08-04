package ar.edu.itba.graphiccomp.group2.tree.kd;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.tuple.Pair;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.tree.base.TreeStructure;
import ar.edu.itba.graphiccomp.group2.tree.kd.KDTreeNode.KDPlaneNormalDir;

public class KDTree<T> implements TreeStructure<T> {

	public static interface KDTreeStategy<T> {

		Pair<Collection<T>, Collection<T>> split(KDTreeNode<T> node);

		Pair<Vector3d, KDPlaneNormalDir> dividerPlane(Collection<T> objects);

		Box bounds(Collection<T> objects);

		boolean test(T object, Line line, CollisionContext collision);

		boolean isFull(KDTreeNode<T> node);

	}

	private final KDTreeNode<T> _root;
	private KDTreeStategy<T> _strategy;
	private boolean _checkRepetitions = false;

	public KDTree(KDTreeStategy<T> strategy) {
		_strategy = Objects.requireNonNull(strategy);
		_root = KDTreeNode.leaf(this, new LinkedList<>(), 0);
	}

	public KDTree<T> setCheckRepetitions(boolean checkRepetitions) {
		_checkRepetitions = checkRepetitions;
		return this;
	}

	public KDTreeNode<T> root() {
		return _root;
	}

	public KDTreeStategy<T> strategy() {
		return _strategy;
	}

	@Override
	public Box bounds() {
		return root().bounds();
	}

	public KDTree<T> addAll(Collection<? extends T> objects) {
		root().addAll(objects);
		return this;
	}

	@Override
	public void test(Line line, CollisionContext collision) {
		test(root(), line, collision, _checkRepetitions ? new HashSet<T>() : null);
	}

	private boolean test(KDTreeNode<T> node, Line line, CollisionContext collision, Set<T> checked) {
		if (node.bounds().intersects(line)) {
			if (node.type().isLeaf()) {
				final CollisionContext tmp = new CollisionContext();
				for (T elem : node.objects()) {
					if (checked != null && !checked.add(elem)) {
						continue;
					}
					if (strategy().test(elem, line, tmp)) {
						collision.updateIfCollisionIsCloser(line, tmp);
						tmp.restart();
					}
				}
				if (collision.intersects()) {
					return true;
				}
			} else {
				test(node.right(), line, collision, checked);
				test(node.left(), line, collision, checked);
				return collision.intersects();
				// FIXME: Ver porque se  pierden algunos triangulos al renderizar charizard (por ej)
//				PlaneSide side = Plane.sideOf(line.p0(), node.plane().getLeft(), node.plane().getRight().n());
//				if (side == PlaneSide.BOTH) {
//					test(node.right(), line, collision, checked);
//					test(node.left(), line, collision, checked);
//					return collision.intersects();
//				} else {
//					Preconditions.checkIsTrue(side != PlaneSide.BOTH);
//					KDTreeNode<T> first = (side == PlaneSide.FRONT) ? node.right() : node.left();
//					KDTreeNode<T> second = (side == PlaneSide.FRONT) ? node.left() : node.right();
//					return test(first, line, collision, checked) || test(second, line, collision, checked);
//				}
			}
		}
		return false;
	}

	@Override
	public void traverse(Consumer<T> consumer) {
		traverse(root(), consumer, new HashSet<>());
	}

	private void traverse(KDTreeNode<T> node, Consumer<T> consumer, Set<T> visited) {
		if (node.type().isLeaf()) {
			for (T elem : node.objects()) {
				if (visited.add(elem)) {
					consumer.accept(elem);
				}
			}
		} else {
			traverse(node.left(), consumer, visited);
			traverse(node.right(), consumer, visited);
		}
	}

	public int depth() {
		return depth(root());
	}

	private int depth(KDTreeNode<T> node) {
		if (node.type().isLeaf()) {
			return node.depth();
		}
		return Math.max(depth(node.left()), depth(node.right()));
	}

	public int totalSize() {
		return totalSize(root());
	}

	private int totalSize(KDTreeNode<T> node) {
		if (node.type().isLeaf()) {
			return node.objects().size();
		}
		return totalSize(node.left()) + totalSize(node.right());
	}
}
