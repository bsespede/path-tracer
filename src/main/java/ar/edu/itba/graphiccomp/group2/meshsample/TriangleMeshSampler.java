package ar.edu.itba.graphiccomp.group2.meshsample;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.math.Triangles;
import ar.edu.itba.graphiccomp.group2.mesh.TriangleMesh;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;

public class TriangleMeshSampler implements MeshSampler {

	private Geometry _geometry;
	private int[] _triangles;
	private double[] areas;

	public TriangleMeshSampler(Geometry geometry) {
		_geometry = geometry;
		TriangleMesh triangleMesh = (TriangleMesh) geometry.mesh();
		areas = new double[triangleMesh.triangleCount()];
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < areas.length; i++) {
			areas[i] = Triangles.area(triangleMesh.a(i), triangleMesh.b(i), triangleMesh.c(i));
			min = Math.min(min, areas[i]);
		}
		int[] areasInt = new int[areas.length];
		int sum = 0;
		for (int i = 0; i < areas.length; i++) {
			areasInt[i] = (int) (areas[i] / min);
			sum += areasInt[i];
		}
		_triangles = new int[sum];
		int index = 0;
		for (int i = 0; i < areas.length; i++) {
			for (int j = 0; j < areasInt[i]; j++) {
				_triangles[index++] = i;
			}
		}
	}

	public int randomTriangle() {
		return _triangles[(int) (Math.random() * _triangles.length)];
	}

	@Override
	public Geometry geometry() {
		return _geometry;
	}

	public TriangleMesh mesh() {
		return (TriangleMesh) geometry().mesh();
	}

	public double areaOf(int triangle) {
		return areas[triangle];
	}

	@Override
	public Vector3d sample() {
		int triangle = randomTriangle();
		double e1Sqrt = Math.sqrt(Math.random());
		double u = 1 - e1Sqrt;
		double v = Math.random() * e1Sqrt;
		return Triangles.pointAt(mesh(), triangle, u, v, new Vector3d());
	}
}
