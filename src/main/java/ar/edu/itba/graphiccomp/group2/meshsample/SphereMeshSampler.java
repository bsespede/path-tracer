package ar.edu.itba.graphiccomp.group2.meshsample;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.math.MathUtil;
import ar.edu.itba.graphiccomp.group2.mesh.SphereMesh;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;

public class SphereMeshSampler implements MeshSampler {

	private Geometry _geometry;
	private SphereMesh _mesh;

	public SphereMeshSampler(Geometry geometry) {
		_geometry = geometry;
		_mesh = (SphereMesh) geometry.mesh();
	}

	@Override
	public Geometry geometry() {
		return _geometry;
	}

	@Override
	public Vector3d sample() {
		Vector3d sample = new Vector3d(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
		if (MathUtil.isZero(sample.x) && MathUtil.isZero(sample.y) && MathUtil.isZero(sample.z)) {
			sample.x = Math.random();
		}
		sample.normalize();
		sample.scale(_mesh.sphere().r());
		sample.add(_mesh.sphere().center());
		return sample;
	}

}
