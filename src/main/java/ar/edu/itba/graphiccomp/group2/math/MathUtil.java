package ar.edu.itba.graphiccomp.group2.math;

import javax.vecmath.Vector3d;

public class MathUtil {

	public static final double TWO_PI = Math.PI * 2;
	public static final double HALF_PI = Math.PI / 2;
	public static final double EPS = 0.0015;

	public static boolean isZero(double d1) {
		return equals(d1, 0);
	}

	public static boolean equals(double d1, double d2) {
		return Math.abs(d1 - d2) < EPS;
	}

	public static boolean equals(double d1, double d2, double eps) {
		return Math.abs(d1 - d2) < eps;
	}

	public static double distance(Vector3d v1, Vector3d v2) {
		return Math.sqrt(distanceSq(v1, v2));
	}

	public static double distanceSq(Vector3d v1, Vector3d v2) {
		double dx = v1.x - v2.x;
		double dy = v1.y - v2.y;
		double dz = v1.z - v2.z;
		return dx * dx + dy * dy + dz * dz;
	}

	public static double clamp(double v, double min, double max) {
		return Math.max(min, Math.min(max, v));
	}

	public static double sq(double d) {
		return d * d;
	}

	public static double max(double v1, double v2, double v3) {
		return Math.max(v1, Math.max(v2, v3));
	}

	public static double min(double v1, double v2, double v3) {
		return Math.min(v1, Math.min(v2, v3));
	}

	public static boolean isInt(String s) {
		if (s == null) {
			return false;
		}
		try {
			Integer.valueOf(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
