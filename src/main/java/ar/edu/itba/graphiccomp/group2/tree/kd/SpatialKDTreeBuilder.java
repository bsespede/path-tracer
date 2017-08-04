package ar.edu.itba.graphiccomp.group2.tree.kd;

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
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Spatial;
import ar.edu.itba.graphiccomp.group2.tree.kd.KDTree.KDTreeStategy;
import ar.edu.itba.graphiccomp.group2.tree.kd.KDTreeNode.KDPlaneNormalDir;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class SpatialKDTreeBuilder implements Supplier<KDTree<Spatial>> {

	private final List<? extends Spatial> _spatials;

	public SpatialKDTreeBuilder(List<? extends Spatial> spatials) {
		_spatials = spatials;
	}

	@Override
	public KDTree<Spatial> get() {
		KDTree<Spatial> tree = new KDTree<>(new KDSpatialTreeStategy()).setCheckRepetitions(true);
		tree.addAll(_spatials);
		System.out.println(String.format("[INFO] Loaded spatial kd tree with %d elemtns", _spatials.size()));
		return tree;
	}

	private final class KDSpatialTreeStategy implements KDTreeStategy<Spatial> {

		@Override
		public Pair<Collection<Spatial>, Collection<Spatial>> split(KDTreeNode<Spatial> node) {
			Collection<Spatial> left = new LinkedList<>();
			Collection<Spatial> right = new LinkedList<>();
			for (Spatial object : node.objects()) {
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

		private PlaneSide side(Spatial object, KDTreeNode<Spatial> node) {
			Vector3d p0 = node.plane().getLeft();
			Vector3d n = node.plane().getRight().n();
			PlaneSide sideA = Plane.sideOf(object.boundingVolume().lbb(), p0, n);
			PlaneSide sideB = Plane.sideOf(object.boundingVolume().rtf(), p0, n);
			return sideA.union(sideB);
		}

		@Override
		public Pair<Vector3d, KDPlaneNormalDir> dividerPlane(Collection<Spatial> objects) {
			final Vector3d min = Vector3ds.constant(Double.MAX_VALUE);
			final Vector3d max = Vector3ds.constant(-Double.MAX_VALUE);
			final Vector3d p0 = new Vector3d();
			for (Spatial object : objects) {
				p0.add(object.boundingVolume().center());
				Triangles.ensureBoundings(object.boundingVolume().lbb(), min, max);
				Triangles.ensureBoundings(object.boundingVolume().rtf(), min, max);
			}
			p0.scale(1f / objects.size());
			KDPlaneNormalDir axis;
			{
				double dx = max.x - min.x;
				double dy = max.y - min.y;
				double dz = max.z - min.z;
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
		public Box bounds(Collection<Spatial> objects) {
			if (objects.isEmpty()) {
				return new Box(new Vector3d(), new Vector3d(.1, .1, .1));
			}
			final Vector3d min = Vector3ds.constant(Double.MAX_VALUE);
			final Vector3d max = Vector3ds.constant(-Double.MAX_VALUE);
			for (Spatial object : objects) {
				Triangles.ensureBoundings(object.boundingVolume().lbb(), min, max);
				Triangles.ensureBoundings(object.boundingVolume().rtf(), min, max);
			}
			return Triangles.box(min, max);
		}

		@Override
		public boolean test(Spatial object, Line line, CollisionContext collision) {
			return object.trace(line, collision);
		}

		@Override
		public boolean isFull(KDTreeNode<Spatial> node) {
			return node.objects().size() > 5;
		}
	}

}
