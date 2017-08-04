package ar.edu.itba.graphiccomp.group2.light;

import static java.lang.Math.max;
import static java.lang.Math.pow;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.util.Preconditions;

public class SpotLight extends PointLight {

	private final Vector3d _d = new Vector3d();
	private final double _spotExp;
	private final double _cosAngle;

	public SpotLight(Vector3d p0, Vector3d d, double angle, double spotExp) {
		super(p0);
		_d.set(d);
		_d.normalize();
		_spotExp = spotExp;
		_cosAngle = Math.cos(angle);
		Preconditions.checkIsTrue(0 < spotExp, "Angle out of bounds");
	}

	@Override
	protected Vector3d decay(Vector3d result, Vector3d position, Vector3d direction, double distanceSq) {
		double cosAngle = -_d.dot(direction);
		if (cosAngle < _cosAngle) {
			return super.decay(result, position, direction, distanceSq);
		}
		double spotEffect = max(pow(cosAngle, _spotExp), 0);
		result.scale(spotEffect);
		return super.decay(result, position, direction, distanceSq);
	}
}
