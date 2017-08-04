package ar.edu.itba.graphiccomp.group2.materialsampler;

import static ar.edu.itba.graphiccomp.group2.math.MathUtil.sq;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.math.Line;
import ar.edu.itba.graphiccomp.group2.math.MathUtil;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class MatteMaterialSampler implements MaterialSampler {

	@Override
	public void sample(Material material, Line r, Vector3d p0, Vector3d normal, Line result) {
		result.setP0(p0);
		cosWeightedHemisphereSample(normal, result.d());
	}

	public static Vector3d cosWeightedHemisphereSample(Vector3d normal, Vector3d result) {
		final Vector3d du = Vector3ds.perpendicular(normal, new Vector3d());
		final Vector3d dv = Vector3ds.cross(du, normal);
		double angle = Math.random() * MathUtil.TWO_PI;
		double rSqrt = sqrt(Math.random());
		double ru = rSqrt * cos(angle);
		double rv = rSqrt * sin(angle);
		result.set(normal);
		result.scale(sqrt(1 - sq(ru) - sq(rv)));
		du.scale(ru);
		dv.scale(rv);
		result.add(du);
		result.add(dv);
		return result;
	}

}
