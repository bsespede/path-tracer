package ar.edu.itba.graphiccomp.group2.mesh;

import java.util.List;
import java.util.Objects;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.bounding.AABB;
import ar.edu.itba.graphiccomp.group2.bounding.BoundingVolume;
import ar.edu.itba.graphiccomp.group2.math.Box;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.Transform;
import ar.edu.itba.graphiccomp.group2.math.Triangle;
import ar.edu.itba.graphiccomp.group2.meshsample.MeshSampler;
import ar.edu.itba.graphiccomp.group2.meshsample.TriangleMeshSampler;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.tree.base.TreeStructure;
import ar.edu.itba.graphiccomp.group2.tree.kd.TriangleKDTreeBuilder;
import ar.edu.itba.graphiccomp.group2.util.Preconditions;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public final class TriangleMesh implements Mesh {

	private final Vector3d[] _vs;
	private final Integer[] _indexes;
	private final Vector3d[] _ns;

	private final Vector2d[] _uvs;
	private final TreeStructure<Integer> _tree;

	public TriangleMesh(List<Integer> indexes, List<Vector3d> vs, List<Vector3d> ns, List<Vector2d> uvs, Box bounds) {
		this(indexes.toArray(new Integer[indexes.size()]), 
			vs.toArray(new Vector3d[vs.size()]), 
			ns.toArray(new Vector3d[ns.size()]), 
			uvs.toArray(new Vector2d[uvs.size()]), 
			bounds);
	}

	public TriangleMesh(Integer[] indexes, Vector3d[] vs, Vector3d[] ns, Vector2d[] uvs, Box bounds) {
		_indexes = Objects.requireNonNull(indexes);
		_vs = Objects.requireNonNull(vs);
		_ns = Objects.requireNonNull(ns);
		_uvs = Objects.requireNonNull(uvs);
		_tree = new TriangleKDTreeBuilder(this).get();
		Preconditions.checkIsTrue(indexes.length != 0 && indexes.length % 3 == 0);
	}

	public int triangleCount() {
		return _indexes.length / 3;
	}

	public boolean intersection(Line line, CollisionContext result, int index) {
		int vIndex = 3 * index;
		Vector3d a = a2(vIndex);
		Vector3d b = b2(vIndex);
		Vector3d c = c2(vIndex);
		if (Triangle.intersects(a, b, c, line, result)) {
			computeNormals(a, b, c, result.localPosition(), vIndex, result.normal());
			result.setIndex(index);
		}
		return result.intersects();
	}

	@Override
	public boolean intersection(Line line, CollisionContext result) {
		_tree.test(line, result);
		return result.intersects();
	}

	private void computeNormals(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d f, int index, Vector3d n) {
		double f1x = p1.x - f.x;
		double f1y = p1.y - f.y;
		double f1z = p1.z - f.z;
		double f2x = p2.x - f.x;
		double f2y = p2.y - f.y;
		double f2z = p2.z - f.z;
		double f3x = p3.x - f.x;
		double f3y = p3.y - f.y;
		double f3z = p3.z - f.z;
		double a = Vector3ds.crossLen(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z, p1.x - p3.x, p1.y - p3.y, p1.z - p3.z);
		double a1 = Vector3ds.crossLen(f2x, f2y, f2z, f3x, f3y, f3z) / a;
		double a2 = Vector3ds.crossLen(f3x, f3y, f3z, f1x, f1y, f1z) / a;
		double a3 = Vector3ds.crossLen(f1x, f1y, f1z, f2x, f2y, f2z) / a;

		Vector3d n1 = n2(index);
		Vector3d n2 = n2(index + 1);
		Vector3d n3 = n2(index + 2);

		n.x = n1.x * a1 + n2.x * a2 + n3.x * a3;
		n.y = n1.y * a1 + n2.y * a2 + n3.y * a3;
		n.z = n1.z * a1 + n2.z * a2 + n3.z * a3;
	}

	@Override
	public Vector2d uvmapping(Vector3d position, Vector2d bounds, int collisionIndex, Vector2d result) {
		if (_uvs.length == 0) {
			return result;
		}
		collisionIndex *= 3;
		Vector3d a = a2(collisionIndex);
		Vector3d b = b2(collisionIndex);
		Vector3d c = c2(collisionIndex);
		Vector2d uva = _uvs[_indexes[collisionIndex]];
		Vector2d uvb = _uvs[_indexes[collisionIndex + 1]];
		Vector2d uvc = _uvs[_indexes[collisionIndex + 2]];
		uvmapping(a, b, c, position, uva, uvb, uvc, result);
		return result;
	}

	private void uvmapping(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d f, Vector2d uv1, Vector2d uv2, Vector2d uv3, Vector2d uv) {
		double f1x = p1.x - f.x;
		double f1y = p1.y - f.y;
		double f1z = p1.z - f.z;

		double f2x = p2.x - f.x;
		double f2y = p2.y - f.y;
		double f2z = p2.z - f.z;

		double f3x = p3.x - f.x;
		double f3y = p3.y - f.y;
		double f3z = p3.z - f.z;

		double a = Vector3ds.crossLen(
			p1.x - p2.x, p1.y - p2.y, p1.z - p2.z, 
			p1.x - p3.x, p1.y - p3.y, p1.z - p3.z
		);
		double a1 = Vector3ds.crossLen(f2x, f2y, f2z, f3x, f3y, f3z) / a;
		double a2 = Vector3ds.crossLen(f3x, f3y, f3z, f1x, f1y, f1z) / a;
		double a3 = Vector3ds.crossLen(f1x, f1y, f1z, f2x, f2y, f2z) / a;
		uv.x = uv1.x * a1 + uv2.x * a2 + uv3.x * a3;
		uv.y = 1 - (uv1.y * a1 + uv2.y * a2 + uv3.y * a3);
	}

	public Vector3d a(int index) {
		return _vs[_indexes[3 * index]];
	}

	public Vector3d b(int index) {
		return _vs[_indexes[3 * index + 1]];
	}

	public Vector3d c(int index) {
		return _vs[_indexes[3 * index + 2]];
	}

	public Vector3d a2(int index) {
		return _vs[_indexes[index]];
	}

	public Vector3d b2(int index) {
		return _vs[_indexes[index + 1]];
	}

	public Vector3d c2(int index) {
		return _vs[_indexes[index + 2]];
	}

	public Vector3d n2(int index) {
		return _ns[_indexes[index]];
	}

	@Override
	public BoundingVolume boundingVolume(Transform transform) {
		return new AABB(_tree.bounds().apply(transform));
	}

	@Override
	public MeshSampler sampler(Geometry geometry) {
		return new TriangleMeshSampler(geometry);
	}
}
