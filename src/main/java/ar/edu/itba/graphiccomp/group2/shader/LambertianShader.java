package ar.edu.itba.graphiccomp.group2.shader;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.scene.Geometry;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class LambertianShader implements Shader {

	@Override
	public Vector3d apply(Vector3d diffuse, Vector3d specular, CollisionContext collision, Vector3d I, Vector3d eye) {
		final Geometry geometry = collision.geometry();
		Vector3d N = collision.normal();
		Vector3d L = I;
		double LdotN = Math.max(0, N.dot(L));
		if (LdotN > 0) {
			Vector3d result = new Vector3d();
			Vector3d C = geometry.uvmapping(collision.localPosition(), collision.index());
			result.x = LdotN * C.x * diffuse.x;
			result.y = LdotN * C.y * diffuse.y;
			result.z = LdotN * C.z * diffuse.z;
			return result;
		}
		return Vector3ds.zero;
	}
}
