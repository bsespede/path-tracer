package ar.edu.itba.graphiccomp.group2.materialsampler;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.MathUtil;
import ar.edu.itba.graphiccomp.group2.shader.CookTorranceShader;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class MetalMaterialSampler implements MaterialSampler {

	@Override
	public void sample(Material material, Line r, Vector3d p0, Vector3d normal, Line result) {
		Vector3d reflection = new Vector3d();
		Vector3ds.reflect(r.d(), normal, reflection);
		double e = 1 / ((CookTorranceShader) material.shader()).roughness();
		result.setP0(p0);
		cosWeightedHemisphereSample(reflection, result.d(), e);
	}

	public static Vector3d cosWeightedHemisphereSample(Vector3d normal, Vector3d result, double e) {
		double r1 = Math.random();
		double r2 = Math.random();
		double phi = MathUtil.TWO_PI * r1;
		double exp = 1 / (e + 1);
		double sigma = acos(pow(1 - r2, exp));
		double sinSigma = sin(sigma);
		double u = sinSigma * cos(phi);
		double v = sinSigma * sin(phi);
		double w = cos(sigma);
		{
			Vector3d du = Vector3ds.perpendicular(normal, new Vector3d());
			Vector3d dv = Vector3ds.cross(du, normal);
			du.scale(u);
			dv.scale(v);
			result.set(normal);
			result.scale(w);
			result.add(du);
			result.add(dv);
		}
		return result;
	}

	// public static void main(String[] args) {
	// Vector3d normal = new Vector3d(0, 1, 0);
	// Material material = new Material();
	// material.setShader(new CookTorranceShader(0.075));
	// MetalMaterialSampler sampler = new MetalMaterialSampler();
	// Vector3d p0 = new Vector3d();
	// Line r = new Line(p0, new Vector3d(1, 0, 0));
	// Line result = new Line();
	// for (int i = 0; i < 1000; i++) {
	// sampler.sample(material, r, p0, normal, result);
	// System.out.println(String.format("%.2f\t%.2f\t%.2f\t", result.d().x,
	// result.d().y, result.d().z));
	// }
	// }
}
