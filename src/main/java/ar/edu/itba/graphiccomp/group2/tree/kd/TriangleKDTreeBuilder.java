package ar.edu.itba.graphiccomp.group2.tree.kd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import javax.vecmath.Vector3d;

import org.apache.commons.lang3.tuple.Pair;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Plane;
import ar.edu.itba.graphiccomp.group2.math.Plane.PlaneSide;
import ar.edu.itba.graphiccomp.group2.math.Triangles;
import ar.edu.itba.graphiccomp.group2.mesh.TriangleMesh;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.tree.kd.KDTree.KDTreeStategy;
import ar.edu.itba.graphiccomp.group2.tree.kd.KDTreeNode.KDPlaneNormalDir;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class TriangleKDTreeBuilder implements Supplier<KDTree<Integer>> {

	private final TriangleMesh _mesh;

	public TriangleKDTreeBuilder(TriangleMesh mesh) {
		_mesh = mesh;
	}

	@Override
	public KDTree<Integer> get() {
		KDTree<Integer> tree = new KDTree<>(new KDTriangleTreeStategy());
		int trisCount = _mesh.triangleCount();
		{
			List<Integer> elems = new ArrayList<>(trisCount);
			for (int i = 0; i < trisCount; i++) {
				elems.add(i);
			}
			tree.addAll(elems);
		}
		String info = String.format(
			"[INFO] Loaded triangle KDtree with %d triangles. Depth: %d. Size: %d / %d", 
			trisCount, tree.depth(), trisCount, tree.totalSize()
		);
		System.out.println(info);
		return tree;
	}

	private final class KDTriangleTreeStategy implements KDTreeStategy<Integer> {

		@Override
		public Pair<Collection<Integer>, Collection<Integer>> split(KDTreeNode<Integer> node) {
			Collection<Integer> left = new LinkedList<>();
			Collection<Integer> right = new LinkedList<>();
			for (Integer object : node.objects()) {
				PlaneSide side = side(object, node);
				if (side == PlaneSide.BACK) {
					left.add(object);
				} else if (side == PlaneSide.FRONT) {
					right.add(object);
				} else {
					left.add(object);
					right.add(object);
				}
			}
			return Pair.of(left, right);
		}

		private PlaneSide side(Integer object, KDTreeNode<Integer> node) {
			Vector3d p0 = node.plane().getLeft();
			Vector3d n = node.plane().getRight().n();
			Vector3d middle = new Vector3d();
			middle.add(_mesh.a(object));
			middle.add(_mesh.b(object));
			middle.add(_mesh.c(object));
			middle.scale(1 / 3f);
			return Plane.sideOf(middle, p0, n);
//			PlaneSide sideA = Plane.sideOf(_mesh.a(object), p0, n);
//			PlaneSide sideB = Plane.sideOf(_mesh.b(object), p0, n);
//			PlaneSide sideC = Plane.sideOf(_mesh.c(object), p0, n);
//			return sideA.union(sideB).union(sideC);
		}

		@Override
		public Pair<Vector3d, KDPlaneNormalDir> dividerPlane(Collection<Integer> objects) {
			final Vector3d min = Vector3ds.constant(Double.MAX_VALUE);
			final Vector3d max = Vector3ds.constant(-Double.MAX_VALUE);
			final Vector3d p0 = new Vector3d();
			final Vector3d tmp = new Vector3d();
			for (int t : objects) {
				final Vector3d a = _mesh.a(t);
				final Vector3d b = _mesh.b(t);
				final Vector3d c = _mesh.c(t);
				{
					Triangles.ensureBoundings(a, min, max);
					Triangles.ensureBoundings(b, min, max);
					Triangles.ensureBoundings(c, min, max);
				}
				{
					tmp.set(Vector3ds.zero);
					tmp.add(a);
					tmp.add(b);
					tmp.add(c);
					tmp.scale(1f / 3);
					p0.add(tmp);
				}
			}
			p0.scale(1f / objects.size());
			KDPlaneNormalDir axis;
			{
				double dx = max.x - min.x;
				double dy = max.y - min.y;
				double dz = max.z - min.z;
				// Preconditions.checkIsTrue(dx >= 0);
				// Preconditions.checkIsTrue(dy >= 0);
				// Preconditions.checkIsTrue(dz >= 0);
				if (dx > dy && dx > dz) {
					axis = KDPlaneNormalDir.X;
				} else if (dy > dz) {
					axis = KDPlaneNormalDir.Y;
				} else {
					axis = KDPlaneNormalDir.Z;
				}
			}
			return Pair.of(p0, axis);
		}

		@Override
		public Box bounds(Collection<Integer> objects) {
			if (objects.isEmpty()) {
				return new Box(new Vector3d(), new Vector3d(.1, .1, .1));
			}
			final Vector3d min = Vector3ds.constant(Double.MAX_VALUE);
			final Vector3d max = Vector3ds.constant(-Double.MAX_VALUE);
			for (int t : objects) {
				Triangles.ensureBoundings(_mesh.a(t), min, max);
				Triangles.ensureBoundings(_mesh.b(t), min, max);
				Triangles.ensureBoundings(_mesh.c(t), min, max);
			}
			return Triangles.box(min, max);
		}

		@Override
		public boolean test(Integer object, Line line, CollisionContext collision) {
			return _mesh.intersection(line, collision, object);
		}

		@Override
		public boolean isFull(KDTreeNode<Integer> node) {
			return node.objects().size() > 100;
		}
	}

}
