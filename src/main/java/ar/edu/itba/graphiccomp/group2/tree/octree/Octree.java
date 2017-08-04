package ar.edu.itba.graphiccomp.group2.tree.octree;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.tree.base.TreeStructure;
import ar.edu.itba.graphiccomp.group2.tree.octree.OctreeNode.NodeType;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class Octree<T> implements TreeStructure<T> {

	private final OctreeNode<T> _root;
	private final NodeStrategy<T> _strategy;
	private final int _splitSize;
	private final int _maxDepth;
	private boolean _checkRepeated = false;

	public Octree(Box bounds, NodeStrategy<T> strategy) {
		this(bounds, strategy, 20, 4);
	}

	public Octree(Box bounds, NodeStrategy<T> strategy, int splitSize, int maxDepth) {
		_root = new OctreeNode<T>(0, bounds, this);
		_strategy = Objects.requireNonNull(strategy);
		_splitSize = splitSize;
		_maxDepth = maxDepth;
	}

	public int splitSize() {
		return _splitSize;
	}

	public int maxDepth() {
		return _maxDepth;
	}

	public NodeStrategy<T> strategy() {
		return _strategy;
	}

	public OctreeNode<T> root() {
		return _root;
	}

	@Override
	public Box bounds() {
		return root().bounds();
	}

	public Octree<T> checkRepeated() {
		_checkRepeated = true;
		return this;
	}

	@Override
	public Octree<T> addAll(Collection<? extends T> objects) {
		objects.stream().forEach(elem -> Preconditions.checkIsTrue(root().add(elem)));
		return this;
	}

	@Override
	public void test(Line line, CollisionContext collision) {
		sortNodesMethod(line, collision);
		// recursiveSearchMethod(root(), line, collision);
	}

	private void sortNodesMethod(Line line, CollisionContext collision) {
		PriorityQueue<OctreeNode<T>> queue = new PriorityQueue<OctreeNode<T>>(new Comparator<OctreeNode<T>>() {
			@Override
			public int compare(OctreeNode<T> o1, OctreeNode<T> o2) {
				double d1Sq = Vector3ds.distanceSq(line.p0(), o1.bounds().center());
				double d2Sq = Vector3ds.distanceSq(line.p0(), o2.bounds().center());
				double diff = d1Sq - d2Sq;
				return diff < 0 ? -1 : (diff > 0 ? 1 : 0);
			}
		});
		intersectingNodes(root(), line, collision, queue);
		if (!queue.isEmpty()) {
			Set<T> checked = _checkRepeated && queue.size() > 1 ? new HashSet<>() : null;
			CollisionContext testCollision = new CollisionContext();
			do {
				OctreeNode<T> node = queue.poll();
				if (collision.intersects()) {
					double d1 = collision.collisionDistanceSq();
					double d2 = node.bounds().distanceSq(line.p0());
					if (d2 >= d1) {
						return;
					}
				}
				for (T object : node.objects()) {
					if (checked != null && !checked.add(object)) {
						continue;
					}
					if (_strategy.test(object, line, testCollision)) {
						collision.updateIfCollisionIsCloser(line, testCollision);
						testCollision.restart();
					}
				}
			} while (!queue.isEmpty());
		}
	}

	private void intersectingNodes(OctreeNode<T> node, Line line, CollisionContext collision, PriorityQueue<OctreeNode<T>> queue) {
		if (node.type().equals(NodeType.LEAF) && node.size() == 0) {
			return;
		}
		if (node.bounds().intersects(line)) {
			switch (node.type()) {
			case INTERNAL:
				for (OctreeNode<T> child : node.children()) {
					intersectingNodes(child, line, collision, queue);
				}
				break;
			case LEAF:
				queue.add(node);
				break;
			}
		}
	}

	public void fillDepth(int depth) {
		fillDepth(0, depth, root());
	}

	public void fillDepth(int depth, int maxDepth, OctreeNode<T> node) {
		if (depth >= maxDepth) {
			return;
		}
		node.split();
		if (node.type().equals(NodeType.INTERNAL)) {
			for (OctreeNode<T> child : node.children()) {
				fillDepth(depth + 1, maxDepth, child);
			}
		}
	}

	public int sizeDiff() {
		Set<T> elements = new HashSet<>();
		sizeDiff(root(), elements);
		return elements.size();
	}

	public void sizeDiff(OctreeNode<T> node, Set<T> elements) {
		if (node.type().equals(NodeType.LEAF)) {
			elements.addAll(node.objects());
		} else {
			for (int i = 0; i < OctreeNode.CHILDREN_COUNT; i++) {
				sizeDiff(node.child(i), elements);
			}
		}
	}

	public int totalSize() {
		return totalSize(root());
	}

	private int totalSize(OctreeNode<T> node) {
		if (node.type().equals(NodeType.LEAF)) {
			return node.objects().size();
		} else {
			int sum = 0;
			for (int i = 0; i < OctreeNode.CHILDREN_COUNT; i++) {
				sum += totalSize(node.child(i));
			}
			return sum;
		}
	}

	public int depth() {
		return depth(root());
	}

	private int depth(OctreeNode<T> node) {
		if (node.type().equals(NodeType.LEAF)) {
			return 0;
		} else {
			int maxDepth = 0;
			for (int i = 0; i < OctreeNode.CHILDREN_COUNT; i++) {
				maxDepth = Math.max(maxDepth, depth(node.child(i)));
			}
			return 1 + maxDepth;
		}
	}

	@Override
	public void traverse(Consumer<T> consumer) {
		traverse(consumer, root(), new HashSet<T>());
	}

	private void traverse(Consumer<T> consumer, OctreeNode<T> node, Set<T> visited) {
		if (node.isLeaf()) {
			for (T elem : node.objects()) {
				if (!visited.contains(elem)) {
					consumer.accept(elem);
					visited.add(elem);
				}
			}
		} else {
			for (OctreeNode<T> child : node.children()) {
				traverse(consumer, child, visited);
			}
		}
	}
}
