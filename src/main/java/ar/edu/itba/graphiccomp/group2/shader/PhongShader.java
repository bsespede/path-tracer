package ar.edu.itba.graphiccomp.group2.shader;

import static java.lang.Math.pow;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class PhongShader implements Shader {

	/**
	 * shininess constant for this material, which is larger for surfaces that
	 * are smoother and more mirror-like. When this constant is large the
	 * specular highlight is small.
	 */
	private double _shininess = 2;

	public PhongShader(double shininess) {
		_shininess = shininess;
	}

	@Override
	public Vector3d apply(Vector3d diffuse, Vector3d specular, CollisionContext collision, Vector3d I, Vector3d eye) {
		Material material = collision.geometry().material();
		final Vector3d result = new Vector3d();
		final double IdotN = I.dot(collision.normal());
		{
			Vector3d kd = material.kd();
			result.x = diffuse.x * kd.x * IdotN;
			result.y = diffuse.y * kd.y * IdotN;
			result.z = diffuse.z * kd.z * IdotN;
		}
		{
			Vector3d ks = material.ks();
			double r = r(I, collision.normal(), eye, _shininess, IdotN);
			result.x += specular.x * ks.x * r;
			result.y += specular.y * ks.y * r;
			result.z += specular.z * ks.z * r;
		}
		return Vector3ds.mult(result, collision.geometry().uvmapping(collision.localPosition(), collision.index()));
	}

	private double r(Vector3d I, Vector3d n, Vector3d eye, double shininess, double IdotN) {
		IdotN = 2 * IdotN;
		double x = n.x * IdotN - I.x;
		double y = n.y * IdotN - I.y;
		double z = n.z * IdotN - I.z;
		return pow(Vector3ds.dot(eye, x, y, z), shininess);
	}
}
