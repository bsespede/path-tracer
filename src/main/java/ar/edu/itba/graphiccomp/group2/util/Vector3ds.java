package ar.edu.itba.graphiccomp.group2.util;

import java.util.List;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import ar.edu.itba.graphiccomp.group2.math.MathUtil;

public class Vector3ds {

	public static final Vector3d zero = new Vector3d();

	public static Vector3d constant(double d) {
		return new Vector3d(d, d, d);
	}

	public static double distance(Vector3d v1, Vector3d v2) {
		return Math.sqrt(distanceSq(v1, v2));
	}

	public static double distanceSq(Vector3d v1, Vector3d v2) {
		double dx = v1.x - v2.x;
		double dy = v1.y - v2.y;
		double dz = v1.z - v2.z;
		return lenghtSq(dx, dy, dz);
	}

	public static double lenghtSq(double x, double y, double z) {
		return x * x + y * y + z * z;
	}

	public static Vector3d scaleSelf(Vector3d v, double scale) {
		v.scale(scale);
		return v;
	}

	public static Vector3d scale(Vector3d v, double scale) {
		Vector3d result = new Vector3d(v);
		result.scale(scale);
		return result;
	}

	public static Vector3d mult(Vector3d v1, Vector3d v2) {
		v1.x *= v2.x;
		v1.y *= v2.y;
		v1.z *= v2.z;
		return v1;
	}

	public static Vector3d mult(Vector3d v1, Vector3d v2, double scale) {
		v1.x *= v2.x * scale;
		v1.y *= v2.y * scale;
		v1.z *= v2.z * scale;
		return v1;
	}

	public static Vector3d div(Vector3d v1, Vector3d v2) {
		v1.x /= v2.x;
		v1.y /= v2.y;
		v1.z /= v2.z;
		return v1;
	}

	public static Vector3d add(Vector3d v1, Vector3d v2) {
		v1.x += v2.x;
		v1.y += v2.y;
		v1.z += v2.z;
		return v1;
	}

	public static Vector3d add(Vector3d v1, Vector3d v2, double scale) {
		v1.x += v2.x * scale;
		v1.y += v2.y * scale;
		v1.z += v2.z * scale;
		return v1;
	}

	public static Vector3d add(Vector3d v1, double x, double y, double z) {
		v1.x += x;
		v1.y += y;
		v1.z += z;
		return v1;
	}

	public static float min(Vector3f pixel) {
		return Math.min(pixel.x, Math.min(pixel.y, pixel.z));
	}

	public static float max(Vector3f pixel) {
		return Math.max(pixel.x, Math.max(pixel.y, pixel.z));
	}

	public static boolean isPositive(Vector3d v) {
		return v.x > 0 && v.y > 0 && v.z > 0;
	}

	public static Vector3d lerp(Vector3d start, Vector3d end, double t) {
		return lerp(start, end, t, new Vector3d());
	}

	public static Vector3d lerp(Vector3d start, Vector3d end, double t, Vector3d result) {
		Preconditions.checkIsTrue(0 <= t && t <= 1);
		result.x = start.x * (1 - t) + end.x * t;
		result.y = start.y * (1 - t) + end.y * t;
		result.z = start.z * (1 - t) + end.z * t;
		return result;
	}

	public static Vector3d lerpAdd(Vector3d start, Vector3d end, double t, Vector3d result) {
		Preconditions.checkIsTrue(0 <= t && t <= 1);
		result.x += start.x * (1 - t) + end.x * t;
		result.y += start.y * (1 - t) + end.y * t;
		result.z += start.z * (1 - t) + end.z * t;
		return result;
	}

	/**
	 * @param d
	 *            Apunta al punto de colision
	 * @param n
	 *            Vector que sale del punto de colision
	 * @param result
	 *            Vector que sale desde el punto de colision y reflejado sobre n usando d
	 */
	public static void reflect(Vector3d d, Vector3d n, Vector3d result) {
		result.set(n);
		result.scale(-2 * d.dot(n));
		result.add(d);
	}

	/**
	 * @param a
	 *            <b>MUST</b> be a versor
	 * @param b
	 *            <b>MUST</b> be a versor
	 * @param cosTheta
	 *            <b>MUST</b> be the cos of the angle between a and b (radians)
	 */
	public static Vector3d rejection(Vector3d a, Vector3d b, double cosTheta, Vector3d result) {
		result.set(b);
		result.scale(-cosTheta);
		result.add(a);
		return result;
	}

	public static Vector3d parse(List<String> values) {
		return new Vector3d(Double.parseDouble(values.get(0)), Double.parseDouble(values.get(1)), Double.parseDouble(values.get(2)));
	}

	public static Vector3d parse(List<String> values, int offset) {
		return new Vector3d(
			Double.parseDouble(values.get(0 + offset)),
			Double.parseDouble(values.get(1 + offset)),
			Double.parseDouble(values.get(2 + offset))
		);
	}

	public static boolean isZero(Vector3d position) {
		return position.x == 0 && position.y == 0 && position.z == 0;
	}

	public static double dot(Vector3d v, double x, double y, double z) {
		return v.x * x + v.y * y + v.z * z;
	}

	/**
	 * Returns the projection of "a" vector into "b"
	 */
	public static Vector3d project(Vector3d a, Vector3d b, double scale) {
		Vector3d result = new Vector3d(a);
		result.scale(a.dot(b) / a.length() * b.length());
		result.scale(scale);
		return result;
	}

	public static Vector3d abs(Vector3d v) {
		v.x = Math.abs(v.x);
		v.y = Math.abs(v.y);
		v.z = Math.abs(v.z);
		return v;
	}

	public static Vector3d avg(Vector3d v1, Vector3d v2) {
		Vector3d avg = new Vector3d(v1);
		avg.add(v2);
		avg.scale(0.5);
		return avg;
	}

	public static double crossLen(double x1, double y1, double z1, double x2, double y2, double z2) {
		double x, y, z;
		x = y1 * z2 - z1 * y2;
		y = x2 * z1 - z2 * x1;
		z = x1 * y2 - y1 * x2;
		return Math.sqrt(lenghtSq(x, y, z));
	}

	public static boolean equals(Vector3d v1, Vector3d v2) {
		return MathUtil.equals(v1.x, v2.x) && MathUtil.equals(v1.y, v2.y) && MathUtil.equals(v1.z, v2.z);
	}

	public static void swap(Vector3d v1, Vector3d v2) {
		double x = v1.x;
		double y = v1.y;
		double z = v1.z;
		v1.set(v2);
		v2.x = x;
		v2.y = y;
		v2.z = z;
	}

	public static Vector3d pow(Vector3d v, Vector3d exp) {
		v.x = Math.pow(v.x, exp.x);
		v.y = Math.pow(v.y, exp.y);
		v.z = Math.pow(v.z, exp.z);
		return v;
	}

	public static Vector3d perpendicular(Vector3d n, Vector3d result) {
		result.set(n.z, n.z, -n.x - n.y);
		if (MathUtil.isZero(result.x) && MathUtil.isZero(result.z)) {
			result.set(-n.y - n.z, n.x, n.x);
		}
		result.normalize();
//		Preconditions.checkIsTrue(MathUtil.isZero(result.dot(n)));
		return result;
	}

	public static Vector3d cross(Vector3d v1, Vector3d v2) {
		Vector3d result = new Vector3d();
		result.cross(v1, v2);
		return result;
	}
}
