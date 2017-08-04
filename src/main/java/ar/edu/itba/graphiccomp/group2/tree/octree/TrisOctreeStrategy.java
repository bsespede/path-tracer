package ar.edu.itba.graphiccomp.group2.tree.octree;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Triangles;
import ar.edu.itba.graphiccomp.group2.mesh.TriangleMesh;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public class TrisOctreeStrategy implements NodeStrategy<Integer> {

	public static Octree<Integer> build(TriangleMesh mesh, Box bounds) {
		int trisCount = mesh.triangleCount();
		int maxdepth = (int) (Math.log10(trisCount) + 2);
		Octree<Integer> tree = new Octree<>(bounds, new TrisOctreeStrategy(mesh), 50, maxdepth);
		//tree.fillDepth((int) (Math.log10(trisCount) / 2));
		List<Integer> elems = new ArrayList<Integer>(trisCount);
		for (int i = 0; i < trisCount; i++) {
			elems.add(i);
		}
		tree.addAll(elems);
		Preconditions.checkIsTrue(tree.sizeDiff() == trisCount);
		String info = String.format(
			"[INFO] Loaded triangle octree with %d triangles. Depth: %d / %d. Total Size: %s", 
			trisCount, tree.depth(), maxdepth, tree.totalSize()
		);
		System.out.println(info);
		return tree;
	}

	private TriangleMesh _mesh;

	public TrisOctreeStrategy(TriangleMesh mesh) {
		_mesh = mesh;
	}

	@Override
	public boolean test(Integer object, Line line, CollisionContext collision) {
		return _mesh.intersection(line, collision, object);
	}

	@Override
	public boolean test(Integer object, Box bounds) {
		Vector3d a = _mesh.a(object);
		Vector3d b = _mesh.b(object);
		Vector3d c = _mesh.c(object);
		return Triangles.test(a, b, c, bounds);
	}

}
