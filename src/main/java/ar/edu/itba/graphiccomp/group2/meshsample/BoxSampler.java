package ar.edu.itba.graphiccomp.group2.meshsample;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.math.FinitePlane;
import ar.edu.itba.graphiccomp.group2.mesh.BoxMesh;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;

public class BoxSampler implements MeshSampler {

	private Geometry _geometry;
	private BoxMesh _mesh;

	public BoxSampler(Geometry geometry) {
		_geometry = geometry;
		_mesh = (BoxMesh) geometry.mesh();
	}

	@Override
	public Geometry geometry() {
		return _geometry;
	}

	@Override
	public Vector3d sample() {
		FinitePlane face = _mesh.box().face((int) (Math.random() * 4));
		double ru = (Math.random() - 0.5) * face.uLength();
		double rv = (Math.random() - 0.5) * face.vLength();
		Vector3d u = face.u();
		Vector3d v = face.v();
		Vector3d sample = new Vector3d(face.infPlane().p0());
		sample.x += u.x * ru + v.x * rv;
		sample.y += u.y * ru + v.y * rv;
		sample.z += u.z * ru + v.z * rv;
		return sample;
	}

}
